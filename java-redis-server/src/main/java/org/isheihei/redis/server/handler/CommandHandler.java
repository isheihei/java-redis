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
import org.isheihei.redis.core.command.impl.transaction.Discard;
import org.isheihei.redis.core.command.impl.transaction.Exec;
import org.isheihei.redis.core.command.impl.transaction.Multi;
import org.isheihei.redis.core.command.impl.transaction.UnWatch;
import org.isheihei.redis.core.command.impl.transaction.Watch;
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
    protected void channelRead0(ChannelHandlerContext ctx, Command command) {
        try {
            // 如果开启了认证功能，所有命令执行前需要检查认证是否成功
            if (ConfigUtil.getRequirePass() != null && client.getAuth() == 0 && command.type() != CommandType.auth) {
                ctx.writeAndFlush(new Errors(ErrorsConst.NO_AUTH));
                return;
            }
            if (client.getFlag()) {
                if (command instanceof Exec || command instanceof Watch || command instanceof UnWatch || command instanceof Discard) {
                    ctx.writeAndFlush(command.handle(client));
                } else if (command instanceof Multi) {
                    ctx.writeAndFlush(new Errors(ErrorsConst.MULTI_CAN_NOT_NESTED));
                } else {
                    client.addCommand(command);
                    ctx.writeAndFlush(new SimpleString("QUEUED"));
                }
            } else {
                if (command instanceof Exec) {
                    ctx.writeAndFlush(new Errors(ErrorsConst.EXEC_WITHOUT_MULTI));
                } else if (command instanceof Discard) {
                    ctx.writeAndFlush(new Errors(ErrorsConst.DISCARD_WITHOUT_MULTI));
                } else if (command instanceof Save) {
                    if (rdb != null) {
                        rdb.save();
                    }
                    ctx.writeAndFlush(command.handle(client));
                } else if (command instanceof BgSave) {
                    if (rdb != null) {
                        rdb.bgSave();
                    }
                    ctx.writeAndFlush(command.handle(client));
                } else {
                    ctx.writeAndFlush(command.handle(client));
                }
            }
        } catch (Exception e) {
            LOGGER.error("执行命令出错", e);
            ctx.writeAndFlush(new Errors(ErrorsConst.INTERNEL_ERROR));
        }
    }
}
