package org.isheihei.redis.core.command.impl.connection;

import io.netty.channel.ChannelHandlerContext;
import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.Command;
import org.isheihei.redis.core.command.CommandType;
import org.isheihei.redis.core.resp.BulkString;
import org.isheihei.redis.core.resp.Resp;
import org.isheihei.redis.core.struct.impl.BytesWrapper;

/**
 * @ClassName: Echo
 * @Description: 打印一个给定的信息 message ，测试时使用
 * @Date: 2022/6/11 16:00
 * @Author: isheihei
 */
public class Echo implements Command {

    private Resp[] array;

    private BytesWrapper message;

    @Override
    public CommandType type() {
        return CommandType.echo;
    }

    @Override
    public void setContent(Resp[] array) {
        this.array = array;
    }


    @Override
    public void handle(ChannelHandlerContext ctx, RedisClient redisClient) {
        if ((message = getBytesWrapper(ctx, array, 1)) == null) {
            return;
        }
        ctx.writeAndFlush(new BulkString(message));
    }
}
