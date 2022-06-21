package org.isheihei.redis.core.command.impl.server;

import io.netty.channel.ChannelHandlerContext;
import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.AbstractCommand;
import org.isheihei.redis.core.command.CommandType;
import org.isheihei.redis.core.resp.impl.SimpleString;

/**
 * @ClassName: FlushAll
 * @Description: TODO
 * @Date: 2022/6/11 23:51
 * @Author: isheihei
 */
public class FlushAll extends AbstractCommand {

    @Override
    public CommandType type() {
        return CommandType.flushall;
    }

    @Override
    public void handle(ChannelHandlerContext ctx, RedisClient redisClient) {
        redisClient.flushAll();
        ctx.writeAndFlush(SimpleString.OK);
    }
}
