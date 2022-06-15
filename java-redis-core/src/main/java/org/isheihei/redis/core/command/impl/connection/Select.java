package org.isheihei.redis.core.command.impl.connection;

import io.netty.channel.ChannelHandlerContext;
import org.isheihei.redis.common.consts.ErrorsConsts;
import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.CommandType;
import org.isheihei.redis.core.command.WriteCommand;
import org.isheihei.redis.core.resp.impl.Errors;
import org.isheihei.redis.core.resp.impl.SimpleString;
import org.isheihei.redis.core.struct.impl.BytesWrapper;

/**
 * @ClassName: Select
 * @Description: 切换到指定的数据库
 * @Date: 2022/6/11 16:01
 * @Author: isheihei
 */
public class Select extends WriteCommand {

    private int index;

    @Override
    public CommandType type() {
        return CommandType.select;
    }


    @Override
    public void handle(ChannelHandlerContext ctx, RedisClient redisClient) {
        BytesWrapper indexBytes;
        if ((indexBytes = getBytesWrapper(ctx, array, 1)) == null) {
            ctx.writeAndFlush(new Errors(ErrorsConsts.INVALID_DB_INDEX));
            return;
        }
        try {
            index = Integer.parseInt(indexBytes.toUtf8String());
        } catch (NumberFormatException e) {
            LOGGER.error("数据库索引不是整数类型");
            ctx.writeAndFlush(new Errors(ErrorsConsts.INVALID_DB_INDEX));
            return;
        }
        if (redisClient.setDb(index)) {
            ctx.writeAndFlush(SimpleString.OK);
        } else {
            ctx.writeAndFlush(new Errors(ErrorsConsts.INVALID_DB_INDEX));
        }
    }

    @Override
    public void handleWrite(ChannelHandlerContext ctx, RedisClient redisClient) {

    }

    @Override
    public void handleLoadAof(RedisClient redisClient) {
        BytesWrapper indexBytes;
        if ((indexBytes = getBytesWrapper(array, 1)) == null) {
            return;
        }
        try {
            index = Integer.parseInt(indexBytes.toUtf8String());
        } catch (NumberFormatException e) {
            LOGGER.error("数据库索引不是整数类型");
            return;
        }
        redisClient.setDb(index);
    }
}
