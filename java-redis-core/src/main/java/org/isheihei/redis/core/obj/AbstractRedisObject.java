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


    public AbstractRedisObject() {
    }

    public AbstractRedisObject(RedisDataStructType encoding) {
        this.encoding = encoding;
    }

    @Override
    public abstract RedisDataStruct data();

    @Override
    public RedisDataStructType getEncoding() {
        return encoding;
    }



    public void setEncoding(RedisDataStructType encoding) {
        this.encoding = encoding;
    }


}
