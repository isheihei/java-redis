package org.isheihei.redis.core.command.impl;

import io.netty.channel.ChannelHandlerContext;
import org.isheihei.redis.common.consts.ErrorsConsts;
import org.isheihei.redis.common.util.ConfigUtil;
import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.Command;
import org.isheihei.redis.core.command.CommandType;
import org.isheihei.redis.core.resp.BulkString;
import org.isheihei.redis.core.resp.Errors;
import org.isheihei.redis.core.resp.Resp;
import org.isheihei.redis.core.resp.SimpleString;

/**
 * @ClassName: Auth
 * @Description: TODO
 * @Date: 2022/6/8 19:03
 * @Author: isheihei
 */
public class Auth implements Command {
    private String password;

    @Override
    public CommandType type() {
        return CommandType.auth;
    }

    @Override
    public void setContent(Resp[] array) {
        BulkString bulkString = (BulkString) array[1];
        byte[] content = bulkString.getContent().getByteArray();
        if (content.length == 0) {
            password = "";
        } else {
            password = new String(content);
        }
    }

    @Override
    public void handle(ChannelHandlerContext ctx, RedisClient redisClient) {
        if (password.equals(ConfigUtil.getAuth())) {
            redisClient.setAuth(1);
            ctx.writeAndFlush(new SimpleString("OK"));
            LOGGER.error("Auth 日志");
        } else {
            ctx.writeAndFlush(new Errors(ErrorsConsts.INVALID_PASSWORD));
        }
    }
}
