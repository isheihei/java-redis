package org.isheihei.redis.core.command.impl.list;

import io.netty.channel.ChannelHandlerContext;
import org.isheihei.redis.common.consts.ErrorsConsts;
import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.Command;
import org.isheihei.redis.core.command.CommandType;
import org.isheihei.redis.core.db.RedisDB;
import org.isheihei.redis.core.obj.RedisObject;
import org.isheihei.redis.core.obj.impl.RedisListObject;
import org.isheihei.redis.core.resp.BulkString;
import org.isheihei.redis.core.resp.Errors;
import org.isheihei.redis.core.resp.Resp;
import org.isheihei.redis.core.resp.RespArray;
import org.isheihei.redis.core.struct.RedisDataStruct;
import org.isheihei.redis.core.struct.impl.BytesWrapper;
import org.isheihei.redis.core.struct.impl.RedisDoubleLinkedList;

import java.util.List;

/**
 * @ClassName: Lrange
 * @Description: 返回列表中指定区间内的元素
 * @Date: 2022/6/10 15:43
 * @Author: isheihei
 */
public class Lrange implements Command {

    private BytesWrapper key;
    private int          start;
    private int          end;

    private Resp[] array;

    @Override
    public CommandType type() {
        return CommandType.lrange;
    }

    @Override
    public void setContent(Resp[] array) {
        this.array = array;
    }

    @Override
    public void handle(ChannelHandlerContext ctx, RedisClient redisClient) {
        if ((key = getBytesWrapper(ctx, array, 1)) == null) return;
        BytesWrapper startString;
        if ((startString = getBytesWrapper(ctx, array, 2)) == null) return;
        BytesWrapper endString;
        if ((endString = getBytesWrapper(ctx, array, 3)) == null) return;
        try {
            start = Integer.parseInt(startString.toUtf8String());
            end = Integer.parseInt(endString.toUtf8String());
        } catch (NumberFormatException e) {
            LOGGER.error("参数无法转换为数字", e);
            ctx.writeAndFlush(new Errors(ErrorsConsts.VALUE_IS_NOT_INT));
            return;
        }
        RedisDB db = redisClient.getDb();
        RedisObject redisObject = db.get(key);
        if (redisObject == null) {
            ctx.writeAndFlush(new RespArray(new Resp[0]));
        } else if (redisObject instanceof RedisListObject) {
            RedisDataStruct data = redisObject.data();
            if (data instanceof RedisDoubleLinkedList) {
                RedisDoubleLinkedList list = (RedisDoubleLinkedList) data;
                List<BytesWrapper> range = list.lrange(start, end);
                ctx.writeAndFlush(new RespArray(range.stream().map(BulkString::new).toArray(Resp[]::new)));
            } else {
                throw new UnsupportedOperationException();
            }
        } else {
            ctx.writeAndFlush(new Errors(ErrorsConsts.WRONG_TYPE_OPERATION));
            return;
        }

    }
}
