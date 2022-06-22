package org.isheihei.redis.core.command.impl.key;

import org.isheihei.redis.common.consts.ErrorsConst;
import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.AbstractWriteCommand;
import org.isheihei.redis.core.command.CommandType;
import org.isheihei.redis.core.db.RedisDB;
import org.isheihei.redis.core.resp.Resp;
import org.isheihei.redis.core.resp.impl.Errors;
import org.isheihei.redis.core.resp.impl.SimpleString;
import org.isheihei.redis.core.struct.impl.BytesWrapper;

/**
 * @ClassName: Rename
 * @Description: 修改 key 的名字为 newkey
 * @Date: 2022/6/11 15:46
 * @Author: isheihei
 */
public class Rename extends AbstractWriteCommand {

    private BytesWrapper oldKey;

    private BytesWrapper newKey;

    @Override
    public CommandType type() {
        return CommandType.rename;
    }

    @Override
    public Resp handleWrite(RedisClient redisClient) {
        if ((oldKey = getBytesWrapper(array, 1)) == null) {
            return new Errors(String.format(ErrorsConst.COMMAND_WRONG_ARGS_NUMBER, type().toString()));
        }
        if ((newKey = getBytesWrapper(array, 2)) == null) {
            return new Errors(String.format(ErrorsConst.COMMAND_WRONG_ARGS_NUMBER, type().toString()));
        }

        RedisDB db = redisClient.getDb();
        boolean res = db.reName(oldKey, newKey);
        if (res) {
            db.plusDirty();
            return SimpleString.OK;
        } else {
            return new Errors(ErrorsConst.NO_SUCH_KEY);
        }
    }
}
