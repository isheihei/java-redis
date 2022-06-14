package org.isheihei.redis.core.command.impl.server;

import io.netty.channel.ChannelHandlerContext;
import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.AbstractCommand;
import org.isheihei.redis.core.command.CommandType;

/**
 * @ClassName: Flushall
 * @Description: TODO
 * @Date: 2022/6/11 23:51
 * @Author: isheihei
 */
public class Flushall extends AbstractCommand {

    @Override
    public CommandType type() {
        return null;
    }

    @Override
    public void handle(ChannelHandlerContext ctx, RedisClient redisClient) {

    }
}
