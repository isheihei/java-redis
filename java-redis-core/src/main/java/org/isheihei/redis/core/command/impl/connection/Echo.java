package org.isheihei.redis.core.command.impl.connection;

import org.isheihei.redis.common.consts.ErrorsConst;
import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.AbstractCommand;
import org.isheihei.redis.core.command.CommandType;
import org.isheihei.redis.core.resp.Resp;
import org.isheihei.redis.core.resp.impl.BulkString;
import org.isheihei.redis.core.resp.impl.Errors;
import org.isheihei.redis.core.struct.impl.BytesWrapper;

/**
 * @ClassName: Echo
 * @Description: 打印一个给定的信息 message ，测试时使用
 * @Date: 2022/6/11 16:00
 * @Author: isheihei
 */
public class Echo extends AbstractCommand {

    private BytesWrapper message;

    @Override
    public CommandType type() {
        return CommandType.echo;
    }


    @Override
    public Resp handle(RedisClient redisClient) {
        if ((message = getBytesWrapper(array, 1)) == null) {
            return new Errors(String.format(ErrorsConst.COMMAND_WRONG_ARGS_NUMBER, type().toString()));
        }
        return new BulkString(message);
    }
}
