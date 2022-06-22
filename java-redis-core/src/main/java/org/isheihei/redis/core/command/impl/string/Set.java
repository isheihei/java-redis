package org.isheihei.redis.core.command.impl.string;

import org.isheihei.redis.common.consts.ErrorsConst;
import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.AbstractWriteCommand;
import org.isheihei.redis.core.command.CommandType;
import org.isheihei.redis.core.db.RedisDB;
import org.isheihei.redis.core.obj.impl.RedisStringObject;
import org.isheihei.redis.core.resp.Resp;
import org.isheihei.redis.core.resp.impl.Errors;
import org.isheihei.redis.core.resp.impl.SimpleString;
import org.isheihei.redis.core.struct.impl.BytesWrapper;

/**
 * @ClassName: Set
 * @Description: 将键 key 设定为指定的“字符串”值
 * @Date: 2022/6/9 23:23
 * @Author: isheihei
 */
public class Set extends AbstractWriteCommand {

    private BytesWrapper key;

    private BytesWrapper value;

    @Override
    public CommandType type() {
        return CommandType.set;
    }

    @Override
    public Resp handleWrite(RedisClient redisClient) {
        if ((key = getBytesWrapper(array, 1)) == null) {
            return new Errors(String.format(ErrorsConst.COMMAND_WRONG_ARGS_NUMBER, type().toString()));
        }
        if ((value = getBytesWrapper(array, 2)) == null) {
            return new Errors(String.format(ErrorsConst.COMMAND_WRONG_ARGS_NUMBER, type().toString()));
        }
        RedisStringObject stringObject = new RedisStringObject(value);
        RedisDB db = redisClient.getDb();
        db.put(key, stringObject);
        db.touchWatchKey(key);
        db.plusDirty();
        return SimpleString.OK;
    }
}
