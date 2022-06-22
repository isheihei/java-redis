package org.isheihei.redis.core.command.impl.list;

import org.isheihei.redis.common.consts.ErrorsConst;
import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.CommandType;
import org.isheihei.redis.core.resp.Resp;
import org.isheihei.redis.core.resp.impl.Errors;
import org.isheihei.redis.core.struct.impl.BytesWrapper;

/**
 * @ClassName: LPop
 * @Description: 于删除并返回存储在 key 中的列表的第一个元素
 * @Date: 2022/6/10 16:38
 * @Author: isheihei
 */
public class LPop extends Pop {

    private BytesWrapper key;

    @Override
    public CommandType type() {
        return CommandType.lpop;
    }

    @Override
    public Resp handleWrite(RedisClient redisClient) {
        if ((key = getBytesWrapper(array, 1)) == null) {
            return new Errors(String.format(ErrorsConst.COMMAND_WRONG_ARGS_NUMBER, type().toString()));
        }
        return lrPop(redisClient, key, true);
    }
}
