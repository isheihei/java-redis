package org.isheihei.redis.core.command.impl.list;

import io.netty.channel.ChannelHandlerContext;
import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.CommandType;
import org.isheihei.redis.core.resp.BulkString;
import org.isheihei.redis.core.resp.Resp;
import org.isheihei.redis.core.struct.impl.BytesWrapper;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName: Lpush
 * @Description: 列表头添加元素，不存在则创建
 * @Date: 2022/6/10 15:43
 * @Author: isheihei
 */
public class Lpush extends Push {

    private BytesWrapper key;
    private List<BytesWrapper> values;

    private Resp[] array;

    @Override
    public CommandType type() {
        return CommandType.lpush;
    }

    @Override
    public void setContent(Resp[] array) {
        this.array = array;

    }

    @Override
    public void handle(ChannelHandlerContext ctx, RedisClient redisClient) {
        if ((key = getBytesWrapper(ctx, array, 1)) == null) return;
        values = Arrays.stream(array).skip(2).map(resp -> ((BulkString) resp).getContent()).collect(Collectors.toList());
        lrPush(ctx, redisClient, true, key, values);
    }
}
