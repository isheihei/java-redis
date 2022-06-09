package org.isheihei.redis.core.command.impl.string;

import io.netty.channel.ChannelHandlerContext;
import org.isheihei.redis.common.consts.ErrorsConsts;
import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.Command;
import org.isheihei.redis.core.command.CommandType;
import org.isheihei.redis.core.obj.impl.RedisStringObject;
import org.isheihei.redis.core.resp.Errors;
import org.isheihei.redis.core.resp.Resp;
import org.isheihei.redis.core.resp.SimpleString;
import org.isheihei.redis.core.struct.impl.BytesWrapper;

/**
 * @ClassName: Set
 * @Description: set key value
 * @Date: 2022/6/9 23:23
 * @Author: isheihei
 */
public class Set implements Command {

    private Resp[] array;

    @Override
    public CommandType type() {
        return CommandType.set;
    }

    @Override
    public void setContent(Resp[] array) {
        this.array = array;
    }

    @Override
    public void handle(ChannelHandlerContext ctx, RedisClient redisClient) {
        BytesWrapper key = getDs(array, 1);
        BytesWrapper value = getDs(array, 2);
        if (key == null || value == null) {
            ctx.writeAndFlush(new Errors(String.format(ErrorsConsts.COMMAND_WRONG_ARGS_NUMBER, type().toString())));
            return;
        }
        RedisStringObject stringObject = new RedisStringObject(value);
        redisClient.getDb().put(key, stringObject);
        ctx.writeAndFlush(new SimpleString("OK"));
    }
}
