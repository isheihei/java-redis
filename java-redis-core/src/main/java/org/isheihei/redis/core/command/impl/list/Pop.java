package org.isheihei.redis.core.command.impl.list;

import io.netty.channel.ChannelHandlerContext;
import org.isheihei.redis.common.consts.ErrorsConsts;
import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.Command;
import org.isheihei.redis.core.command.CommandType;
import org.isheihei.redis.core.db.RedisDB;
import org.isheihei.redis.core.obj.RedisObject;
import org.isheihei.redis.core.obj.impl.RedisListObject;
import org.isheihei.redis.core.resp.BulkString;
import org.isheihei.redis.core.resp.Errors;
import org.isheihei.redis.core.resp.Resp;
import org.isheihei.redis.core.struct.RedisDataStruct;
import org.isheihei.redis.core.struct.impl.BytesWrapper;
import org.isheihei.redis.core.struct.impl.RedisDoubleLinkedList;

/**
 * @ClassName: Pop
 * @Description: TODO
 * @Date: 2022/6/10 16:30
 * @Author: isheihei
 */
public abstract class Pop implements Command {
    @Override
    public abstract CommandType type();

    @Override
    public abstract void setContent(Resp[] array);

    @Override
    public abstract void handle(ChannelHandlerContext ctx, RedisClient redisClient);

    public void lrPop(ChannelHandlerContext ctx, RedisClient redisClient, BytesWrapper key, boolean direct) {
        RedisDB db = redisClient.getDb();
        RedisObject redisObject = db.get(key);
        if (redisObject == null) {
            ctx.writeAndFlush(BulkString.NullBulkString);
            return;
        }
        if (redisObject instanceof RedisListObject) {
            RedisDataStruct data = redisObject.data();
            if (data instanceof RedisDoubleLinkedList) {
                BytesWrapper res = null;
                RedisDoubleLinkedList list = (RedisDoubleLinkedList) data;
                if (direct) {
                    res = list.lpop();
                } else {
                    res = list.rpop();
                }
                if (res == null) {
                    ctx.writeAndFlush(BulkString.NullBulkString);
                } else {
                    ctx.writeAndFlush(new BulkString(res));
                }
            } else {
                throw new UnsupportedOperationException();
            }
        } else {
            ctx.writeAndFlush(new Errors(ErrorsConsts.WRONG_TYPE_OPERATION));
        }


    }
}
