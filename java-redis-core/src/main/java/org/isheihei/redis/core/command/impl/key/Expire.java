package org.isheihei.redis.core.command.impl.key;

import io.netty.channel.ChannelHandlerContext;
import org.isheihei.redis.common.consts.ErrorsConsts;
import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.Command;
import org.isheihei.redis.core.command.CommandType;
import org.isheihei.redis.core.db.RedisDB;
import org.isheihei.redis.core.resp.Errors;
import org.isheihei.redis.core.resp.Resp;
import org.isheihei.redis.core.resp.RespInt;
import org.isheihei.redis.core.struct.impl.BytesWrapper;

import java.util.concurrent.TimeUnit;

/**
 * @ClassName: Expire
 * @Description: TODO
 * @Date: 2022/6/11 15:42
 * @Author: isheihei
 */
public class Expire implements Command {

    private Resp[] array;

    private BytesWrapper key;

    private long expireAt;

    @Override
    public CommandType type() {
        return CommandType.expire;
    }

    @Override
    public void setContent(Resp[] array) {
        this.array = array;
    }

    @Override
    public void handle(ChannelHandlerContext ctx, RedisClient redisClient) {
        if ((key = getBytesWrapper(ctx, array, 1)) == null) {
            return;
        }
        BytesWrapper bytesExpireAt;
        if ((bytesExpireAt = getBytesWrapper(ctx, array, 2)) == null) {
            return;
        }
        try {
            long timeout = Long.parseLong(bytesExpireAt.toUtf8String());
            // TODO aof 重写时要使用绝对时间 不能使用相对时间
            expireAt = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(timeout);
        } catch (NumberFormatException e) {
            LOGGER.error("参数无法转换为时间戳", e);
            ctx.writeAndFlush(new Errors(ErrorsConsts.VALUE_IS_NOT_INT));
            return;
        }
        RedisDB db = redisClient.getDb();
        if (db.get(key) == null) {
            ctx.writeAndFlush(new RespInt(0));
            return;
        }
        int res = db.expire(key, expireAt);
        ctx.writeAndFlush(new RespInt(res));
    }
}
