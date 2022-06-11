package org.isheihei.redis.core.command.impl.connection;

import io.netty.channel.ChannelHandlerContext;
import org.isheihei.redis.common.consts.ErrorsConsts;
import org.isheihei.redis.common.util.ConfigUtil;
import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.Command;
import org.isheihei.redis.core.command.CommandType;
import org.isheihei.redis.core.resp.Errors;
import org.isheihei.redis.core.resp.Resp;
import org.isheihei.redis.core.resp.SimpleString;
import org.isheihei.redis.core.struct.impl.BytesWrapper;

/**
 * @ClassName: Auth
 * @Description: 认证
 * @Date: 2022/6/8 19:03
 * @Author: isheihei
 */
public class Auth implements Command {
    private BytesWrapper password;

    private Resp[] array;

    @Override
    public CommandType type() {
        return CommandType.auth;
    }

    @Override
    public void setContent(Resp[] array) {
        this.array = array;
    }

    @Override
    public void handle(ChannelHandlerContext ctx, RedisClient redisClient) {
        if ((password = getBytesWrapper(ctx, array, 1)) == null) {
            return;
        }
        if (password.toUtf8String().equals(ConfigUtil.getRequirepass())) {
            redisClient.setAuth(1);
            ctx.writeAndFlush(SimpleString.OK);
        } else {
            ctx.writeAndFlush(new Errors(ErrorsConsts.INVALID_PASSWORD));
        }
    }
}