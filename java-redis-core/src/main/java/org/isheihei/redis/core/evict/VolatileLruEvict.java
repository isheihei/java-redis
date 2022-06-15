package org.isheihei.redis.core.evict;

import org.isheihei.redis.core.struct.impl.BytesWrapper;

/**
 * @ClassName: VolatileLruEvict
 * @Description: TODO
 * @Date: 2022/6/15 16:32
 * @Author: isheihei
 */
public class VolatileLruEvict extends AbstractEvictStrategy{
    @Override
    public void doEvict() {
        if (db.expiresSize() == 0) {
            LOGGER.info("没有易失键，尝试逐出失败");
            return;
        }
        LOGGER.info("正在逐出......");
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
        LOGGER.info("淘汰了 " + lruKey.toUtf8String());
    }
}
