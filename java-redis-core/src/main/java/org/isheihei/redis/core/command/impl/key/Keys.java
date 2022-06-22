package org.isheihei.redis.core.command.impl.key;

import io.netty.channel.ChannelHandlerContext;
import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.AbstractCommand;
import org.isheihei.redis.core.command.CommandType;
import org.isheihei.redis.core.resp.Resp;
import org.isheihei.redis.core.resp.impl.BulkString;
import org.isheihei.redis.core.resp.impl.RespArray;
import org.isheihei.redis.core.struct.impl.BytesWrapper;

import java.util.Set;

/**
 * @ClassName: Keys
 * @Description: 返回所有 keys
 * @Date: 2022/6/11 15:42
 * @Author: isheihei
 */
public class Keys extends AbstractCommand {
    @Override
    public CommandType type() {
        return CommandType.keys;
    }

    @Override
    public void handle(ChannelHandlerContext ctx, RedisClient redisClient) {
        Set<BytesWrapper> keys = redisClient.getDb().keys();
        ctx.writeAndFlush(new RespArray(keys.stream().map(BulkString::new).toArray(Resp[]::new)));
    }
}
