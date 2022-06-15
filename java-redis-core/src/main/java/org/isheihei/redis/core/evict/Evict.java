package org.isheihei.redis.core.evict;

/**
 * @ClassName: Evict
 * @Description: TODO
 * @Date: 2022/6/15 21:31
 * @Author: isheihei
 */
public interface Evict {

    public static EvictStrategy volatileLruEvict() {
        return new VolatileLruEvict();
    }

    public static EvictStrategy volatileTtlEvict() {
        return new VolatileTtlEvict();
    }
}
