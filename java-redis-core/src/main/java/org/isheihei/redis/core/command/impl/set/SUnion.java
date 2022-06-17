package org.isheihei.redis.core.command.impl.set;

import io.netty.channel.ChannelHandlerContext;
import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.CommandType;

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
    public void handle(ChannelHandlerContext ctx, RedisClient redisClient) {
        setsCommand(ctx, redisClient, 2);
    }
}
