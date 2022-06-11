package org.isheihei.redis.core.command.impl.connection;

import io.netty.channel.ChannelHandlerContext;
import org.isheihei.redis.common.consts.ErrorsConsts;
import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.Command;
import org.isheihei.redis.core.command.CommandType;
import org.isheihei.redis.core.resp.Errors;
import org.isheihei.redis.core.resp.Resp;
import org.isheihei.redis.core.resp.SimpleString;
import org.isheihei.redis.core.struct.impl.BytesWrapper;

/**
 * @ClassName: Select
 * @Description: 切换到指定的数据库
 * @Date: 2022/6/11 16:01
 * @Author: isheihei
 */
public class Select implements Command {

    private Resp[] array;

    private int index;

    @Override
    public CommandType type() {
        return CommandType.select;
    }

    @Override
    public void setContent(Resp[] array) {
        this.array = array;
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
}
