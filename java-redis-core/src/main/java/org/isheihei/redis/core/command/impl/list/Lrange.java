package org.isheihei.redis.core.command.impl.list;

import io.netty.channel.ChannelHandlerContext;
import org.isheihei.redis.common.consts.ErrorsConsts;
import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.Command;
import org.isheihei.redis.core.command.CommandType;
import org.isheihei.redis.core.db.RedisDB;
import org.isheihei.redis.core.obj.RedisObject;
import org.isheihei.redis.core.obj.impl.RedisListObject;
import org.isheihei.redis.core.resp.*;
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
        key = getKey(ctx, array, 1);
        String startString = getArgsOrSubCommand(ctx, array, 2);
        String endString = getArgsOrSubCommand(ctx, array, 3);
        if (key == null || startString == null || endString == null) {
            ctx.writeAndFlush(new Errors(String.format(ErrorsConsts.COMMAND_WRONG_ARGS_NUMBER, type().toString())));
            return;
        }
        start = Integer.parseInt(startString);
        end = Integer.parseInt(endString);
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
            throw new UnsupportedOperationException();
        }

    }
}
