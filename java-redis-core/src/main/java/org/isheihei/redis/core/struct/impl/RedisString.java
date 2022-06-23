package org.isheihei.redis.core.struct.impl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import org.isheihei.redis.core.struct.RedisDataStruct;

/**
 * @ClassName: RedisString
 * @Description: Redis动态字符串数据类型
 * @Date: 2022/5/31 0:24
 * @Author: isheihei
 */
public class RedisString implements RedisDataStruct {

    private BytesWrapper value;

    public RedisString(BytesWrapper value){
        this.value = value;
    }
    public RedisString(){

    }
    public BytesWrapper getValue()
    {
        return value;
    }

    public void setValue(BytesWrapper value)
    {
        this.value = value;
    }

    public int append(BytesWrapper append) {
        byte[] oldValue = value.getByteArray();
        byte[] appendValue = append.getByteArray();
        byte[] newValue = new byte[oldValue.length + appendValue.length];
        System.arraycopy(oldValue, 0, newValue, 0, oldValue.length);
        System.arraycopy(appendValue, 0, newValue, oldValue.length, appendValue.length);
        value = new BytesWrapper(newValue);
        return value.length();
    }

    @Override
    public byte[] toBytes() {
        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.buffer();
        byteBuf.writeBytes(value.getByteArray());
        return ByteBufUtil.getBytes(byteBuf);
    }

    @Override
    public void loadRdb(ByteBuf byteBuf) {
        byteBuf.readerIndex();
        value = new BytesWrapper(ByteBufUtil.getBytes(byteBuf));
    }
}
