package org.isheihei.redis.core.command.impl.list;

import io.netty.channel.ChannelHandlerContext;
import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.CommandType;
import org.isheihei.redis.core.struct.impl.BytesWrapper;

/**
 * @ClassName: RPop
 * @Description: 在 key 中的列表的尾部插入所有指定的值
 * @Date: 2022/6/10 16:38
 * @Author: isheihei
 */
public class RPop extends Pop{
    private BytesWrapper key;

    @Override
    public CommandType type() {
        return CommandType.rpop;
    }

    @Override
    public void handleWrite(ChannelHandlerContext ctx, RedisClient redisClient) {
        if ((key = getBytesWrapper(ctx, array, 1)) == null) return;
        lrPop(ctx, redisClient, key, false);
    }

    @Override
    public void handleLoadAof(RedisClient redisClient) {
        if ((key = getBytesWrapper(array, 1)) == null) return;
        lrPop(redisClient, key, false);
    }
}
