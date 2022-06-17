package org.isheihei.redis.core.command.impl.string;

import io.netty.channel.ChannelHandlerContext;
import org.isheihei.redis.common.consts.ErrorsConst;
import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.CommandType;
import org.isheihei.redis.core.command.WriteCommand;
import org.isheihei.redis.core.db.RedisDB;
import org.isheihei.redis.core.obj.RedisObject;
import org.isheihei.redis.core.obj.impl.RedisStringObject;
import org.isheihei.redis.core.resp.impl.Errors;
import org.isheihei.redis.core.resp.impl.RespInt;
import org.isheihei.redis.core.struct.RedisDataStruct;
import org.isheihei.redis.core.struct.impl.BytesWrapper;
import org.isheihei.redis.core.struct.impl.RedisDynamicString;

/**
 * @ClassName: Append
 * @Description: 为指定的 key 追加值
 * @Date: 2022/6/11 15:21
 * @Author: isheihei
 */
public class Append extends WriteCommand {

    private BytesWrapper key;

    private BytesWrapper value;

    @Override
    public CommandType type() {
        return CommandType.append;
    }

    @Override
    public void handleWrite(ChannelHandlerContext ctx, RedisClient redisClient) {
        if ((key = getBytesWrapper(ctx, array, 1)) == null) {
            return;
        }
        if ((value = getBytesWrapper(ctx, array, 2)) == null) {
            return;
        }
        RedisDB db = redisClient.getDb();
        RedisObject redisObject = db.get(key);
        if (redisObject == null) {
            db.put(key, new RedisStringObject(value));
            ctx.writeAndFlush(new RespInt(value.length()));
        }else {
            if (redisObject instanceof RedisStringObject) {
                RedisDataStruct data = redisObject.data();
                if (data instanceof RedisDynamicString) {
                    RedisDynamicString string = (RedisDynamicString) data;
                    int length = string.append(value);
                    ctx.writeAndFlush(new RespInt(length));
                } else {
                    throw new UnsupportedOperationException();
                }
            } else{
                ctx.writeAndFlush(new Errors(ErrorsConst.WRONG_TYPE_OPERATION));
            }
        }
    }

    @Override
    public void handleLoadAof(RedisClient redisClient) {
        if ((key = getBytesWrapper(array, 1)) == null) {
            return;
        }
        if ((value = getBytesWrapper(array, 2)) == null) {
            return;
        }
        RedisDB db = redisClient.getDb();
        RedisObject redisObject = db.get(key);
        if (redisObject == null) {
            db.put(key, new RedisStringObject(value));
        }else {
            if (redisObject instanceof RedisStringObject) {
                RedisDataStruct data = redisObject.data();
                if (data instanceof RedisDynamicString) {
                    RedisDynamicString string = (RedisDynamicString) data;
                    int length = string.append(value);
                } else {
                    throw new UnsupportedOperationException();
                }
            }
        }
    }
}
