package org.isheihei.redis.core.evict.impl;

import org.isheihei.redis.core.evict.AbstractEvictStrategy;
import org.isheihei.redis.core.struct.impl.BytesWrapper;

/**
 * @ClassName: VolatileRandomEvict
 * @Description: 对易失键（设置了过期时间）执行随机逐出策略
 * @Date: 2022/6/15 23:56
 * @Author: isheihei
 */
public class VolatileRandomEvict extends AbstractEvictStrategy {
    @Override
    public void doEvict() {
        BytesWrapper randomKey = db.getRandomExpires();
        db.delete(randomKey);
        LOGGER.info("淘汰了key : " + randomKey);
    }
}
