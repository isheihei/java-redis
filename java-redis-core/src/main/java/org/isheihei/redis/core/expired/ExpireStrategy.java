package org.isheihei.redis.core.expired;

/**
 * @ClassName: ExpireStrategy
 * @Description: TODO
 * @Date: 2022/6/14 12:49
 * @Author: isheihei
 */
public interface ExpireStrategy {

    org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(ExpireStrategy.class);

    void activeExpireCycle();
}
