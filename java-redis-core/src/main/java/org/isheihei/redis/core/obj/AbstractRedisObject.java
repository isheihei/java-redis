package org.isheihei.redis.core.obj;

import org.isheihei.redis.core.struct.RedisDataStruct;
import org.isheihei.redis.core.struct.RedisDataStructType;

/**
 * @ClassName: AbstractRedisObject
 * @Description: 抽象类
 * @Date: 2022/5/31 12:56
 * @Author: isheihei
 */
public abstract class AbstractRedisObject implements RedisObject{

    // 对象底层实现结构
    private RedisDataStructType encoding;

    // 对象类型
    private RedisObjectType type;

    // 对象引用
    private RedisDataStruct data;

    public AbstractRedisObject(RedisDataStructType encoding, RedisObjectType type, RedisDataStruct data) {
        this.encoding = encoding;
        this.type = type;
        this.data = data;
    }

    @Override
    public RedisDataStructType encoding() {
        return encoding;
    }

    @Override
    public RedisObjectType type() {
        return type;
    }

    @Override
    public RedisDataStruct data() {
        return data;
    }
}
