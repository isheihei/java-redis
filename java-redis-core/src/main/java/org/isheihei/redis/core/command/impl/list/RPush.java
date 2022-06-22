package org.isheihei.redis.core.command.impl.list;

import org.isheihei.redis.common.consts.ErrorsConst;
import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.CommandType;
import org.isheihei.redis.core.resp.Resp;
import org.isheihei.redis.core.resp.impl.BulkString;
import org.isheihei.redis.core.resp.impl.Errors;
import org.isheihei.redis.core.struct.impl.BytesWrapper;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName: RPush
 * @Description: 列表头添加元素，不存在则创建
 * @Date: 2022/6/10 16:24
 * @Author: isheihei
 */
public class RPush extends Push{

    private BytesWrapper key;
    private List<BytesWrapper> values;

    @Override
    public CommandType type() {
        return CommandType.rpush;
    }

    @Override
    public Resp handleWrite(RedisClient redisClient) {
        if ((key = getBytesWrapper(array, 1)) == null) {
            return new Errors(String.format(ErrorsConst.COMMAND_WRONG_ARGS_NUMBER, type().toString()));
        }
        values = Arrays.stream(array).skip(2).map(resp -> ((BulkString) resp).getContent()).collect(Collectors.toList());
        return lrPush(redisClient, false, key, values);
    }
}
