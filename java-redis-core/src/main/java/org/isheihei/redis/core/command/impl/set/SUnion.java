package org.isheihei.redis.core.command.impl.set;

import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.CommandType;
import org.isheihei.redis.core.resp.Resp;

/**
 * @ClassName: SUnion
 * @Description: 于返回所有给定集合的并集
 * @Date: 2022/6/11 15:39
 * @Author: isheihei
 */
public class SUnion extends SetsCommand {

    @Override
    public CommandType type() {
        return CommandType.sunion;
    }

    @Override
    public Resp handle(RedisClient redisClient) {
        return setsCommand(redisClient, 2);
    }
}
