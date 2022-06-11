package org.isheihei.redis.core.command.impl.hash;

import io.netty.channel.ChannelHandlerContext;
import org.isheihei.redis.common.consts.ErrorsConsts;
import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.Command;
import org.isheihei.redis.core.command.CommandType;
import org.isheihei.redis.core.db.RedisDB;
import org.isheihei.redis.core.obj.RedisObject;
import org.isheihei.redis.core.obj.impl.RedisMapObject;
import org.isheihei.redis.core.resp.BulkString;
import org.isheihei.redis.core.resp.Errors;
import org.isheihei.redis.core.resp.Resp;
import org.isheihei.redis.core.resp.SimpleString;
import org.isheihei.redis.core.struct.RedisDataStruct;
import org.isheihei.redis.core.struct.impl.BytesWrapper;
import org.isheihei.redis.core.struct.impl.RedisMap;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName: Hmset
 * @Description: 将多个 field-value 对设置到哈希表中
 * @Date: 2022/6/11 15:15
 * @Author: isheihei
 */
public class Hmset implements Command {
    private BytesWrapper key;

    private List<BytesWrapper> fvLists;

    private Resp[] array;

    @Override
    public CommandType type() {
        return CommandType.hmset;
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

        fvLists = Arrays.stream(array).skip(2).map(resp -> ((BulkString) resp).getContent()).collect(Collectors.toList());
        if (fvLists == null) {
            ctx.writeAndFlush(new Errors(String.format(ErrorsConsts.COMMAND_WRONG_ARGS_NUMBER, type().toString())));
            return;
        }
        if (fvLists.size() % 2 != 0) {
            ctx.writeAndFlush(new Errors(String.format(ErrorsConsts.WRONG_ARGS_NUMBER, type().toString().toUpperCase())));
            return;
        }

        RedisDB db = redisClient.getDb();
        RedisObject redisObject = db.get(key);
        if (redisObject == null) {
            redisObject = new RedisMapObject();
            db.put(key, redisObject);
        }
        if (redisObject instanceof RedisMapObject) {
            RedisDataStruct data = redisObject.data();
            if (data instanceof RedisMap) {
                RedisMap map = ((RedisMap) redisObject.data());
                map.mset(fvLists);
                ctx.writeAndFlush(SimpleString.OK);
            } else {
                throw new UnsupportedOperationException();
            }
        } else {
            ctx.writeAndFlush(new Errors(ErrorsConsts.WRONG_TYPE_OPERATION));
        }

    }
}
