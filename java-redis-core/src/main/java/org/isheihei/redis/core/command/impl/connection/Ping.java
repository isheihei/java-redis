package org.isheihei.redis.core.command.impl.connection;

import io.netty.channel.ChannelHandlerContext;
import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.Command;
import org.isheihei.redis.core.command.CommandType;
import org.isheihei.redis.core.resp.BulkString;
import org.isheihei.redis.core.resp.Resp;
import org.isheihei.redis.core.resp.SimpleString;
import org.isheihei.redis.core.struct.impl.BytesWrapper;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName: Ping
 * @Description: 用来测试连接是否存活
 * @Date: 2022/6/11 16:00
 * @Author: isheihei
 */
public class Ping implements Command {

    private List<BytesWrapper> message;

    @Override
    public CommandType type() {
        return CommandType.ping;
    }

    @Override
    public void setContent(Resp[] array) {
        message = Arrays.stream(array).skip(1).map(resp -> ((BulkString) resp).getContent()).collect(Collectors.toList());
    }

    @Override
    public void handle(ChannelHandlerContext ctx, RedisClient redisClient) {
        if (message == null || message.size() == 0) {
            ctx.writeAndFlush(new SimpleString("PONG"));
        } else {
            ctx.writeAndFlush(new SimpleString(message.get(0).toUtf8String()));
        }
        // 立即回复
        ctx.flush();
    }
}
