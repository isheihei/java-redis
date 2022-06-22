package org.isheihei.redis.core.command.impl.transaction;

import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.AbstractCommand;
import org.isheihei.redis.core.command.CommandType;
import org.isheihei.redis.core.resp.Resp;
import org.isheihei.redis.core.resp.impl.SimpleString;

/**
 * @ClassName: Discard
 * @Description: 取消事务，放弃执行事务队列内的所有命令
 * @Date: 2022/6/22 15:50
 * @Author: isheihei
 */
public class Discard extends AbstractCommand {
    @Override
    public CommandType type() {
        return CommandType.discard;
    }

    @Override
    public Resp handle(RedisClient redisClient) {
        redisClient.flushCommand();
        redisClient.setFlag(false);
        redisClient.setDirtyCas(false);
        redisClient.getDb().unWatchKeys(redisClient);
        return SimpleString.OK;
    }
}
