package org.isheihei.redis.core.obj.impl;

import org.isheihei.redis.core.obj.AbstractRedisObject;
import org.isheihei.redis.core.obj.RedisObjectType;
import org.isheihei.redis.core.struct.RedisDataStruct;
import org.isheihei.redis.core.struct.RedisDataStructType;

/**
 * @ClassName: RedisListObject
 * @Description: Redis列表对象
 * @Date: 2022/5/31 13:05
 * @Author: isheihei
 */
public class RedisListObject extends AbstractRedisObject {

    public RedisListObject(RedisDataStructType encoding, RedisObjectType type, RedisDataStruct data) {
        super(encoding, type, data);
    }
}
