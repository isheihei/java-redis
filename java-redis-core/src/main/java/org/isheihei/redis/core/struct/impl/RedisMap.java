package org.isheihei.redis.core.struct.impl;

import org.isheihei.redis.core.obj.RedisObject;
import org.isheihei.redis.core.struct.RedisDataStruct;

import java.util.HashMap;

/**
 * @ClassName: RedisMap
 * @Description: Redis字典数据类型
 * @Date: 2022/5/31 0:25
 * @Author: isheihei
 */
public class RedisMap<K, V> extends HashMap<K, V> implements RedisDataStruct {
}
