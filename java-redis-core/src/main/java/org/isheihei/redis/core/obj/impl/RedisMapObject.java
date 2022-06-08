package org.isheihei.redis.core.obj.impl;

import org.isheihei.redis.core.obj.AbstractRedisObject;
import org.isheihei.redis.core.obj.RedisObjectType;
import org.isheihei.redis.core.struct.RedisDataStruct;
import org.isheihei.redis.core.struct.RedisDataStructType;

/**
 * @ClassName: RedisMapObject
 * @Description: Redis字典对象
 * @Date: 2022/5/31 13:06
 * @Author: isheihei
 */
public class RedisMapObject extends AbstractRedisObject {

    public RedisMapObject(RedisDataStructType encoding, RedisObjectType type, RedisDataStruct data) {
        super(encoding, type, data);
    }
}
