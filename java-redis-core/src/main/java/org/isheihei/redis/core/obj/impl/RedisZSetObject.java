package org.isheihei.redis.core.obj.impl;

import io.netty.buffer.ByteBuf;
import org.isheihei.redis.core.obj.AbstractRedisObject;
import org.isheihei.redis.core.struct.RedisDataStruct;
import org.isheihei.redis.core.struct.RedisDataStructType;

/**
 * @ClassName: RedisZSetObject
 * @Description: Redis有序集合对象
 * @Date: 2022/5/31 13:08
 * @Author: isheihei
 */
public class RedisZSetObject extends AbstractRedisObject {

    private RedisDataStruct zset;

    public RedisZSetObject() {
        setEncoding(RedisDataStructType.REDIS_Z_SET);
        zset = getEncoding().getSupplier().get();
    }

    public RedisZSetObject(RedisDataStructType encoding) {
        super(encoding);
        zset = getEncoding().getSupplier().get();
    }

    @Override
    public byte getType() {
        return (byte) 4;
    }

    @Override
    public RedisDataStruct data() {
        return zset;
    }

    @Override
    public byte[] objectToBytes() {
        return new byte[0];
    }

    @Override
    public void loadRdb(ByteBuf bufferPolled) {

    }
}
