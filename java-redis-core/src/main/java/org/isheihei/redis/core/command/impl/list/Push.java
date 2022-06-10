package org.isheihei.redis.core.command.impl.list;

import io.netty.channel.ChannelHandlerContext;
import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.Command;
import org.isheihei.redis.core.command.CommandType;
import org.isheihei.redis.core.db.RedisDB;
import org.isheihei.redis.core.obj.RedisObject;
import org.isheihei.redis.core.obj.impl.RedisListObject;
import org.isheihei.redis.core.resp.Resp;
import org.isheihei.redis.core.resp.RespInt;
import org.isheihei.redis.core.struct.RedisDataStruct;
import org.isheihei.redis.core.struct.impl.BytesWrapper;
import org.isheihei.redis.core.struct.impl.RedisDoubleLinkedList;

import java.util.List;

/**
 * @ClassName: Push
 * @Description: lpush rpush 提取公共父类 降低重复代码
 * @Date: 2022/6/10 16:17
 * @Author: isheihei
 */
public abstract class Push implements Command {

    @Override
    public abstract CommandType type();

    @Override
    public abstract void setContent(Resp[] array);

    @Override
    public abstract void handle(ChannelHandlerContext ctx, RedisClient redisClient);

    /**
     * @Description: 提取列表插入公共方法
     * @Param: ctx
     * @Param: redisClient
     * @Param: true：左，false：右
     * @Author: isheihei
     */
    public void lrPush(ChannelHandlerContext ctx, RedisClient redisClient, boolean direct, BytesWrapper key, List<BytesWrapper> values) {
        if (values.size() == 0) {
            ctx.writeAndFlush(new RespInt(0));
        }
        RedisDB db = redisClient.getDb();
        RedisObject redisObject = db.get(key);
        if (redisObject == null) {
            // 不存在列表 则创建
            redisObject = new RedisListObject();
            db.put(key, redisObject);
        }
        RedisDataStruct data = redisObject.data();
        if (data instanceof RedisDoubleLinkedList) {
            RedisDoubleLinkedList list = (RedisDoubleLinkedList) data;
            if (direct) {
                list.lpush(values);
            } else {
                list.rpush(values);
            }
            ctx.writeAndFlush(new RespInt(list.size()));
        } else {
            throw  new UnsupportedOperationException();
        }
    }

}
