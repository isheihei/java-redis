package org.isheihei.redis.core.command.impl.set;

import io.netty.channel.ChannelHandlerContext;
import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.CommandType;

/**
 * @ClassName: SUnionStore
 * @Description: SUNION 并保存
 * @Date: 2022/6/11 15:39
 * @Author: isheihei
 */
public class SUnionStore extends SetStoreCommand{
    @Override
    public CommandType type() {
        return CommandType.sunionstore;
    }

    @Override
    public void handleWrite(ChannelHandlerContext ctx, RedisClient redisClient) {
        setsStoreCommand(ctx, redisClient, 2);
    }

    @Override
    public void handleLoadAof(RedisClient redisClient) {
        loadRdbSetsStoreCommand(redisClient, 2);
    }
}
