package org.isheihei.redis.core.expired;

import org.isheihei.redis.core.db.RedisDB;
import org.isheihei.redis.core.struct.impl.BytesWrapper;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName: DeafaultExpireStrategy
 * @Description: TODO
 * @Date: 2022/6/14 13:07
 * @Author: isheihei
 */
public class DefaultExpireStrategy implements ExpireStrategy{

    private List<RedisDB> dbs;

    private int dbNumbers = 16;

    private int keyNumbers = 20;

    private int currentDb = 0;

    private long timeLimit = TimeUnit.MICROSECONDS.toMillis(1000);

    private long start;

    public DefaultExpireStrategy(List<RedisDB> dbs) {
        this.dbs = dbs;
    }

    public DefaultExpireStrategy() {
    }

    public DefaultExpireStrategy(List<RedisDB> dbs, int dbNumbers, int keyNumbers) {
        this.dbs = dbs;
        this.dbNumbers = dbNumbers;
        this.keyNumbers = keyNumbers;
    }

    @Override
    public void activeExpireCycle() {
        start = System.currentTimeMillis();
        int dbSize = dbs.size();
        if (dbSize < dbNumbers) {
            dbNumbers = dbSize;
        }

        currentDb %= dbSize;
        for (int i = 0; i < keyNumbers; i++){
            RedisDB redisDB = dbs.get(currentDb);
            int expiresSize = redisDB.expiresSize();
            if (expiresSize == 0) return;
            BytesWrapper randomKey = redisDB.getRandomExpires();
            if (redisDB.isExpired(randomKey)) {
                LOGGER.info("过期key: "  + randomKey.toUtf8String() +  "被删除");
                redisDB.delete(randomKey);
            }
            if (System.currentTimeMillis() - start > timeLimit) {
                return;
            }
        }
    }

    @Override
    public void setDbs(List<RedisDB> dbs) {
        this.dbs = dbs;
    }
}
