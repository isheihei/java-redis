package org.isheihei.redis.core.struct.impl;

import org.isheihei.redis.core.obj.RedisObject;
import org.isheihei.redis.core.struct.RedisDataStruct;

import java.util.HashSet;

/**
 * @ClassName: RedisSet
 * @Description: Redis集合数据类型
 * @Date: 2022/5/31 0:25
 * @Author: isheihei
 */
public class RedisSet extends HashSet<RedisDynamicString> implements RedisDataStruct {
}
