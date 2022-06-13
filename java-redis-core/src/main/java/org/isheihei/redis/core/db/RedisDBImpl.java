package org.isheihei.redis.core.db;

import org.isheihei.redis.core.obj.RedisObject;
import org.isheihei.redis.core.persist.aof.Aof;
import org.isheihei.redis.core.struct.impl.BytesWrapper;

import java.util.HashMap;
import java.util.Map;
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

    // TODO
    private Aof aof;

    //  过期字典
    private final Map<BytesWrapper, Long> expires = new HashMap<>();

    @Override
    public Set<BytesWrapper> keys() {
        return dict.keySet();
    }

    @Override
    public boolean exist(BytesWrapper key) {
        return dict.containsKey(key);
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
        } else if (expired(key)){
            expires.remove(key);
            dict.remove(key);
            // TODO aof 追加一条删除命令
            return null;
        } else {
            return redisObject;
        }
    }

    @Override
    public int expire(BytesWrapper key, long expireTime) {
        RedisObject redisObject = this.get(key);
        if (redisObject == null) {
            return 0;
        } else {
            expires.put(key, expireTime);
            return 1;
        }
    }

    /**
     * @Description: 一个键是否已经过期 过期返回 true
     * @Param: key
     * @Return: int
     * @Author: isheihei
     */
    private boolean expired(BytesWrapper key) {
        if (expires.get(key) == null) {
            return false;
        } else {
            return expires.get(key) < System.currentTimeMillis();
        }
    }

    @Override
    public int persist(BytesWrapper key) {
        if (expires.get(key) == null) {
            return 0;
        } else {
            expires.remove(key);
            return 1;
        }
    }

    @Override
    public void cleanAll() {
        dict.clear();
    }
}
