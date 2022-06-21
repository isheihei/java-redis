package org.isheihei.redis.core.command.impl.string;

import io.netty.channel.ChannelHandlerContext;
import org.isheihei.redis.common.consts.ErrorsConst;
import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.AbstractCommand;
import org.isheihei.redis.core.command.CommandType;
import org.isheihei.redis.core.db.RedisDB;
import org.isheihei.redis.core.obj.RedisObject;
import org.isheihei.redis.core.obj.impl.RedisStringObject;
import org.isheihei.redis.core.resp.impl.BulkString;
import org.isheihei.redis.core.resp.impl.Errors;
import org.isheihei.redis.core.struct.RedisDataStruct;
import org.isheihei.redis.core.struct.impl.BytesWrapper;
import org.isheihei.redis.core.struct.impl.RedisString;

/**
 * @ClassName: Get
 * @Description: 获取指定 key 的值
 * @Date: 2022/6/9 23:22
 * @Author: isheihei
 */
public class Get  extends AbstractCommand {

    private BytesWrapper key;

    @Override
    public CommandType type() {
        return CommandType.get;
    }


    @Override
    public void handle(ChannelHandlerContext ctx, RedisClient redisClient) {
        if ((key = getBytesWrapper(ctx, array, 1)) == null) {
            return;
        }
        RedisDB db = redisClient.getDb();
        RedisObject redisObject = db.get(key);
        if (redisObject == null) {
            ctx.writeAndFlush(BulkString.NullBulkString);
            return;
        }

        if (redisObject instanceof RedisStringObject) {
            RedisDataStruct data = redisObject.data();
            if (data instanceof RedisString) {
                RedisString value = (RedisString) data;
                ctx.writeAndFlush(new BulkString(value.getValue()));
            } else {
                throw new UnsupportedOperationException();
            }
        } else {
            ctx.writeAndFlush(new Errors(ErrorsConst.WRONG_TYPE_OPERATION));
        }
    }
}
