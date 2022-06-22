package org.isheihei.redis.core.command.impl.string;

import org.isheihei.redis.common.consts.ErrorsConst;
import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.AbstractWriteCommand;
import org.isheihei.redis.core.command.CommandType;
import org.isheihei.redis.core.db.RedisDB;
import org.isheihei.redis.core.obj.impl.RedisStringObject;
import org.isheihei.redis.core.resp.Resp;
import org.isheihei.redis.core.resp.impl.BulkString;
import org.isheihei.redis.core.resp.impl.Errors;
import org.isheihei.redis.core.resp.impl.SimpleString;
import org.isheihei.redis.core.struct.impl.BytesWrapper;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName: MSet
 * @Description: 设置多个 key 的值为各自对应的 value
 * @Date: 2022/6/9 23:24
 * @Author: isheihei
 */
public class MSet extends AbstractWriteCommand {

    private List<BytesWrapper> kvList;

    @Override
    public CommandType type() {
        return CommandType.mset;
    }

    @Override
    public Resp handleWrite(RedisClient redisClient) {
        // TODO 批量应该是原子操作
        kvList = Arrays.stream(array).skip(1).map(resp -> ((BulkString) resp).getContent()).collect(Collectors.toList());
        if (kvList.size() == 0) {
            return new Errors(String.format(ErrorsConst.COMMAND_WRONG_ARGS_NUMBER, type().toString()));
        }

        if (kvList.size() % 2 != 0) {
            return new Errors(String.format(ErrorsConst.WRONG_ARGS_NUMBER, type().toString().toUpperCase()));
        } else {
            RedisDB db = redisClient.getDb();
            Iterator<BytesWrapper> iterator = kvList.iterator();
            while (iterator.hasNext()) {
                BytesWrapper key = iterator.next();
                BytesWrapper value = iterator.next();
                db.touchWatchKey(key);
                db.plusDirty();
                db.put(key, new RedisStringObject(value));
            }
            return SimpleString.OK;
        }
    }
}
