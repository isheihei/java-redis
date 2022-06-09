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
}
