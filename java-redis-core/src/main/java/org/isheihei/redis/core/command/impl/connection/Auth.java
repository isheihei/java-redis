package org.isheihei.redis.core.command.impl.connection;

import org.isheihei.redis.common.consts.ErrorsConst;
import org.isheihei.redis.common.util.ConfigUtil;
import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.AbstractCommand;
import org.isheihei.redis.core.command.CommandType;
import org.isheihei.redis.core.resp.Resp;
import org.isheihei.redis.core.resp.impl.Errors;
import org.isheihei.redis.core.resp.impl.SimpleString;
import org.isheihei.redis.core.struct.impl.BytesWrapper;

/**
 * @ClassName: Auth
 * @Description: 认证
 * @Date: 2022/6/8 19:03
 * @Author: isheihei
 */
public class Auth extends AbstractCommand {
    private BytesWrapper password;

    @Override
    public CommandType type() {
        return CommandType.auth;
    }

    @Override
    public Resp handle(RedisClient redisClient) {
        if ((password = getBytesWrapper(array, 1)) == null) {
            return new Errors(String.format(ErrorsConst.COMMAND_WRONG_ARGS_NUMBER, type().toString()));
        }
        if (password.toUtf8String().equals(ConfigUtil.getRequirePass())) {
            redisClient.setAuth(1);
            return SimpleString.OK;
        } else {
            return new Errors(ErrorsConst.INVALID_PASSWORD);
        }
    }
}
