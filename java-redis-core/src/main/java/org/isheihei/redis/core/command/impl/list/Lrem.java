package org.isheihei.redis.core.command.impl.list;

import io.netty.channel.ChannelHandlerContext;
import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.Command;
import org.isheihei.redis.core.command.CommandType;
import org.isheihei.redis.core.resp.Resp;

/**
 * @ClassName: Lrem
 * @Description: TODO
 * @Date: 2022/6/10 15:43
 * @Author: isheihei
 */
public class Lrem implements Command {
    @Override
    public CommandType type() {
        return CommandType.lrem;
    }

    @Override
    public void setContent(Resp[] array) {

    }

    @Override
    public void handle(ChannelHandlerContext ctx, RedisClient redisClient) {
    }
}
