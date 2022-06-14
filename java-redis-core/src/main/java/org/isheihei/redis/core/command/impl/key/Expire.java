package org.isheihei.redis.core.command.impl.key;

import io.netty.channel.ChannelHandlerContext;
import org.isheihei.redis.common.consts.ErrorsConsts;
import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.CommandType;
import org.isheihei.redis.core.command.WriteCommand;
import org.isheihei.redis.core.db.RedisDB;
import org.isheihei.redis.core.resp.BulkString;
import org.isheihei.redis.core.resp.Errors;
import org.isheihei.redis.core.resp.RespArray;
import org.isheihei.redis.core.resp.RespInt;
import org.isheihei.redis.core.struct.impl.BytesWrapper;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName: Expire
 * @Description: TODO
 * @Date: 2022/6/11 15:42
 * @Author: isheihei
 */
public class Expire extends WriteCommand {

    private BytesWrapper key;

    private long expireAt;

    @Override
    public CommandType type() {
        return CommandType.expire;
    }

    @Override
    public void handleWrite(ChannelHandlerContext ctx, RedisClient redisClient) {
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

    @Override
    public void handleLoadAof(RedisClient redisClient) {
        if ((key = getBytesWrapper(array, 1)) == null) {
            return;
        }
        BytesWrapper bytesExpireAt;
        if ((bytesExpireAt = getBytesWrapper(array, 2)) == null) {
            return;
        }
        try {
            long timeout = Long.parseLong(bytesExpireAt.toUtf8String());
            // aof 载入时直接载入绝对时间
            expireAt = timeout;
        } catch (NumberFormatException e) {
            LOGGER.error("参数无法转换为时间戳", e);
            return;
        }
        RedisDB db = redisClient.getDb();
        if (db.get(key) == null) {
            return;
        }
        int res = db.expire(key, expireAt);
    }

    @Override
    public void putAof() {
        // aof 写入时转换为绝对时间
        array[2] = new BulkString(new BytesWrapper(String.valueOf(expireAt).getBytes(StandardCharsets.UTF_8)));
        getAof().put(new RespArray(array));
    }
}
