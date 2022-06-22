package org.isheihei.redis.core.obj.impl;

import io.netty.buffer.ByteBuf;
import org.isheihei.redis.core.obj.AbstractRedisObject;
import org.isheihei.redis.core.struct.RedisDataStruct;
import org.isheihei.redis.core.struct.RedisDataStructType;
import org.isheihei.redis.core.struct.impl.BytesWrapper;
import org.isheihei.redis.core.struct.impl.RedisString;

/**
 * @ClassName: RedisStringObject
 * @Description: Redis字符串对象
 * @Date: 2022/5/31 13:05
 * @Author: isheihei
 */
public class RedisStringObject extends AbstractRedisObject {

    private RedisDataStruct string;

    public RedisStringObject() {
        setEncoding(RedisDataStructType.REDIS_STRING);
        string = getEncoding().getSupplier().get();
    }


    public RedisStringObject(RedisDataStructType encoding) {
        super(encoding);
        string = getEncoding().getSupplier().get();
    }

    @Override
    public String getType() {
        return "string";
    }

    @Override
    public byte getCode() {
        return (byte) 0;
    }

    public RedisStringObject(BytesWrapper stringValue) {
        setEncoding(RedisDataStructType.REDIS_STRING);
        string = new RedisString(stringValue);
    }

    @Override
    public RedisDataStruct data() {
        return string;
    }

    @Override
    public byte[] objectToBytes() {
        return string.toBytes();
    }

    @Override
    public void loadRdb(ByteBuf bufferPolled) {
        string.loadRdb(bufferPolled);
    }
}
