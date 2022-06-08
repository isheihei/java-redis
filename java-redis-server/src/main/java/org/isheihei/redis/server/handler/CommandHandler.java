package org.isheihei.redis.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.isheihei.redis.common.consts.ErrorsConsts;
import org.isheihei.redis.common.util.ConfigUtil;
import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.Command;
import org.isheihei.redis.core.command.CommandType;
import org.isheihei.redis.core.resp.Errors;

/**
 * @ClassName: CommandHandler
 * @Description: 操作命令处理
 * @Date: 2022/6/7 22:13
 * @Author: isheihei
 */
public class CommandHandler extends SimpleChannelInboundHandler<Command> {

    private RedisClient client;

    public CommandHandler(RedisClient client) {
        super();
        this.client = client;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Command command) throws Exception {
        // 如果开启了认证功能，所有命令执行前需要检查认证是否成功
        if (ConfigUtil.getAuth() != null && client.getAuth() == 0 && command.type() != CommandType.auth) {
            ctx.writeAndFlush(new Errors(ErrorsConsts.NO_AUTH));
        } else {
            command.handle(ctx, client);
        }
    }
}