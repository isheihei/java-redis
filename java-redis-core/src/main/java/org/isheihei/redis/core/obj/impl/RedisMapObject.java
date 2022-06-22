package org.isheihei.redis.core.obj.impl;

import io.netty.buffer.ByteBuf;
import org.isheihei.redis.core.obj.AbstractRedisObject;
import org.isheihei.redis.core.struct.RedisDataStruct;
import org.isheihei.redis.core.struct.RedisDataStructType;

/**
 * @ClassName: RedisMapObject
 * @Description: Redis字典对象
 * @Date: 2022/5/31 13:06
 * @Author: isheihei
 */
public class RedisMapObject extends AbstractRedisObject {

    private RedisDataStruct map;

    public RedisMapObject() {
        setEncoding(RedisDataStructType.REDIS_MAP);
        map = getEncoding().getSupplier().get();
    }

    public RedisMapObject(RedisDataStructType encoding) {
        super(encoding);
        map = getEncoding().getSupplier().get();
    }

    @Override
    public String getType() {
        return "hash";
    }

    @Override
    public byte getCode() {
        return (byte) 1;
    }

    @Override
    public RedisDataStruct data() {
        return map;
    }

    @Override
    public byte[] objectToBytes() {
        return new byte[0];
    }

    @Override
    public void loadRdb(ByteBuf bufferPolled) {

    }


}
