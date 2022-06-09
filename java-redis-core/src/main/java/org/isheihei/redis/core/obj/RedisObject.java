package org.isheihei.redis.core.obj;

import org.isheihei.redis.core.struct.RedisDataStruct;
import org.isheihei.redis.core.struct.RedisDataStructType;

/**
 * @ClassName: RedisObject
 * @Description: Redis数据对象
 * @Date: 2022/5/31 0:22
 * @Author: isheihei
 */
public interface RedisObject {

    RedisDataStruct data();

    RedisDataStructType getEncoding();
}
