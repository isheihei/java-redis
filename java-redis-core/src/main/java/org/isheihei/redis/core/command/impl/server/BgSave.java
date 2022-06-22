package org.isheihei.redis.core.command.impl.server;

import io.netty.channel.ChannelHandlerContext;
import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.AbstractCommand;
import org.isheihei.redis.core.command.CommandType;

/**
 * @ClassName: BgSave
 * @Description: 后台保存DB
 * @Date: 2022/6/22 11:33
 * @Author: isheihei
 */
public class BgSave extends AbstractCommand {

    @Override
    public CommandType type() {
        return CommandType.bgsave;
    }

    @Override
    public void handle(ChannelHandlerContext ctx, RedisClient redisClient) {

    }
}
