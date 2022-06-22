package org.isheihei.redis.core.command.impl.server;

import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.AbstractCommand;
import org.isheihei.redis.core.command.CommandType;
import org.isheihei.redis.core.resp.Resp;
import org.isheihei.redis.core.resp.impl.RespInt;

/**
 * @ClassName: DbSize
 * @Description: 返回当前数据库中 key 的数量
 * @Date: 2022/6/11 23:50
 * @Author: isheihei
 */
public class DbSize extends AbstractCommand {
    @Override
    public CommandType type() {
        return CommandType.dbsize;
    }

    @Override
    public Resp handle(RedisClient redisClient) {
        return new RespInt(redisClient.getDb().size());
    }
}
