package org.isheihei.redis.core.command.impl;

import io.netty.channel.ChannelHandlerContext;
import org.isheihei.redis.common.util.TRACEID;
import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.Command;
import org.isheihei.redis.core.command.CommandType;
import org.isheihei.redis.core.resp.BulkString;
import org.isheihei.redis.core.resp.Resp;
import org.isheihei.redis.core.resp.SimpleString;

/**
 * @ClassName: Client
 * @Description: 客户端操作
 * @Date: 2022/6/8 19:14
 * @Author: isheihei
 */
public class Client implements Command {

    private String subCommand;

    private Resp[] array;

    @Override
    public CommandType type() {
        return CommandType.client;
    }

    @Override
    public void setContent(Resp[] array) {
        this.array = array;
        subCommand = ((BulkString) array[1]).getContent().toUtf8String();
    }

    @Override
    public void handle(ChannelHandlerContext ctx, RedisClient redisClient) {
        String traceId = TRACEID.currentTraceId();
        LOGGER.debug("traceId:{} 当前的子命令是：{}"+traceId+subCommand);
        switch (subCommand) {
            case "setname":
                String ClientName = new String(((BulkString) array[2]).getContent().getByteArray());
                redisClient.setName(ClientName);
                ctx.writeAndFlush(new SimpleString("OK"));
                break;
            case "getname":
                String name = redisClient.getName();
                if (name == null) {
                    ctx.writeAndFlush(BulkString.NullBulkString);
                } else {
                    ctx.writeAndFlush(new SimpleString(name));
                }
                break;
            default:
                throw new IllegalArgumentException();
        }
    }
}
