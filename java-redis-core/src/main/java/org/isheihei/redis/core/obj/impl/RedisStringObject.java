package org.isheihei.redis.core.obj.impl;

import org.isheihei.redis.core.obj.AbstractRedisObject;
import org.isheihei.redis.core.struct.RedisDataStruct;
import org.isheihei.redis.core.struct.RedisDataStructType;
import org.isheihei.redis.core.struct.impl.BytesWrapper;
import org.isheihei.redis.core.struct.impl.RedisDynamicString;

/**
 * @ClassName: RedisStringObject
 * @Description: Redis字符串对象
 * @Date: 2022/5/31 13:05
 * @Author: isheihei
 */
public class RedisStringObject extends AbstractRedisObject {

    private RedisDataStruct string;

    public RedisStringObject() {
        setEncoding(RedisDataStructType.redisDynamicString);
        string = getEncoding().getSupplier().get();
    }


    public RedisStringObject(RedisDataStructType encoding) {
        super(encoding);
        string = getEncoding().getSupplier().get();
    }

    public RedisStringObject(BytesWrapper stringValue) {
        setEncoding(RedisDataStructType.redisDynamicString);
        string = new RedisDynamicString(stringValue);
    }



    @Override
    public RedisDataStruct data() {
        return string;
    }
}
