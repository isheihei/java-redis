package org.isheihei.redis.core.expired;

/**
 * @ClassName: Expire
 * @Description: 过期策略工厂
 * @Date: 2022/6/15 21:52
 * @Author: isheihei
 */
public interface Expire {

    static ExpireStrategy DEFAULT_EXPIRE_STRATEGY() {
        return new DefaultExpireStrategy();
    }
}
