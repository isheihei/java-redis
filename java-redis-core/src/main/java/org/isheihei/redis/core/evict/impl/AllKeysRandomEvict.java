package org.isheihei.redis.core.evict.impl;

import org.isheihei.redis.core.evict.AbstractEvictStrategy;
import org.isheihei.redis.core.struct.impl.BytesWrapper;

/**
 * @ClassName: AllKeysRandomEvict
 * @Description: 对所有键执行随机逐出策略
 * @Date: 2022/6/15 23:57
 * @Author: isheihei
 */
public class AllKeysRandomEvict extends AbstractEvictStrategy {
    @Override
    public void doEvict() {
        BytesWrapper randomKey = db.getRandomKey();
        db.delete(randomKey);
        LOGGER.info("淘汰了key : " + randomKey);
    }
}
