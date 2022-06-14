package org.isheihei.redis.server;

import org.isheihei.redis.core.expired.ExpireStrategy;
import org.isheihei.redis.core.persist.aof.Aof;

import java.util.concurrent.TimeUnit;

/**
 * @ClassName: ServerCron
 * @Description: 服务器定期任务
 * @Date: 2022/6/14 12:42
 * @Author: isheihei
 */
public class ServerCron implements Runnable{

    // TODO 持久化操作放到子线程或者子进程进行

    private ExpireStrategy expireStrategy = null;

    private Aof aof = null;

    private long lastAofTime = 0;

    public ServerCron() {
    }

    public ServerCron expireStrategy(ExpireStrategy expireStrategy) {
        this.expireStrategy = expireStrategy;
        return this;
    }

    public ServerCron aof(Aof aof) {
        this.aof = aof;
        return this;
    }

    private void databasesCron() {
        expireStrategy.activeExpireCycle();
    }

    private void aofPersist() {
        if (System.currentTimeMillis() - lastAofTime > TimeUnit.SECONDS.toMillis(1)) {
            lastAofTime = System.currentTimeMillis();
            aof.save();
        }
    }

    @Override
    public void run() {
        databasesCron();
        aofPersist();
    }
}
