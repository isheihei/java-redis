package org.isheihei.redis.core.evict.impl;

import org.isheihei.redis.core.evict.AbstractEvictStrategy;
import org.isheihei.redis.core.struct.impl.BytesWrapper;

/**
 * @ClassName: AllKeysLruEvict
 * @Description: 对所有键执行lru策略
 * @Date: 2022/6/15 23:57
 * @Author: isheihei
 */
public class AllKeysLruEvict extends AbstractEvictStrategy {
    @Override
    public void doEvict() {
        BytesWrapper lruKey = null;
        long min = Long.MAX_VALUE;
        for (int i = 0; i < samples; i++) {
            BytesWrapper randomKey = db.getRandomKey();
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
