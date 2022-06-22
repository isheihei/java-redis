package org.isheihei.redis.core.command.impl.key;

import io.netty.channel.ChannelHandlerContext;
import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.AbstractWriteCommand;
import org.isheihei.redis.core.command.CommandType;
import org.isheihei.redis.core.resp.impl.RespInt;
import org.isheihei.redis.core.struct.impl.BytesWrapper;

/**
 * @ClassName: Persist
 * @Description: 删除给定 key 的过期时间，使得 key 永不过期
 * @Date: 2022/6/11 15:44
 * @Author: isheihei
 */
public class Persist extends AbstractWriteCommand {

    private BytesWrapper key;

    @Override
    public CommandType type() {
        return CommandType.persist;
    }

    @Override
    public void handleWrite(ChannelHandlerContext ctx, RedisClient redisClient) {
        if ((key = getBytesWrapper(ctx, array, 1)) == null) {
            return;
        }
        int persist = redisClient.getDb().persist(key);
        ctx.writeAndFlush(new RespInt(persist));
    }

    @Override
    public void handleLoadAof(RedisClient redisClient) {
        if ((key = getBytesWrapper(array, 1)) == null) {
            return;
        }
        redisClient.getDb().persist(key);
    }
}
