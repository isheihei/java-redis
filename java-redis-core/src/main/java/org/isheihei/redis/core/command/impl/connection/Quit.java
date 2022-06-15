package org.isheihei.redis.core.command.impl.connection;

import io.netty.channel.ChannelHandlerContext;
import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.AbstractCommand;
import org.isheihei.redis.core.command.CommandType;
import org.isheihei.redis.core.resp.impl.SimpleString;

/**
 * @ClassName: Quit
 * @Description: 请求服务器关闭连接
 * @Date: 2022/6/11 16:00
 * @Author: isheihei
 */
public class Quit extends AbstractCommand {
    @Override
    public CommandType type()
    {
        return CommandType.quit;
    }

    @Override
    public void handle(ChannelHandlerContext ctx, RedisClient redisClient)
    {
        ctx.writeAndFlush(SimpleString.OK);
        ctx.close();
    }
}
