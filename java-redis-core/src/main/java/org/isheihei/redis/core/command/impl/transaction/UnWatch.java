package org.isheihei.redis.core.command.impl.transaction;

import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.AbstractCommand;
import org.isheihei.redis.core.command.CommandType;
import org.isheihei.redis.core.resp.Resp;
import org.isheihei.redis.core.resp.impl.SimpleString;

/**
 * @ClassName: UnWatch
 * @Description: 取消 WATCH 命令对所有 key 的监视
 * @Date: 2022/6/22 15:49
 * @Author: isheihei
 */
public class UnWatch extends AbstractCommand {

    @Override
    public CommandType type() {
        return CommandType.unwatch;
    }

    @Override
    public Resp handle(RedisClient redisClient) {
        redisClient.setDirtyCas(false);
        redisClient.unWatchKeys(redisClient);
        return SimpleString.OK;
    }
}
