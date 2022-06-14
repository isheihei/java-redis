package org.isheihei.redis.core.command.impl.string;

import io.netty.channel.ChannelHandlerContext;
import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.AbstractCommand;
import org.isheihei.redis.core.command.CommandType;

/**
 * @ClassName: SetEx
 * @Description: TODO SETEX key seconds value
 * @Date: 2022/6/9 23:24
 * @Author: isheihei
 */
public class SetEx extends AbstractCommand {
    @Override
    public CommandType type() {
        return null;
    }

    @Override
    public void handle(ChannelHandlerContext ctx, RedisClient redisClient) {

    }
}
