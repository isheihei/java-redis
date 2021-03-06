package org.isheihei.redis.core.struct;

import org.isheihei.redis.core.struct.impl.*;

import java.util.function.Supplier;

/**
 * @ClassName: RedisDataStructType
 * @Description: Redis数据类型枚举类
 * @Date: 2022/5/31 0:25
 * @Author: isheihei
 */
public enum RedisDataStructType {
    REDIS_DOUBLE_LINKED_LIST(RedisDoubleLinkedList::new),
    REDIS_MAP(RedisMap::new),
    REDIS_STRING(RedisString::new),
    REDIS_SET(RedisSet::new),
    REDIS_Z_SET(RedisZSet::new);

    // 数据结构构造器
    private final Supplier<RedisDataStruct> supplier;

    RedisDataStructType(Supplier supplier) {
        this.supplier = supplier;
    }

    /**
     * @Description: 获取一个数据结构实例对象
     * @Return: Supplier<RedisDataStruct>
     * @Author: isheihei
     */
    public Supplier<RedisDataStruct> getSupplier() {
        return supplier;
    }
}
