package org.isheihei.redis.core.command.impl.string;

import org.isheihei.redis.common.consts.ErrorsConst;
import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.AbstractWriteCommand;
import org.isheihei.redis.core.command.CommandType;
import org.isheihei.redis.core.db.RedisDB;
import org.isheihei.redis.core.obj.RedisObject;
import org.isheihei.redis.core.obj.impl.RedisStringObject;
import org.isheihei.redis.core.resp.Resp;
import org.isheihei.redis.core.resp.impl.Errors;
import org.isheihei.redis.core.resp.impl.RespInt;
import org.isheihei.redis.core.struct.impl.BytesWrapper;
import org.isheihei.redis.core.struct.impl.RedisString;

/**
 * @ClassName: SetNx
 * @Description:  SET if not exists
 * @Date: 2022/6/9 23:25
 * @Author: isheihei
 */
public class SetNx extends AbstractWriteCommand {

    private BytesWrapper key;

    private BytesWrapper value;

    @Override
    public CommandType type() {
        return CommandType.setnx;
    }

    @Override
    public Resp handleWrite(RedisClient redisClient) {
        if ((key = getBytesWrapper(array, 1)) == null) {
            return new Errors(String.format(ErrorsConst.COMMAND_WRONG_ARGS_NUMBER, type().toString()));
        }
        if ((value = getBytesWrapper(array, 2)) == null) {
            return new Errors(String.format(ErrorsConst.COMMAND_WRONG_ARGS_NUMBER, type().toString()));
        }

        RedisDB db = redisClient.getDb();
        RedisObject redisObject = db.get(key);
        if (redisObject == null) {
            return new RespInt(0);
        }
        RedisStringObject redisStringObject = new RedisStringObject();
        ((RedisString) redisStringObject.data()).setValue(value);
        db.put(key, redisStringObject);
        db.touchWatchKey(key);
        db.plusDirty();
        return new RespInt(1);
    }
}
