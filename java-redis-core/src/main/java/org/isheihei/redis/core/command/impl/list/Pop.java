package org.isheihei.redis.core.command.impl.list;

import io.netty.channel.ChannelHandlerContext;
import org.isheihei.redis.common.consts.ErrorsConst;
import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.CommandType;
import org.isheihei.redis.core.command.AbstractWriteCommand;
import org.isheihei.redis.core.db.RedisDB;
import org.isheihei.redis.core.obj.RedisObject;
import org.isheihei.redis.core.obj.impl.RedisListObject;
import org.isheihei.redis.core.resp.impl.BulkString;
import org.isheihei.redis.core.resp.impl.Errors;
import org.isheihei.redis.core.struct.RedisDataStruct;
import org.isheihei.redis.core.struct.impl.BytesWrapper;
import org.isheihei.redis.core.struct.impl.RedisDoubleLinkedList;

/**
 * @ClassName: Pop
 * @Description: lpop rpop 提取公共父类 降低重复代码
 * @Date: 2022/6/10 16:30
 * @Author: isheihei
 */
public abstract class Pop extends AbstractWriteCommand {
    @Override
    public abstract CommandType type();

    @Override
    public abstract void handleWrite(ChannelHandlerContext ctx, RedisClient redisClient);

    @Override
    public abstract void handleLoadAof(RedisClient redisClient);

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
            ctx.writeAndFlush(new Errors(ErrorsConst.WRONG_TYPE_OPERATION));
        }
    }

    public void lrPop(RedisClient redisClient, BytesWrapper key, boolean direct) {
        RedisDB db = redisClient.getDb();
        RedisObject redisObject = db.get(key);
        if (redisObject == null) {
            return;
        }
        if (redisObject instanceof RedisListObject) {
            RedisDataStruct data = redisObject.data();
            if (data instanceof RedisDoubleLinkedList) {
                BytesWrapper res = null;
                RedisDoubleLinkedList list = (RedisDoubleLinkedList) data;
                if (direct) {
                    list.lpop();
                } else {
                    list.rpop();
                }
            } else {
                throw new UnsupportedOperationException();
            }
        }
    }
}
