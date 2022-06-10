package org.isheihei.redis.core.db;

import org.isheihei.redis.core.obj.RedisObject;
import org.isheihei.redis.core.struct.impl.BytesWrapper;

import java.util.HashMap;
import java.util.List;
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
        dict.put(key, redisObject);
    }

    @Override
    public RedisObject get(BytesWrapper key) {
        RedisObject redisObject = dict.get(key);
        if (redisObject == null) {
            return null;
        } else {
            return redisObject;
        }
    }

    @Override
    public long remove(List<BytesWrapper> keys) {
        return keys.stream().peek(dict::remove).count();
    }

    @Override
    public void cleanAll() {
        dict.clear();
    }
}
