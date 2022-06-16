package org.isheihei.redis.core.obj;

import io.netty.buffer.ByteBuf;
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

    byte getType();

    long getLru();

    void refreshLru();

    long getCnt();

    long getLdt();

    void updateLfu();

    int lfuDecrAndReturn();

    byte[] objectToBytes();

    void loadRdb(ByteBuf bufferPolled);
}
