package org.isheihei.redis.core.command.impl.string;

import io.netty.channel.ChannelHandlerContext;
import org.isheihei.redis.common.consts.ErrorsConst;
import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.AbstractWriteCommand;
import org.isheihei.redis.core.command.CommandType;
import org.isheihei.redis.core.db.RedisDB;
import org.isheihei.redis.core.obj.impl.RedisStringObject;
import org.isheihei.redis.core.resp.impl.Errors;
import org.isheihei.redis.core.resp.impl.RespInt;
import org.isheihei.redis.core.struct.impl.BytesWrapper;
import org.isheihei.redis.core.struct.impl.RedisString;

import java.util.concurrent.TimeUnit;

/**
 * @ClassName: SetEx
 * @Description: 键 key 的值设置为 value ， 并将键 key 的生存时间设置为 seconds 秒钟
 * @Date: 2022/6/9 23:24
 * @Author: isheihei
 */
public class SetEx extends AbstractWriteCommand {

    private BytesWrapper key;

    private BytesWrapper value;

    private long expireAt;

    @Override
    public CommandType type() {
        return CommandType.setex;
    }

    @Override
    public void handleWrite(ChannelHandlerContext ctx, RedisClient redisClient) {
        if ((key = getBytesWrapper(ctx, array, 1)) == null) {
            return;
        }

        if ((value = getBytesWrapper(ctx, array, 2)) == null) {
            return;
        }

        BytesWrapper bytesExpireAt;
        if ((bytesExpireAt = getBytesWrapper(ctx, array, 3)) == null) {
            return;
        }
        try {
            long timeout = Long.parseLong(bytesExpireAt.toUtf8String());
            expireAt = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(timeout);
        } catch (NumberFormatException e) {
            LOGGER.error("参数无法转换为时间戳", e);
            ctx.writeAndFlush(new Errors(ErrorsConst.VALUE_IS_NOT_INT));
            return;
        }

        RedisDB db = redisClient.getDb();
        RedisStringObject redisStringObject = new RedisStringObject();
        ((RedisString) redisStringObject.data()).setValue(value);
        db.put(key, redisStringObject);
        int res = db.expire(key, expireAt);
        ctx.writeAndFlush(new RespInt(res));
    }

    @Override
    public void handleLoadAof(RedisClient redisClient) {
        if ((key = getBytesWrapper(array, 1)) == null) {
            return;
        }

        if ((value = getBytesWrapper(array, 2)) == null) {
            return;
        }

        BytesWrapper bytesExpireAt;
        if ((bytesExpireAt = getBytesWrapper(array, 3)) == null) {
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
        RedisStringObject redisStringObject = new RedisStringObject();
        ((RedisString) redisStringObject.data()).setValue(value);
        db.put(key, redisStringObject);
        db.expire(key, expireAt);
    }
}
