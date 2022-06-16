package org.isheihei.redis.core.struct;

import io.netty.buffer.ByteBuf;

/**
 * @ClassName: RedisDataStruct
 * @Description: Redis数据类型
 * @Date: 2022/5/31 0:23
 * @Author: isheihei
 */
public interface RedisDataStruct {
    byte[] toBytes();

    void loadRdb(ByteBuf bufferPolled);
}
