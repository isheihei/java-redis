package org.isheihei.redis.core.command.impl.string;

import io.netty.channel.ChannelHandlerContext;
import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.AbstractCommand;
import org.isheihei.redis.core.command.CommandType;

/**
 * @ClassName: SetNx
 * @Description: TODO
 * @Date: 2022/6/9 23:25
 * @Author: isheihei
 */
public class SetNx extends AbstractCommand {
    @Override
    public CommandType type() {
        return null;
    }

    @Override
    public void handle(ChannelHandlerContext ctx, RedisClient redisClient) {

    }
}
