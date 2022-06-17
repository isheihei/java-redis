package org.isheihei.redis.core.command.impl.set;

import io.netty.channel.ChannelHandlerContext;
import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.CommandType;

/**
 * @ClassName: SDiffStore
 * @Description: SDIFF 并保存
 * @Date: 2022/6/11 15:28
 * @Author: isheihei
 */
public class SDiffStore extends SetStoreCommand {

    @Override
    public CommandType type() {
        return CommandType.sdiffstore;
    }

    @Override
    public void handleWrite(ChannelHandlerContext ctx, RedisClient redisClient) {
       setsStoreCommand(ctx, redisClient, 0);
    }

    @Override
    public void handleLoadAof(RedisClient redisClient) {
       loadRdbSetsStoreCommand(redisClient, 0);
    }
}
