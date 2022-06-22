package org.isheihei.redis.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.log4j.Logger;
import org.isheihei.redis.common.consts.ErrorsConst;
import org.isheihei.redis.common.util.ConfigUtil;
import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.Command;
import org.isheihei.redis.core.command.CommandType;
import org.isheihei.redis.core.command.impl.server.BgSave;
import org.isheihei.redis.core.command.impl.server.Save;
import org.isheihei.redis.core.persist.rdb.Rdb;
import org.isheihei.redis.core.resp.impl.Errors;
import org.isheihei.redis.core.resp.impl.SimpleString;

/**
 * @ClassName: CommandHandler
 * @Description: 操作命令处理
 * @Date: 2022/6/7 22:13
 * @Author: isheihei
 */
public class CommandHandler extends SimpleChannelInboundHandler<Command> {

    private static final Logger LOGGER = Logger.getLogger(CommandHandler.class);

    private RedisClient client;

    private Rdb rdb;

    public CommandHandler(RedisClient client, Rdb rdb) {
        super();
        this.client = client;
        this.rdb = rdb;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Command command) throws Exception {
        try {
            // 如果开启了认证功能，所有命令执行前需要检查认证是否成功
            if (ConfigUtil.getRequirePass() != null && client.getAuth() == 0 && command.type() != CommandType.auth) {
                ctx.writeAndFlush(new Errors(ErrorsConst.NO_AUTH));
            } else {
                if (command instanceof Save) {
                    if (rdb == null) {
                        ctx.writeAndFlush(new SimpleString("rdb close"));
                        return;
                    }
                    rdb.save();
                    ctx.writeAndFlush(SimpleString.OK);
                    return;
                } else if (command instanceof BgSave) {
                    if (rdb == null) {
                        ctx.writeAndFlush(new SimpleString("rdb close"));
                        return;
                    }
                    rdb.bgSave();
                    ctx.writeAndFlush(new SimpleString("Background saving started"));
                    return;
                }
                command.handle(ctx, client);
            }
        } catch (Exception e) {
            LOGGER.error("执行命令出错", e);
            ctx.writeAndFlush(new Errors(ErrorsConst.INTERNEL_ERROR));
        }
    }
}
