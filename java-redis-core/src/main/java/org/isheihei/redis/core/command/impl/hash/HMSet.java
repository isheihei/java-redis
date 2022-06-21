package org.isheihei.redis.core.command.impl.hash;

import io.netty.channel.ChannelHandlerContext;
import org.isheihei.redis.common.consts.ErrorsConst;
import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.CommandType;
import org.isheihei.redis.core.command.AbstractWriteCommand;
import org.isheihei.redis.core.db.RedisDB;
import org.isheihei.redis.core.obj.RedisObject;
import org.isheihei.redis.core.obj.impl.RedisMapObject;
import org.isheihei.redis.core.resp.impl.BulkString;
import org.isheihei.redis.core.resp.impl.Errors;
import org.isheihei.redis.core.resp.impl.SimpleString;
import org.isheihei.redis.core.struct.RedisDataStruct;
import org.isheihei.redis.core.struct.impl.BytesWrapper;
import org.isheihei.redis.core.struct.impl.RedisMap;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName: HMSet
 * @Description: 将多个 field-value 对设置到哈希表中
 * @Date: 2022/6/11 15:15
 * @Author: isheihei
 */
public class HMSet extends AbstractWriteCommand {
    private BytesWrapper key;

    private List<BytesWrapper> fvLists;

    @Override
    public CommandType type() {
        return CommandType.hmset;
    }

    @Override
    public void handleWrite(ChannelHandlerContext ctx, RedisClient redisClient) {
        // TODO 原子性未实现
        if ((key = getBytesWrapper(ctx, array, 1)) == null) {
            return;
        }
        if ((array.length - 2) == 0) {
            ctx.writeAndFlush(new Errors(String.format(ErrorsConst.COMMAND_WRONG_ARGS_NUMBER, type().toString())));
            return;
        }
        if ((array.length - 2) % 2 != 0) {
            ctx.writeAndFlush(new Errors(String.format(ErrorsConst.WRONG_ARGS_NUMBER, type().toString().toUpperCase())));
            return;
        }

        fvLists = Arrays.stream(array).skip(2).map(resp -> ((BulkString) resp).getContent()).collect(Collectors.toList());
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
            ctx.writeAndFlush(new Errors(ErrorsConst.WRONG_TYPE_OPERATION));
        }
    }

    @Override
    public void handleLoadAof(RedisClient redisClient) {
        // TODO 原子性未实现
        if ((key = getBytesWrapper(array, 1)) == null) {
            return;
        }

        fvLists = Arrays.stream(array).skip(2).map(resp -> ((BulkString) resp).getContent()).collect(Collectors.toList());
        if (fvLists.size() == 0) {
            return;
        }
        if (fvLists.size() % 2 != 0) {
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
            } else {
                throw new UnsupportedOperationException();
            }
        }
    }
}
