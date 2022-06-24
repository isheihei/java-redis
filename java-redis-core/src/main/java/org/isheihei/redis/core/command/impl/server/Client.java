package org.isheihei.redis.core.command.impl.server;

import org.isheihei.redis.common.consts.ErrorsConst;
import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.AbstractCommand;
import org.isheihei.redis.core.command.CommandType;
import org.isheihei.redis.core.resp.Resp;
import org.isheihei.redis.core.resp.impl.BulkString;
import org.isheihei.redis.core.resp.impl.Errors;
import org.isheihei.redis.core.resp.impl.SimpleString;
import org.isheihei.redis.core.struct.impl.BytesWrapper;

/**
 * @ClassName: Client
 * @Description: 客户端操作
 * @Date: 2022/6/8 19:14
 * @Author: isheihei
 */
public class Client extends AbstractCommand {

    private String subCommand;

    private String clientName;

    @Override
    public CommandType type() {
        return CommandType.client;
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
            case "setname":
                if ((clientName = getStringSubCommandArgs(array, 2)) == null) {
                    return new Errors(String.format(ErrorsConst.SUBCOMMAND_WRONG_ARGS_NUMBER, type().toString().toUpperCase(), subCommand));
                }
                redisClient.setName(clientName);
                return SimpleString.OK;
            case "getname":
                String name = redisClient.getName();
                if (name == null) {
                    return BulkString.NullBulkString;
                } else {
                    return new SimpleString(name);
                }
            default:
                return new Errors(String.format(ErrorsConst.CLIENT_SUB_COMMAND_ERROR));
        }
    }
}
