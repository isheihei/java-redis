package org.isheihei.redis.core.command.impl.list;

import io.netty.channel.ChannelHandlerContext;
import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.CommandType;
import org.isheihei.redis.core.resp.Resp;
import org.isheihei.redis.core.struct.impl.BytesWrapper;

/**
 * @ClassName: Lpop
 * @Description: 于删除并返回存储在 key 中的列表的第一个元素
 * @Date: 2022/6/10 16:38
 * @Author: isheihei
 */
public class Lpop extends Pop{

    private BytesWrapper key;

    private Resp[] array;

    @Override
    public CommandType type() {
        return CommandType.lpop;
    }

    @Override
    public void setContent(Resp[] array) {
        this.array = array;
    }

    @Override
    public void handle(ChannelHandlerContext ctx, RedisClient redisClient) {
        if ((key = getBytesWrapper(ctx, array, 1)) == null) return;
        lrPop(ctx, redisClient, key, true);
    }
}
