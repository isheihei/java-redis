package org.isheihei.redis.core.command.impl.string;

import io.netty.channel.ChannelHandlerContext;
import org.isheihei.redis.common.consts.ErrorsConsts;
import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.Command;
import org.isheihei.redis.core.command.CommandType;
import org.isheihei.redis.core.obj.RedisObject;
import org.isheihei.redis.core.obj.impl.RedisStringObject;
import org.isheihei.redis.core.resp.BulkString;
import org.isheihei.redis.core.resp.Errors;
import org.isheihei.redis.core.resp.Resp;
import org.isheihei.redis.core.struct.RedisDataStruct;
import org.isheihei.redis.core.struct.impl.BytesWrapper;
import org.isheihei.redis.core.struct.impl.RedisDynamicString;

/**
 * @ClassName: Get
 * @Description: 获取指定 key 的值
 * @Date: 2022/6/9 23:22
 * @Author: isheihei
 */
public class Get implements Command {

    private BytesWrapper key;

    @Override
    public CommandType type() {
        return CommandType.get;
    }

    @Override
    public void setContent(Resp[] array) {
        key = getBytesWrapper(array, 1);
    }

    @Override
    public void handle(ChannelHandlerContext ctx, RedisClient redisClient) {
        RedisObject redisObject = redisClient.getDb().get(key);
        if (redisObject == null) {
            ctx.writeAndFlush(BulkString.NullBulkString);
            return;
        }

        if (redisObject instanceof RedisStringObject) {
            RedisDataStruct data = redisObject.data();
            if (data instanceof RedisDynamicString) {
                RedisDynamicString value = (RedisDynamicString) data;
                ctx.writeAndFlush(new BulkString(value.getValue()));
            } else {
                throw new UnsupportedOperationException();
            }
        } else {
            ctx.writeAndFlush(new Errors(ErrorsConsts.WRONG_TYPE_OPERATION));
        }
    }
}
