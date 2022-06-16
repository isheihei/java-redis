package org.isheihei.redis.core.db;

import org.isheihei.redis.core.obj.RedisObject;
import org.isheihei.redis.core.struct.impl.BytesWrapper;

import java.util.HashMap;
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
//    private final ConcurrentHashMap<RedisDynamicString, RedisObject> dict = new ConcurrentHashMap<>();

    //  过期字典
    private final Map<BytesWrapper, Long> expires = new HashMap<>();

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
    public void put(BytesWrapper key, RedisObject redisObject) {
        expires.remove(key);
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
            // TODO aof 追加一条删除命令
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
            redisObject.refreshLru();
            redisObject.updateLfu();
            expires.put(key, expireTime);
            return 1;
        }
    }

    @Override
    public Long getTtl(BytesWrapper key) {
        if (expires.containsKey(key)) {
            return expires.get(key);
        } else {
            return null;
        }
    }

    @Override
    public int persist(BytesWrapper key) {
        if (expires.get(key) == null) {
            return 0;
        } else {
            RedisObject redisObject = dict.get(key);
            if (redisObject != null) {
                redisObject.refreshLru();
                redisObject.updateLfu();
            }
            expires.remove(key);
            return 1;
        }
    }

    @Override
    public boolean isExpired(BytesWrapper key) {
        if (expires.containsKey(key)) {
            if (expires.get(key) < System.currentTimeMillis()) {
                return true;
            }
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
            expires.remove(key);
            dict.remove(key);
    }

    @Override
    public void cleanAll() {
        dict.clear();
    }
}
