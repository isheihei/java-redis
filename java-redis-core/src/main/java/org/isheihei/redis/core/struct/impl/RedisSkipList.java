package org.isheihei.redis.core.struct.impl;

import org.isheihei.redis.core.obj.RedisObject;
import org.isheihei.redis.core.struct.RedisDataStruct;

import java.util.concurrent.ConcurrentSkipListMap;

/**
 * @ClassName: RedisSkipList
 * @Description: Redis跳表数据类型
 * @Date: 2022/5/31 0:25
 * @Author: isheihei
 */
public class RedisSkipList<K, V> extends ConcurrentSkipListMap<K, V> implements RedisDataStruct {
}
