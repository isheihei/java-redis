package org.isheihei.redis.core.obj;

import org.isheihei.redis.core.struct.RedisDataStruct;
import org.isheihei.redis.core.struct.RedisDataStructType;

import java.util.Random;

/**
 * @ClassName: AbstractRedisObject
 * @Description: 抽象类
 * @Date: 2022/5/31 12:56
 * @Author: isheihei
 */
public abstract class AbstractRedisObject implements RedisObject{

    // 对象底层实现结构
    private RedisDataStructType encoding;

    //  最近访问时间
    private long lru;

    //  访问计数
    private int accessCount;

    //  最近一次 accessCount 降低的时间
    private long ldt;

    //  65535 分钟为一个周期 每过一个周期降低accessCount
    private static final long LFU_DECAY_TIME = 3932100000L;

    // accessCount 初始值
    private static final int LFU_INIT_VAL = 5;

    // 控制 accessCount 增长的因子 因子越大，增长的概率越小
    private static final int LFU_LOG_FACTOR = 10;

    public AbstractRedisObject() {
        this.lru = System.currentTimeMillis();
        this.accessCount = LFU_INIT_VAL;
        this.ldt = System.currentTimeMillis();
    }

    public AbstractRedisObject(RedisDataStructType encoding) {
        this.encoding = encoding;
        this.lru = System.currentTimeMillis();
        this.ldt = System.currentTimeMillis();
        this.accessCount = LFU_INIT_VAL;
    }
    @Override
    public long getLru() {
        return lru;
    }

    @Override
    public void refreshLru() {
        lru = System.currentTimeMillis();
    }

    @Override
    public long getCnt() {
        return accessCount;
    }

    @Override
    public long getLdt() {
        return ldt;
    }

    /**
     * redis 源码逻辑
     * void updateLFU(robj *val) {
     *     unsigned long counter = LFUDecrAndReturn(val);
     *     counter = LFULogIncr(counter);
     *     val->lru = (LFUGetTimeInMinutes()<<8) | counter;
     * }
     */
    @Override
    public void updateLfu() {
        lfuDecrAndReturn();
        // 最大值为255
        if (accessCount == 255) {
            return;
        }
        // 取一个0-1之间的随机数r与p比较，当r<p时，才增加counter，这和比特币中控制产出的策略类似。
        // p取决于当前counter值与lfu_log_factor因子，
        // counter值与lfu_log_factor因子越大，p越小，r<p的概率也越小，counter增长的概率也就越小。
        double r = new Random().nextDouble();
        double baseval = accessCount - LFU_INIT_VAL;
        if (baseval < 0) baseval = 0;
        double p = 1.0 / (baseval * LFU_LOG_FACTOR + 1);
        if (r < p) accessCount++;
    }

    @Override
    public int lfuDecrAndReturn() {
        long l = System.currentTimeMillis() - ldt;
        long decr = l / LFU_DECAY_TIME;
        if (decr != 0) {
            accessCount -= decr;
            ldt = System.currentTimeMillis();
        }
        return accessCount;
    }

    @Override
    public abstract RedisDataStruct data();

    @Override
    public RedisDataStructType getEncoding() {
        return encoding;
    }


    public void setEncoding(RedisDataStructType encoding) {
        this.encoding = encoding;
    }

}
