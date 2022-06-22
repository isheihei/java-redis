package org.isheihei.redis.server;

import org.isheihei.redis.core.db.RedisDB;
import org.isheihei.redis.core.evict.EvictStrategy;
import org.isheihei.redis.core.expired.ExpireStrategy;
import org.isheihei.redis.core.persist.aof.Aof;
import org.isheihei.redis.core.persist.rdb.Rdb;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName: ServerCron
 * @Description: 服务器定期任务
 * @Date: 2022/6/14 12:42
 * @Author: isheihei
 */
public class ServerCron implements Runnable{
    org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(ServerCron.class);

    private List<RedisDB> dbs;
    private ExpireStrategy expireStrategy = null;

    private EvictStrategy evictStrategy = null;

    private Aof aof = null;

    private Rdb rdb = null;

    private long lastAofTime = 0;

    public ServerCron() {
    }

    public ServerCron(List<RedisDB> dbs) {
        this.dbs = dbs;
    }

    public ServerCron expireStrategy(ExpireStrategy expireStrategy) {
        this.expireStrategy = expireStrategy;
        expireStrategy.setDbs(dbs);
        return this;
    }

    public ServerCron aof(Aof aof) {
        this.aof = aof;
        return this;
    }

    public ServerCron evictStrategy(EvictStrategy evictStrategy) {
        this.evictStrategy = evictStrategy;
        return this;
    }


    public ServerCron rdb(Rdb rdb) {
        this.rdb = rdb;
        return this;
    }

    private void databasesCron() {
        expireStrategy.activeExpireCycle();
    }

    private void aofPersist() {
        if (aof == null) {
            return;
        }
        if (System.currentTimeMillis() - lastAofTime > TimeUnit.SECONDS.toMillis(1)) {
            aof.save();
            lastAofTime = System.currentTimeMillis();
        }
    }

    private void rdbPersist() {
        if (rdb != null && rdb.satisfySaveParams()) {
            rdb.save();
        }
    }

    private void evict() {
        Runtime runtime = Runtime.getRuntime();
        int i = -1;

        /**
         * 正常计算公式为：runtime.totalMemory() > runtime.maxMemory() * evictStrategy.getMemoryRation()
         * but！！！
         * JVM初始分配的内存由-Xms指定，默认是物理内存的1/64;
         * JVM最大分配的内存由-Xmx指定，默认是物理内存的1/4。默认空余堆内存小于40%时，JVM就会增大堆直到-Xmx的最大限制；
         * 空余堆内存大于70%时，JVM会减少堆直到-Xms的最小限制。因此服务器一般设置-Xms、-Xmx相等以避免在每次GC后调整堆的大小。
         * 为了适配 jvm 的内存分配策略
         * 通常服务器中运行时候 jvm 会配置初始和最大内存相同，这时候建议关闭逐出策略
         *
         * 如果使选择开启逐出策略，那么应该设置 jvm 的最大内存即为需要开始逐出的内存大小
         * 例如 -Xms50m -Xmx100m 那么当 jvm 内存初始为50m，当空余堆内存小于 40% 时，jvm扩容到100m，这时候开始逐出策略
         * jvm 设置建议：初始内存/最大内存 = 1/2 下面是推导过程
         * 最小x，最大y：0.6x 扩容到 y，0.3y时候缩减到x
         * 0.6x = 目标阈值
         * 0.3y = 目标阈值
         * 0.6x = 0.3y
         * x:y = 1:2
         * 设当内存占用超过 100m 的时候需要逐出，即目标阈值 = 180m
         * 可以计算出 x = 300m, y = 600m
         * 当占用到 0.6x = 180m 时候增加到 200m
         * 当空闲超 0.7y = 420m 的时候减少到 180m
         */
//        LOGGER.info("目前占用内存：" + runtime.totalMemory());
//        LOGGER.info("最大内存：" + runtime.maxMemory());
//        LOGGER.info("触发内存逐出阈值" + runtime.maxMemory() * evictStrategy.getMemoryRation());
        while (runtime.totalMemory() == runtime.maxMemory()) {
            i = (i + 1) % dbs.size();
            if (dbs.get(i).size() != 0) {
                evictStrategy.setDb(dbs.get(i));
                evictStrategy.doEvict();
            } else {
                continue;
            }
        }
    }

    @Override
    public void run() {
        databasesCron();

        aofPersist();

        rdbPersist();

        evict();
    }

}
