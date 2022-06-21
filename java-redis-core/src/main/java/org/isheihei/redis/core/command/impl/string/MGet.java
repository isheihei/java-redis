package org.isheihei.redis.core.command.impl.string;

import io.netty.channel.ChannelHandlerContext;
import org.isheihei.redis.common.consts.ErrorsConst;
import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.AbstractCommand;
import org.isheihei.redis.core.command.CommandType;
import org.isheihei.redis.core.db.RedisDB;
import org.isheihei.redis.core.obj.impl.RedisStringObject;
import org.isheihei.redis.core.resp.impl.BulkString;
import org.isheihei.redis.core.resp.Resp;
import org.isheihei.redis.core.resp.impl.Errors;
import org.isheihei.redis.core.resp.impl.RespArray;
import org.isheihei.redis.core.struct.RedisDataStruct;
import org.isheihei.redis.core.struct.impl.BytesWrapper;
import org.isheihei.redis.core.struct.impl.RedisString;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName: MGet
 * @Description: 命令返回所有(一个或多个)给定 key 的值
 * @Date: 2022/6/9 23:24
 * @Author: isheihei
 */
public class MGet extends AbstractCommand {

    private List<BytesWrapper> keys;

    @Override
    public CommandType type() {
        return CommandType.mget;
    }

    @Override
    public void handle(ChannelHandlerContext ctx, RedisClient redisClient) {
        keys = Arrays.stream(array).skip(1).map(resp -> ((BulkString) resp).getContent()).collect(Collectors.toList());
        if (keys.size() == 0 || keys == null) {
            ctx.writeAndFlush(new Errors(String.format(ErrorsConst.COMMAND_WRONG_ARGS_NUMBER, type().toString())));
            return;
        }
        RedisDB db = redisClient.getDb();
        Resp[] array = keys.stream()
                .map(key -> db.get(key))
                .map(redisObject -> {
                    if (redisObject == null) {
                        return BulkString.NullBulkString;
                    } else {
                        BytesWrapper value = null;
                        if (redisObject instanceof RedisStringObject) {
                            RedisDataStruct data = redisObject.data();
                            if (data instanceof RedisString) {
                                value = ((RedisString) data).getValue();
                            }
                        }
                        return new BulkString(value);
                    }
                })
                .toArray(Resp[]::new);
        ctx.writeAndFlush(new RespArray(array));

    }
}
