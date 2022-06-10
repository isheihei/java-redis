package org.isheihei.redis.core.command.impl;

import io.netty.channel.ChannelHandlerContext;
import org.isheihei.redis.common.consts.ErrorsConsts;
import org.isheihei.redis.common.util.TRACEID;
import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.Command;
import org.isheihei.redis.core.command.CommandType;
import org.isheihei.redis.core.resp.BulkString;
import org.isheihei.redis.core.resp.Errors;
import org.isheihei.redis.core.resp.Resp;
import org.isheihei.redis.core.resp.SimpleString;
import org.isheihei.redis.core.struct.impl.BytesWrapper;

/**
 * @ClassName: Client
 * @Description: 客户端操作
 * @Date: 2022/6/8 19:14
 * @Author: isheihei
 */
public class Client implements Command {

    private String subCommand;

    private String clientName;

    private Resp[] array;

    @Override
    public CommandType type() {
        return CommandType.client;
    }

    @Override
    public void setContent(Resp[] array) {
        this.array = array;

    }

    @Override
    public void handle(ChannelHandlerContext ctx, RedisClient redisClient) {
        String traceId = TRACEID.currentTraceId();
        LOGGER.debug("traceId:{} 当前的子命令是：{}" + traceId + subCommand);
        BytesWrapper bytesSubCommand;
        if ((bytesSubCommand = getBytesWrapper(ctx, array, 1)) == null) {
            return;
        }
        subCommand = bytesSubCommand.toUtf8String();
        switch (subCommand) {
            case "setname":
                if ((clientName = getStringSubCommandArgs(ctx, array, 2, subCommand)) == null) {
                    return;
                }
                redisClient.setName(clientName);
                ctx.writeAndFlush(SimpleString.OK);
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
                ctx.writeAndFlush(new Errors(String.format(ErrorsConsts.CLIENT_SUB_COMMAND_ERROR)));
        }
    }
}
