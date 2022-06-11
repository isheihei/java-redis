package org.isheihei.redis.core.struct.impl;

import org.isheihei.redis.core.struct.RedisDataStruct;

/**
 * @ClassName: RedisDynamicString
 * @Description: Redis动态字符串数据类型
 * @Date: 2022/5/31 0:24
 * @Author: isheihei
 */
public class RedisDynamicString implements RedisDataStruct {

    private BytesWrapper value;

    public RedisDynamicString(BytesWrapper value){
        this.value = value;
    }
    public RedisDynamicString(){

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
}
