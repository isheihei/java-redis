package org.isheihei.redis.core.command.impl.string;

import io.netty.channel.ChannelHandlerContext;
import org.isheihei.redis.common.consts.ErrorsConsts;
import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.Command;
import org.isheihei.redis.core.command.CommandType;
import org.isheihei.redis.core.db.RedisDB;
import org.isheihei.redis.core.obj.impl.RedisStringObject;
import org.isheihei.redis.core.resp.BulkString;
import org.isheihei.redis.core.resp.Errors;
import org.isheihei.redis.core.resp.Resp;
import org.isheihei.redis.core.resp.SimpleString;
import org.isheihei.redis.core.struct.impl.BytesWrapper;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName: Mset
 * @Description: 设置多个 key 的值为各自对应的 value
 * @Date: 2022/6/9 23:24
 * @Author: isheihei
 */
public class Mset implements Command {

    private List<BytesWrapper> kvList;

    @Override
    public CommandType type() {
        return CommandType.mset;
    }

    @Override
    public void setContent(Resp[] array) {
        kvList = Arrays.stream(array).skip(1).map(resp -> ((BulkString) resp).getContent()).collect(Collectors.toList());
    }

    @Override
    public void handle(ChannelHandlerContext ctx, RedisClient redisClient) {
        // TODO 批量应该是原子操作
        if (kvList.size() == 0 || kvList == null) {
            ctx.writeAndFlush(new Errors(String.format(ErrorsConsts.COMMAND_WRONG_ARGS_NUMBER, type().toString())));
            return;
        }

        if (kvList.size() % 2 != 0) {
            ctx.writeAndFlush(new Errors(String.format(ErrorsConsts.WRONG_ARGS_NUMBER, type().toString().toUpperCase())));
            return;
        } else {
            RedisDB db = redisClient.getDb();
            Iterator<BytesWrapper> iterator = kvList.iterator();
            while (iterator.hasNext()) {
                db.put(iterator.next(), new RedisStringObject(iterator.next()));
            }
            ctx.writeAndFlush(SimpleString.OK);
        }
    }
}
