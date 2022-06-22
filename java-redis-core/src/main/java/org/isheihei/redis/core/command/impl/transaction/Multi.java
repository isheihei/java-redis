package org.isheihei.redis.core.command.impl.transaction;

import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.AbstractCommand;
import org.isheihei.redis.core.command.CommandType;
import org.isheihei.redis.core.resp.Resp;
import org.isheihei.redis.core.resp.impl.SimpleString;

/**
 * @ClassName: Multi
 * @Description: 标记一个事务块的开始
 * @Date: 2022/6/22 15:50
 * @Author: isheihei
 */
public class Multi extends AbstractCommand {
    @Override
    public CommandType type() {
        return CommandType.multi;
    }

    @Override
    public Resp handle(RedisClient redisClient) {
        redisClient.setFlag(true);
        return SimpleString.OK;
    }
}
