package org.isheihei.redis.core.evict;

import org.isheihei.redis.core.evict.impl.AllKeysLfuEvict;
import org.isheihei.redis.core.evict.impl.AllKeysLruEvict;
import org.isheihei.redis.core.evict.impl.AllKeysRandomEvict;
import org.isheihei.redis.core.evict.impl.NoEvict;
import org.isheihei.redis.core.evict.impl.VolatileLfuEvict;
import org.isheihei.redis.core.evict.impl.VolatileLruEvict;
import org.isheihei.redis.core.evict.impl.VolatileRandomEvict;
import org.isheihei.redis.core.evict.impl.VolatileTtlEvict;

/**
 * @ClassName: Evict
 * @Description: 逐出策略工厂
 * @Date: 2022/6/15 21:31
 * @Author: isheihei
 */
public interface Evict {

    static EvictStrategy VOLATILE_LRU_EVICT() {
        return new VolatileLruEvict();
    }

    static EvictStrategy ALL_KEYS_LRU_EVICT() {
        return new AllKeysLruEvict();
    }

    static EvictStrategy VOLATILE_RANDOM_EVICT() {
        return new VolatileRandomEvict();
    }

    static EvictStrategy ALL_KEYS_RANDOM_EVICT() {
        return new AllKeysRandomEvict();
    }

    static EvictStrategy VOLATILE_LFU_EVICT() {
        return new VolatileLfuEvict();
    }

    static EvictStrategy ALL_KEYS_LFU_EVICT() {
        return new AllKeysLfuEvict();
    }

    static EvictStrategy VOLATILE_TTL_EVICT() {
        return new VolatileTtlEvict();
    }

    static EvictStrategy NO_EVICT() {
        return new NoEvict();
    }
}
