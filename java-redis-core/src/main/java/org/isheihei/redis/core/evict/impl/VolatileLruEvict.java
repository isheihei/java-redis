package org.isheihei.redis.core.evict.impl;

import org.isheihei.redis.core.evict.AbstractEvictStrategy;
import org.isheihei.redis.core.struct.impl.BytesWrapper;

/**
 * @ClassName: VolatileLruEvict
 * @Description: 对易失键（设置了过期时间）执行lru策略
 * @Date: 2022/6/15 16:32
 * @Author: isheihei
 */
public class VolatileLruEvict extends AbstractEvictStrategy {
    @Override
    public void doEvict() {
        if (db.expiresSize() == 0) {
            LOGGER.info("没有易失键，尝试淘汰失败");
            return;
        }
        BytesWrapper lruKey = null;
        long min = Long.MAX_VALUE;
        for (int i = 0; i < samples; i++) {
            BytesWrapper randomKey = db.getRandomExpires();
            long lru = db.get(randomKey).getLru();
            if (lru < min) {
                lruKey = randomKey;
                min = lru;
            }
        }
        db.delete(lruKey);
        LOGGER.info("淘汰了key : " + lruKey.toUtf8String());
    }
}
