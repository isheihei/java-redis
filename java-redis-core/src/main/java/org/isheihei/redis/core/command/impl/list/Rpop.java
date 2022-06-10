package org.isheihei.redis.core.command.impl.list;

import io.netty.channel.ChannelHandlerContext;
import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.CommandType;
import org.isheihei.redis.core.resp.Resp;
import org.isheihei.redis.core.struct.impl.BytesWrapper;

/**
 * @ClassName: Rpop
 * @Description: 移除并返回列表最后一个元素的值
 * @Date: 2022/6/10 16:38
 * @Author: isheihei
 */
public class Rpop extends Pop{
    private BytesWrapper key;

    @Override
    public CommandType type() {
        return CommandType.rpop;
    }

    @Override
    public void setContent(Resp[] array) {
        key = getDs(array, 1);
    }

    @Override
    public void handle(ChannelHandlerContext ctx, RedisClient redisClient) {
        lrPop(ctx, redisClient, key, false);
    }
}
