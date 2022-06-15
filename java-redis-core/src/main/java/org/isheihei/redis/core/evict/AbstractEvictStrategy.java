package org.isheihei.redis.core.evict;

import org.isheihei.redis.core.db.RedisDB;

/**
 * @ClassName: AbstractEvictStrategy
 * @Description: 逐出策略抽象类
 * @Date: 2022/6/15 16:33
 * @Author: isheihei
 */
public abstract class AbstractEvictStrategy implements EvictStrategy{

    public RedisDB db;

    //  redis 随机采样数在等于10的情况下已经很接近于理想的LRU算法性能
    public int samples = 10;

    public double memoryRation = 0.5;

    public AbstractEvictStrategy(RedisDB db, int samples, double memoryRation) {
        this.db = db;
        this.samples = samples;
        this.memoryRation = memoryRation;
    }

    public AbstractEvictStrategy(RedisDB db) {
        this.db = db;
    }

    public AbstractEvictStrategy() {
    }

    @Override
    public abstract void doEvict();

    @Override
    public void setDb(RedisDB db) {
        this.db = db;
    }


    @Override
    public double getMemoryRation() {
        return memoryRation;
    }
}
