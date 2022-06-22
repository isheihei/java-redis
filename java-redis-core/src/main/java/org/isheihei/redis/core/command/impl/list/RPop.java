package org.isheihei.redis.core.command.impl.list;

import org.isheihei.redis.common.consts.ErrorsConst;
import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.CommandType;
import org.isheihei.redis.core.resp.Resp;
import org.isheihei.redis.core.resp.impl.Errors;
import org.isheihei.redis.core.struct.impl.BytesWrapper;

/**
 * @ClassName: RPop
 * @Description: 在 key 中的列表的尾部插入所有指定的值
 * @Date: 2022/6/10 16:38
 * @Author: isheihei
 */
public class RPop extends Pop{
    private BytesWrapper key;

    @Override
    public CommandType type() {
        return CommandType.rpop;
    }

    @Override
    public Resp handleWrite(RedisClient redisClient) {
        if ((key = getBytesWrapper(array, 1)) == null){
            return new Errors(String.format(ErrorsConst.COMMAND_WRONG_ARGS_NUMBER, type().toString()));
        }
        return lrPop(redisClient, key, false);
    }
}
