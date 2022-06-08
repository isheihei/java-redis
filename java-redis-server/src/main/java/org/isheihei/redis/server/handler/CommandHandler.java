package org.isheihei.redis.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.Command;

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
        command.handle(ctx, client);
    }
}
