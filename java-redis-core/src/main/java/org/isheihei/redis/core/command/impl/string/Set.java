package org.isheihei.redis.core.command.impl.string;

import io.netty.channel.ChannelHandlerContext;
import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.Command;
import org.isheihei.redis.core.command.CommandType;
import org.isheihei.redis.core.obj.impl.RedisStringObject;
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

    private BytesWrapper key;

    private BytesWrapper value;

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
        if ((key = getBytesWrapper(ctx, array, 1)) == null) {
            return;
        }
        if ((value = getBytesWrapper(ctx, array, 2)) == null) {
            return;
        }
        RedisStringObject stringObject = new RedisStringObject(value);
        redisClient.getDb().put(key, stringObject);
        ctx.writeAndFlush(SimpleString.OK);
    }
}
