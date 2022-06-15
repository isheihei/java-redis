package org.isheihei.redis.core.evict.impl;

import org.isheihei.redis.core.evict.AbstractEvictStrategy;
import org.isheihei.redis.core.struct.impl.BytesWrapper;

/**
 * @ClassName: VolatileTtlEvict
 * @Description: 逐出易失键（设置了过期时间）中最快过期者
 * @Date: 2022/6/15 23:56
 * @Author: isheihei
 */
public class VolatileTtlEvict extends AbstractEvictStrategy {
    @Override
    public void doEvict() {
        BytesWrapper ttlKey = null;
        long min = Long.MAX_VALUE;
        for (int i = 0; i < samples; i++) {
            BytesWrapper randomKey = db.getRandomKey();
            Long ttl = db.getTtl(randomKey);
            if (ttl < min) {
                ttlKey = randomKey;
                min = ttl;
            }
        }
        db.delete(ttlKey);
        LOGGER.info("淘汰了key : " + ttlKey.toUtf8String());
    }
}
