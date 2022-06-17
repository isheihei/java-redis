package org.isheihei.redis.core.command.impl.set;

import io.netty.channel.ChannelHandlerContext;
import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.CommandType;

/**
 * @ClassName: SInterStore
 * @Description: SINTER 并保存
 * @Date: 2022/6/11 15:37
 * @Author: isheihei
 */
public class SInterStore extends SetStoreCommand {

    @Override
    public CommandType type() {
        return CommandType.sinterstore;
    }

    @Override
    public void handleWrite(ChannelHandlerContext ctx, RedisClient redisClient) {
        setsStoreCommand(ctx, redisClient, 1);
    }

    @Override
    public void handleLoadAof(RedisClient redisClient) {
        loadRdbSetsStoreCommand(redisClient, 1);
    }

}
