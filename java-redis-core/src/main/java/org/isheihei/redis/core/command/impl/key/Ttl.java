package org.isheihei.redis.core.command.impl.key;

import io.netty.channel.ChannelHandlerContext;
import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.AbstractCommand;
import org.isheihei.redis.core.command.CommandType;
import org.isheihei.redis.core.db.RedisDB;
import org.isheihei.redis.core.obj.RedisObject;
import org.isheihei.redis.core.resp.impl.RespInt;
import org.isheihei.redis.core.struct.impl.BytesWrapper;

import java.util.concurrent.TimeUnit;

/**
 * @ClassName: Ttl
 * @Description: 以秒为单位返回 key 的剩余过期时间
 * @Date: 2022/6/11 15:47
 * @Author: isheihei
 */
public class Ttl extends AbstractCommand {

    private BytesWrapper key;

    @Override
    public CommandType type() {
        return CommandType.ttl;
    }

    @Override
    public void handle(ChannelHandlerContext ctx, RedisClient redisClient) {
        if ((key = getBytesWrapper(ctx, array, 1)) == null) {
            return;
        }
        RedisDB db = redisClient.getDb();
        RedisObject redisObject = db.get(key);
        if (redisObject == null) {
            ctx.writeAndFlush(new RespInt(-2));
            return;
        }
        Long ttl = db.getTtl(key);
        if (ttl == null) {
            ctx.writeAndFlush(new RespInt(-1));
        } else {
            ctx.writeAndFlush(new RespInt(((int) TimeUnit.MILLISECONDS.toSeconds(ttl - System.currentTimeMillis()))));
        }
    }
}
