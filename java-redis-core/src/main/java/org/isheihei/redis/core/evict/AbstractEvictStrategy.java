package org.isheihei.redis.core.evict;

import org.isheihei.redis.core.db.RedisDB;

/**
 * @ClassName: AbstractEvictStrategy
 * @Description: TODO
 * @Date: 2022/6/15 16:33
 * @Author: isheihei
 */
public abstract class AbstractEvictStrategy implements EvictStrategy{

    public RedisDB db;

    public int samples = 20;

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
    public void setSamples(int samples) {
        this.samples = samples;
    }

    @Override
    public void setMemoryRation(double memoryRation) {
        this.memoryRation = memoryRation;
    }


    @Override
    public double getMemoryRation() {
        return memoryRation;
    }
}
