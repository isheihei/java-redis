package org.isheihei.redis.core.command.impl.set;

import io.netty.channel.ChannelHandlerContext;
import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.CommandType;

/**
 * @ClassName: SInter
 * @Description: 返回所有给定集合的成员交集
 * @Date: 2022/6/11 15:36
 * @Author: isheihei
 */
public class SInter extends SetsCommand {

    @Override
    public CommandType type() {
        return CommandType.sinter;
    }

    @Override
    public void handle(ChannelHandlerContext ctx, RedisClient redisClient) {
       setsCommand(ctx, redisClient, 1);
    }
}
