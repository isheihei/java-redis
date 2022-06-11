package org.isheihei.redis.core.command.impl.list;

import io.netty.channel.ChannelHandlerContext;
import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.CommandType;
import org.isheihei.redis.core.resp.Resp;
import org.isheihei.redis.core.struct.impl.BytesWrapper;

/**
 * @ClassName: Rpop
 * @Description: 在 key 中的列表的尾部插入所有指定的值
 * @Date: 2022/6/10 16:38
 * @Author: isheihei
 */
public class Rpop extends Pop{
    private BytesWrapper key;

    private Resp[] array;

    @Override
    public CommandType type() {
        return CommandType.rpop;
    }

    @Override
    public void setContent(Resp[] array) {
        this.array = array;
    }

    @Override
    public void handle(ChannelHandlerContext ctx, RedisClient redisClient) {
        if ((key = getBytesWrapper(ctx, array, 1)) == null) return;
        lrPop(ctx, redisClient, key, false);
    }
}
