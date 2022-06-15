package org.isheihei.redis.core.expired;

import org.isheihei.redis.core.db.RedisDB;

import java.util.List;

/**
 * @ClassName: ExpireStrategy
 * @Description: 过期策略
 * @Date: 2022/6/14 12:49
 * @Author: isheihei
 */
public interface ExpireStrategy {

    org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(ExpireStrategy.class);

    void activeExpireCycle();

    void setDbs(List<RedisDB> dbs);
}
