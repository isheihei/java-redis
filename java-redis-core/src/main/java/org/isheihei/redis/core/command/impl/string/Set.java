package org.isheihei.redis.core.command.impl.string;

import io.netty.channel.ChannelHandlerContext;
import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.CommandType;
import org.isheihei.redis.core.command.AbstractWriteCommand;
import org.isheihei.redis.core.obj.impl.RedisStringObject;
import org.isheihei.redis.core.resp.impl.SimpleString;
import org.isheihei.redis.core.struct.impl.BytesWrapper;

/**
 * @ClassName: Set
 * @Description: 将键 key 设定为指定的“字符串”值
 * @Date: 2022/6/9 23:23
 * @Author: isheihei
 */
public class Set extends AbstractWriteCommand {

    private BytesWrapper key;

    private BytesWrapper value;

    @Override
    public CommandType type() {
        return CommandType.set;
    }

    @Override
    public void handleWrite(ChannelHandlerContext ctx, RedisClient redisClient) {
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

    @Override
    public void handleLoadAof(RedisClient redisClient) {
        if ((key = getBytesWrapper(array, 1)) == null) {
            return;
        }
        if ((value = getBytesWrapper(array, 2)) == null) {
            return;
        }
        RedisStringObject stringObject = new RedisStringObject(value);
        redisClient.getDb().put(key, stringObject);
    }
}
