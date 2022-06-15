package org.isheihei.redis.core.evict.impl;

import org.isheihei.redis.core.evict.AbstractEvictStrategy;
import org.isheihei.redis.core.struct.impl.BytesWrapper;

/**
 * @ClassName: AllKeysLfuEvict
 * @Description: 对所有键执行lfu策略
 * @Date: 2022/6/15 23:59
 * @Author: isheihei
 */
public class AllKeysLfuEvict extends AbstractEvictStrategy {
    @Override
    public void doEvict() {
        BytesWrapper lfuKey = null;
        long min = Long.MAX_VALUE;
        for (int i = 0; i < samples; i++) {
            BytesWrapper randomKey = db.getRandomKey();
            long lfu = db.get(randomKey).lfuDecrAndReturn();
            if (lfu < min) {
                lfuKey = randomKey;
                min = lfu;
            }
        }
        db.delete(lfuKey);
        LOGGER.info("淘汰了key : " + lfuKey.toUtf8String());
    }
}
