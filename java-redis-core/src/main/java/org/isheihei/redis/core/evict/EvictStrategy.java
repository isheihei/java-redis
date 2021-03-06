package org.isheihei.redis.core.evict;

import org.isheihei.redis.core.db.RedisDB;

/**
 * @ClassName: EvictStrategy
 * @Description: 逐出策略
 * @Date: 2022/6/14 12:59
 * @Author: isheihei
 */
public interface EvictStrategy{

    org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(EvictStrategy.class);

    void doEvict();

    void setDb(RedisDB db);

    double getMemoryRation();
}
