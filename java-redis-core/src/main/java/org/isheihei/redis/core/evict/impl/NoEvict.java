package org.isheihei.redis.core.evict.impl;

import org.isheihei.redis.core.evict.AbstractEvictStrategy;

/**
 * @ClassName: NoEvict
 * @Description: 不进行逐出
 * @Date: 2022/6/15 23:55
 * @Author: isheihei
 */
public class NoEvict extends AbstractEvictStrategy {
    @Override
    public void doEvict() {
        return;
    }
}
