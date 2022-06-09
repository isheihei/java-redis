package org.isheihei.redis.core.command.impl;

import io.netty.channel.ChannelHandlerContext;
import org.isheihei.redis.common.consts.ErrorsConsts;
import org.isheihei.redis.common.util.ConfigUtil;
import org.isheihei.redis.common.util.TRACEID;
import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.Command;
import org.isheihei.redis.core.command.CommandType;
import org.isheihei.redis.core.resp.*;
import org.isheihei.redis.core.struct.impl.BytesWrapper;
import org.isheihei.redis.core.struct.impl.RedisDynamicString;

import java.util.ArrayList;

/**
 * @ClassName: Config
 * @Description: 服务配置相关操作
 * @Date: 2022/6/8 19:25
 * @Author: isheihei
 */
public class Config implements Command {

    private Resp[] array;

    private String subCommand;

    private String configParam;

    private String newConfigValue;

    @Override

    public CommandType type() {
        return CommandType.config;
    }

    @Override
    public void setContent(Resp[] array) {
        this.array = array;
    }

    @Override
    public void handle(ChannelHandlerContext ctx, RedisClient redisClient) {
        String traceId = TRACEID.currentTraceId();
        LOGGER.debug("traceId:{} 当前的子命令是：{}" + traceId + subCommand);
        if ((subCommand = getFirstArgsOrSubCommand(ctx, array, 1)) == null) {
            return;
        }
        switch (subCommand) {
            case "get":
                if ((configParam = getArguments(ctx, array, 2, subCommand)) == null) {
                    return;
                }
                String configValue = null;
                try {
                    configValue = ConfigUtil.getConfig(configParam);
                } catch (Exception e) {
                    LOGGER.error("获取配置属性错误");
                }
                if (configValue == null) {
                    ctx.writeAndFlush(new RespArray(new Resp[0]));
                } else {
                    ArrayList<BulkString> list = new ArrayList<>();
                    list.add(new BulkString(new BytesWrapper((configParam.getBytes(CHARSET)))));
                    list.add(new BulkString(new BytesWrapper(configValue.getBytes(CHARSET))));
                    RespArray arrays = new RespArray(list.toArray(new Resp[list.size()]));
                    ctx.writeAndFlush(arrays);
                }
                break;
            case "set":
                if ((configParam = getArguments(ctx, array, 2, subCommand)) == null) {
                    return;
                }
                if ((newConfigValue = getArguments(ctx, array, 3, subCommand)) == null) {
                    return;
                }
                boolean state = false;
                try {
                    state = ConfigUtil.setConfig(configParam, newConfigValue);
                } catch (Exception e) {
                    LOGGER.error("设置配置属性错误");
                }
                if (state) {
                    ctx.writeAndFlush(SimpleString.OK);
                } else {
                    ctx.writeAndFlush(new Errors(String.format(ErrorsConsts.UNSUPOORT_CONFIG, configParam)));
                }
                break;
            default:
                ctx.writeAndFlush(new Errors(String.format(ErrorsConsts.CONFIG_SUB_COMMAND_ERROR)));
        }
    }
}
