package org.isheihei.redis.core.command.impl.server;

import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.AbstractWriteCommand;
import org.isheihei.redis.core.command.CommandType;
import org.isheihei.redis.core.resp.Resp;
import org.isheihei.redis.core.resp.impl.SimpleString;

/**
 * @ClassName: FlushDb
 * @Description: 数据库中的所有 key
 * @Date: 2022/6/11 23:54
 * @Author: isheihei
 */
public class FlushDb extends AbstractWriteCommand {
    @Override
    public CommandType type() {
        return CommandType.flushdb;
    }

    @Override
    public Resp handleWrite(RedisClient redisClient) {
        redisClient.getDb().flushDb();
        return SimpleString.OK;
    }
}
