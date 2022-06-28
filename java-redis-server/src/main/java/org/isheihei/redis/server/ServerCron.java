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

    /**
     * Java 提供的
     * totalMemory() maxMemory()freeMemory()
     * 方法能获取当前 JVM 已分配内存 、 JVM 最大可扩展内存以及 JVM 当前已分配内存中的空闲内存。
     * 为了适配 JVM 内存，给出以下内存策略：
     *
     * 建议初始内存和最大内存参数相等
     * 这样可以避免运行过程中的内存重分配过程，也更容易精确计算当前内存的占用情况。
     * 但是：
     * 由于 JVM 的内存的 GC 时间是不固定的，有些已经被淘汰的键不能被虚拟机及时回收，可能会造成获取的内存占用信息实时性不强的问题，
     * 这是由于 JVM 内存本身机制决定，能力有限，暂时没有想到更优的解决办法。
     */
    private void evict() {
        Runtime runtime = Runtime.getRuntime();
        int i = -1;
        while (runtime.freeMemory() < 0.2 * runtime.totalMemory()) {
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
