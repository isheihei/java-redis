package org.isheihei.redis.core.command.impl.key;

import org.isheihei.redis.common.consts.ErrorsConst;
import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.AbstractWriteCommand;
import org.isheihei.redis.core.command.CommandType;
import org.isheihei.redis.core.resp.Resp;
import org.isheihei.redis.core.resp.impl.Errors;
import org.isheihei.redis.core.resp.impl.RespInt;
import org.isheihei.redis.core.struct.impl.BytesWrapper;

/**
 * @ClassName: Persist
 * @Description: 删除给定 key 的过期时间，使得 key 永不过期
 * @Date: 2022/6/11 15:44
 * @Author: isheihei
 */
public class Persist extends AbstractWriteCommand {

    private BytesWrapper key;

    @Override
    public CommandType type() {
        return CommandType.persist;
    }

    @Override
    public Resp handleWrite(RedisClient redisClient) {
        if ((key = getBytesWrapper(array, 1)) == null) {
            return new Errors(String.format(ErrorsConst.COMMAND_WRONG_ARGS_NUMBER, type().toString()));
        }
        int persist = redisClient.getDb().persist(key);
        return new RespInt(persist);
    }
}
