package org.isheihei.redis.core.command.impl.zset;

import io.netty.channel.ChannelHandlerContext;
import org.isheihei.redis.common.consts.ErrorsConst;
import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.AbstractCommand;
import org.isheihei.redis.core.command.CommandType;
import org.isheihei.redis.core.obj.RedisObject;
import org.isheihei.redis.core.obj.impl.RedisZSetObject;
import org.isheihei.redis.core.resp.impl.Errors;
import org.isheihei.redis.core.resp.impl.RespInt;
import org.isheihei.redis.core.struct.RedisDataStruct;
import org.isheihei.redis.core.struct.impl.BytesWrapper;
import org.isheihei.redis.core.struct.impl.RedisZSet;

/**
 * @ClassName: ZCount
 * @Description: 有序集 key 中， score 值在 min 和 max 之间(默认包括 score 值等于 min 但不包括 max )的成员的数量
 * @Date: 2022/6/11 15:52
 * @Author: isheihei
 */
public class ZCount extends AbstractCommand {

    private BytesWrapper key;

    private double min;

    private double max;

    @Override
    public CommandType type() {
        return CommandType.zcount;
    }

    @Override
    public void handle(ChannelHandlerContext ctx, RedisClient redisClient) {
        if ((key = getBytesWrapper(ctx, array, 1)) == null) {
            return;
        }
        BytesWrapper minBytes;
        BytesWrapper maxBytes;
        if ((minBytes = getBytesWrapper(ctx, array, 2)) == null) return;
        if ((maxBytes = getBytesWrapper(ctx, array, 3)) == null) return;
        try {
            min = Double.parseDouble(minBytes.toUtf8String());
            max = Double.parseDouble(maxBytes.toUtf8String());
        } catch (NumberFormatException e) {
            LOGGER.error("参数无法转换为数字", e);
            ctx.writeAndFlush(new Errors(ErrorsConst.MIN_OR_MAX_NOT_FLOAT));
            return;
        }

        RedisObject redisObject = redisClient.getDb().get(key);
        if (redisObject == null) {
            ctx.writeAndFlush(new RespInt(0));
            return;
        }
        if (redisObject instanceof RedisZSetObject) {
            RedisDataStruct data = redisObject.data();
            if (data instanceof RedisZSet) {
                RedisZSet zSet = (RedisZSet) data;
                int res = zSet.zCount(min, max);
                ctx.writeAndFlush(new RespInt(res));
            } else {
                throw new UnsupportedOperationException();
            }
        } else {
            ctx.writeAndFlush(new Errors(ErrorsConst.WRONG_TYPE_OPERATION));
        }
    }
}
