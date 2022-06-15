package org.isheihei.redis.core.expired;

/**
 * @ClassName: Expire
 * @Description: TODO
 * @Date: 2022/6/15 21:52
 * @Author: isheihei
 */
public interface Expire {

    public static ExpireStrategy defaultExpireStrategy() {
        return new DefaultExpireStrategy();
    }
}
