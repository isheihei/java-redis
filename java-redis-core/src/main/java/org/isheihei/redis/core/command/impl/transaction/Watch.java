package org.isheihei.redis.core.command.impl.transaction;

import org.isheihei.redis.common.consts.ErrorsConst;
import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.AbstractCommand;
import org.isheihei.redis.core.command.CommandType;
import org.isheihei.redis.core.resp.Resp;
import org.isheihei.redis.core.resp.impl.BulkString;
import org.isheihei.redis.core.resp.impl.Errors;
import org.isheihei.redis.core.resp.impl.SimpleString;
import org.isheihei.redis.core.struct.impl.BytesWrapper;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName: Watch
 * @Description: 标记要监视的key
 * @Date: 2022/6/22 15:50
 * @Author: isheihei
 */
public class Watch extends AbstractCommand {

    private List<BytesWrapper> keyList;


    @Override
    public CommandType type() {
        return CommandType.watch;
    }

    @Override
    public Resp handle(RedisClient redisClient) {
        keyList = Arrays.stream(array).skip(1).map(resp -> ((BulkString) resp).getContent()).collect(Collectors.toList());
        if (keyList.size() == 0) {
            return new Errors(String.format(ErrorsConst.COMMAND_WRONG_ARGS_NUMBER, type().toString()));
        }
        redisClient.getDb().watchKeys(keyList, redisClient);
        return SimpleString.OK;
    }
}
