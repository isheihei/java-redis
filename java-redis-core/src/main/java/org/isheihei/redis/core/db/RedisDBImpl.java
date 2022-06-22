package org.isheihei.redis.core.db;

import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.obj.RedisObject;
import org.isheihei.redis.core.struct.impl.BytesWrapper;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * @ClassName: RedisDB
 * @Description: 数据库实现类
 * @Date: 2022/5/31 14:54
 * @Author: isheihei
 */
public class RedisDBImpl implements RedisDB {
    private final Map<BytesWrapper, RedisObject> dict = new HashMap<>();
//    private final ConcurrentHashMap<RedisString, RedisObject> dict = new ConcurrentHashMap<>();

    //  过期字典
    private final Map<BytesWrapper, Long> expires = new HashMap<>();

    // watch_keys
    private final Map<BytesWrapper, List<RedisClient>> watchKeys = new HashMap<>();

    // 距离上一次 save 或 bgsave 后服务器进行了多少修改
    private long dirty = 0;

    @Override
    public Set<BytesWrapper> keys() {
        return dict.keySet();
    }

    @Override
    public int size() {
        return dict.size();
    }

    @Override
    public Map<BytesWrapper, RedisObject> dict() {
        return dict;
    }

    @Override
    public Map<BytesWrapper, Long> expires() {
        return expires;
    }

    @Override
    public void touchWatchKey(BytesWrapper key) {
        if (watchKeys.containsKey(key)) {
            for (RedisClient client : watchKeys.get(key)) {
                client.setDirtyCas(true);
            }
        }
    }

    @Override
    public void watchKeys(List<BytesWrapper> keys, RedisClient redisClient) {
        keys.forEach(key -> {
            if (watchKeys.containsKey(key)) {
                watchKeys.get(key).add(redisClient);
            } else {
                LinkedList<RedisClient> clients = new LinkedList<>();
                clients.add(redisClient);
                watchKeys.put(key, clients);
            }
        });
    }

    @Override
    public void unWatchKeys(RedisClient redisClient) {
        for (Map.Entry<BytesWrapper, List<RedisClient>> next : watchKeys.entrySet()) {
            next.getValue().remove(redisClient);
        }
    }


    @Override
    public boolean exist(BytesWrapper key) {
        RedisObject redisObject = dict.get(key);
        if (redisObject == null) {
            return false;
        } else {
            redisObject.refreshLru();
            redisObject.updateLfu();
            return true;
        }
    }

    @Override
    public int exist(List<BytesWrapper> keyList) {
        int res = 0;
        for (BytesWrapper key : keyList) {
            RedisObject redisObject = dict.get(key);
            if (redisObject != null) {
                redisObject.refreshLru();
                redisObject.updateLfu();
                res++;
            }
        }
        return res;
    }

    @Override
    public void put(BytesWrapper key, RedisObject redisObject) {
        expires.remove(key);
        dirty++;
        dict.put(key, redisObject);
    }

    @Override
    public RedisObject get(BytesWrapper key) {
        RedisObject redisObject = dict.get(key);
        if (redisObject == null) {
            return null;
        } else if (isExpired(key)){
            expires.remove(key);
            dict.remove(key);
            // TODO aof 应该追加一条删除命令
            return null;
        } else {
            redisObject.refreshLru();
            redisObject.updateLfu();
            return redisObject;
        }
    }

    @Override
    public BytesWrapper getRandomKey() {
        Random random = new Random();
        int randomIndex = random.nextInt(size());

        Set<BytesWrapper> keySet = dict.keySet();
        return keySet.stream().skip(randomIndex).findFirst().get();
    }

    @Override
    public int expire(BytesWrapper key, long expireTime) {
        RedisObject redisObject = get(key);
        if (redisObject == null || expireTime == 0) {
            return 0;
        } else {
            dirty++;
            redisObject.refreshLru();
            redisObject.updateLfu();
            expires.put(key, expireTime);
            return 1;
        }
    }

    @Override
    public Long getTtl(BytesWrapper key) {
        return expires.get(key);
    }

    @Override
    public int persist(BytesWrapper key) {
        if (expires.get(key) == null) {
            return 0;
        } else {
            RedisObject redisObject = dict.get(key);
            if (redisObject != null) {
                dirty++;
                redisObject.refreshLru();
                redisObject.updateLfu();
                expires.remove(key);
                return 1;
            } else {
                return 0;
            }
        }
    }

    @Override
    public boolean isExpired(BytesWrapper key) {
        if (expires.containsKey(key)) {
            return expires.get(key) < System.currentTimeMillis();
        }
        return false;
    }

    @Override
    public int expiresSize() {
        return expires.size();
    }

    @Override
    public BytesWrapper getRandomExpires() {
        Random random = new Random();
        int randomIndex = random.nextInt(expires.size());

        Set<BytesWrapper> keySet = expires.keySet();
        return keySet.stream().skip(randomIndex).findFirst().get();
    }

    @Override
    public void delete(BytesWrapper key) {
        if (dict.remove(key) != null) {
            dirty++;
            expires.remove(key);
        }
    }

    @Override
    public int delete(List<BytesWrapper> keyList) {
        int res = 0;
        for (BytesWrapper key : keyList) {
            if (dict.remove(key) != null) {
                expires.remove(key);
                dirty++;
                res++;
            }
        }
        return res;
    }

    @Override
    public long getDirty() {
        return dirty;
    }

    @Override
    public void plusDirty() {
        dirty++;
    }

    @Override
    public void plusDirty(int plus) {
        dirty += plus;
    }

    @Override
    public void resetDirty() {
        dirty = 0;
    }

    @Override
    public void flushDb() {
        dirty += dict.size();
        for (Map.Entry<BytesWrapper, List<RedisClient>> bytesWrapperListEntry : watchKeys.entrySet()) {
            bytesWrapperListEntry.getValue().forEach(redisClient -> redisClient.setDirtyCas(true));
        }
        plusDirty(dict.size());
        dict.clear();
        expires.clear();
    }

    @Override
    public boolean reName(BytesWrapper oldKey, BytesWrapper newKey) {
        RedisObject redisObject = dict.get(oldKey);
        if (redisObject != null) {
            if (dict.containsKey(newKey)) {
                expires.remove(newKey);
            }
            dict.remove(oldKey);
            expires.remove(oldKey);
            dirty++;
            redisObject.refreshLru();
            redisObject.updateLfu();
            dict.put(newKey, redisObject);
            return true;
        } else {
            return false;
        }
    }
}
