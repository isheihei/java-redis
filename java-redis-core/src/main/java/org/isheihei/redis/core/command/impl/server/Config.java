package org.isheihei.redis.core.command.impl.server;

import org.isheihei.redis.common.consts.ErrorsConst;
import org.isheihei.redis.common.util.ConfigUtil;
import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.AbstractCommand;
import org.isheihei.redis.core.command.CommandType;
import org.isheihei.redis.core.resp.impl.BulkString;
import org.isheihei.redis.core.resp.impl.Errors;
import org.isheihei.redis.core.resp.Resp;
import org.isheihei.redis.core.resp.impl.RespArray;
import org.isheihei.redis.core.resp.impl.SimpleString;
import org.isheihei.redis.core.struct.impl.BytesWrapper;

import java.util.ArrayList;

/**
 * @ClassName: Config
 * @Description: 服务配置相关操作
 * @Date: 2022/6/8 19:25
 * @Author: isheihei
 */
public class Config extends AbstractCommand {

    private String subCommand;

    private String configParam;

    private String newConfigValue;

    @Override

    public CommandType type() {
        return CommandType.config;
    }

    @Override
    public Resp handle(RedisClient redisClient) {
        LOGGER.debug("当前的子命令是：{}" + subCommand);
        BytesWrapper bytesSubCommand;
        if ((bytesSubCommand = getBytesWrapper(array, 1)) == null) {
            return new Errors(String.format(ErrorsConst.COMMAND_WRONG_ARGS_NUMBER, type().toString()));
        }
        subCommand = bytesSubCommand.toUtf8String().toLowerCase();
        switch (subCommand) {
            case "get":
                if ((configParam = getStringSubCommandArgs(array, 2)) == null) {
                    return new Errors(String.format(ErrorsConst.SUBCOMMAND_WRONG_ARGS_NUMBER, type().toString().toUpperCase(), subCommand));
                }
                String configValue = null;
                try {
                    configValue = ConfigUtil.getConfig(configParam);
                } catch (Exception e) {
                    LOGGER.error("获取配置属性错误");
                }
                if (configValue == null) {
                    return new RespArray(new Resp[0]);
                } else {
                    ArrayList<BulkString> list = new ArrayList<>();
                    list.add(new BulkString(new BytesWrapper((configParam.getBytes(CHARSET)))));
                    list.add(new BulkString(new BytesWrapper(configValue.getBytes(CHARSET))));
                    return new RespArray(list.toArray(new Resp[list.size()]));
                }
            case "set":
                if ((configParam = getStringSubCommandArgs(array, 2)) == null) {
                    return new Errors(String.format(ErrorsConst.SUBCOMMAND_WRONG_ARGS_NUMBER, type().toString().toUpperCase(), subCommand));
                }
                if ((newConfigValue = getStringSubCommandArgs(array, 3)) == null) {
                    return new Errors(String.format(ErrorsConst.SUBCOMMAND_WRONG_ARGS_NUMBER, type().toString().toUpperCase(), subCommand));
                }
                boolean state;
                state = ConfigUtil.setConfig(configParam, newConfigValue);
                if (state) {
                    return SimpleString.OK;
                } else {
                    return new Errors(String.format(ErrorsConst.UNSUPOORT_CONFIG, configParam));
                }
            default:
                return new Errors(String.format(ErrorsConst.CONFIG_SUB_COMMAND_ERROR));
        }
    }
}
