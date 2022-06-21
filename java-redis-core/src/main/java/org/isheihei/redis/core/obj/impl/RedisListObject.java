package org.isheihei.redis.core.obj.impl;

import io.netty.buffer.ByteBuf;
import org.isheihei.redis.core.obj.AbstractRedisObject;
import org.isheihei.redis.core.struct.RedisDataStruct;
import org.isheihei.redis.core.struct.RedisDataStructType;

/**
 * @ClassName: RedisListObject
 * @Description: Redis列表对象
 * @Date: 2022/5/31 13:05
 * @Author: isheihei
 */
public class RedisListObject extends AbstractRedisObject {
    private RedisDataStruct list;
    public RedisListObject() {
        setEncoding(RedisDataStructType.REDIS_DOUBLE_LINKED_LIST);
        list = getEncoding().getSupplier().get();
    }

    public RedisListObject(RedisDataStructType encoding) {
        super(encoding);
        list = getEncoding().getSupplier().get();
    }

    @Override
    public byte getType() {
        return (byte) 2;
    }

    @Override
    public RedisDataStruct data() {
        return list;
    }

    @Override
    public byte[] objectToBytes() {
        return new byte[0];
    }

    @Override
    public void loadRdb(ByteBuf bufferPolled) {

    }
}
