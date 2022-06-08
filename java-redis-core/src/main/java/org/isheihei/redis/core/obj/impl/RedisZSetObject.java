package org.isheihei.redis.core.obj.impl;

import org.isheihei.redis.core.obj.AbstractRedisObject;
import org.isheihei.redis.core.obj.RedisObjectType;
import org.isheihei.redis.core.struct.RedisDataStruct;
import org.isheihei.redis.core.struct.RedisDataStructType;

/**
 * @ClassName: RedisZSetObject
 * @Description: Redis有序集合对象
 * @Date: 2022/5/31 13:08
 * @Author: isheihei
 */
public class RedisZSetObject extends AbstractRedisObject {

    public RedisZSetObject(RedisDataStructType encoding, RedisObjectType type, RedisDataStruct data) {
        super(encoding, type, data);
    }
}
