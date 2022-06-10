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
 * @ClassName: Rpush
 * @Description: TODO
 * @Date: 2022/6/10 16:24
 * @Author: isheihei
 */
public class Rpush extends Push{

    private BytesWrapper key;
    private List<BytesWrapper> values;

    @Override
    public CommandType type() {
        return CommandType.rpush;
    }

    @Override
    public void setContent(Resp[] array) {
        key = getDs(array, 1);
        values = Arrays.stream(array).skip(2).map(resp -> ((BulkString) resp).getContent()).collect(Collectors.toList());
    }

    @Override
    public void handle(ChannelHandlerContext ctx, RedisClient redisClient) {
        lrPush(ctx, redisClient, false, key, values);
    }
}
