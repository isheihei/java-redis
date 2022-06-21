package org.isheihei.redis.core.command.impl.zset;

import io.netty.channel.ChannelHandlerContext;
import org.isheihei.redis.common.consts.ErrorsConst;
import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.AbstractCommand;
import org.isheihei.redis.core.command.CommandType;
import org.isheihei.redis.core.obj.RedisObject;
import org.isheihei.redis.core.obj.impl.RedisZSetObject;
import org.isheihei.redis.core.resp.impl.BulkString;
import org.isheihei.redis.core.resp.impl.Errors;
import org.isheihei.redis.core.struct.RedisDataStruct;
import org.isheihei.redis.core.struct.impl.BytesWrapper;
import org.isheihei.redis.core.struct.impl.RedisZSet;

/**
 * @ClassName: ZScore
 * @Description: 返回有序集 key.中成员 member 的分数
 * @Date: 2022/6/11 15:58
 * @Author: isheihei
 */
public class ZScore extends AbstractCommand {

    private BytesWrapper key;

    private BytesWrapper member;

    @Override
    public CommandType type() {
        return CommandType.zscore;
    }

    @Override
    public void handle(ChannelHandlerContext ctx, RedisClient redisClient) {
        if ((key = getBytesWrapper(ctx, array, 1)) == null) {
            return;
        }
        if ((member = getBytesWrapper(ctx, array, 2)) == null) {
            return;
        }

        RedisObject redisObject = redisClient.getDb().get(key);
        if (redisObject == null) {
            ctx.writeAndFlush(BulkString.NullBulkString);
            return;
        }

        if (redisObject instanceof RedisZSetObject) {
            RedisDataStruct data = redisObject.data();
            if (data instanceof RedisZSet) {
                RedisZSet zSet = (RedisZSet) data;
                Double score = zSet.zSCore(member);
                if (score != null) {
                    ctx.writeAndFlush(new BulkString(new BytesWrapper(String.valueOf(score).getBytes(CHARSET))));
                    return;
                } else {
                    ctx.writeAndFlush(BulkString.NullBulkString);
                    return;
                }
            } else {
                throw new UnsupportedOperationException();
            }
        } else {
            ctx.writeAndFlush(new Errors(ErrorsConst.WRONG_TYPE_OPERATION));
        }
    }
}
