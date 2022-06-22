package org.isheihei.redis.core.command.impl.server;

import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.AbstractWriteCommand;
import org.isheihei.redis.core.command.CommandType;
import org.isheihei.redis.core.resp.Resp;
import org.isheihei.redis.core.resp.impl.SimpleString;

/**
 * @ClassName: FlushAll
 * @Description: 清空整个 Redis 中的数据
 * @Date: 2022/6/11 23:51
 * @Author: isheihei
 */
public class FlushAll extends AbstractWriteCommand {

    @Override
    public CommandType type() {
        return CommandType.flushall;
    }

    @Override
    public Resp handleWrite(RedisClient redisClient) {
        redisClient.flushAll();
        return SimpleString.OK;
    }

}
