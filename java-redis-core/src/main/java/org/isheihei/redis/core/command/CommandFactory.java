package org.isheihei.redis.core.command;

import org.apache.log4j.Logger;
import org.isheihei.redis.common.util.TRACEID;
import org.isheihei.redis.core.resp.BulkString;
import org.isheihei.redis.core.resp.Resp;
import org.isheihei.redis.core.resp.RespArray;
import org.isheihei.redis.core.resp.SimpleString;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @ClassName: CommandFactory
 * @Description: 根据RESP协议类型获取对应的操作命令工厂类
 * @Date: 2022/6/8 18:16
 * @Author: isheihei
 */
public class CommandFactory {

    private static final Logger LOGGER = Logger.getLogger(CommandFactory.class);

    // 命令集合
    static Map<String, Supplier<Command>> commandMap = new HashMap<>();

    static {
        for (CommandType type : CommandType.values()) {
            commandMap.put(type.name(), type.getSupplier());
        }
    }

    /**
     * @Description: RESP数组转换为command
     * @Param: respArray
     * @Return: Command
     * @Author: isheihei
     */
    public static Command from(RespArray respArray) {
        Resp[] array = respArray.getArray();
        // 获取命令名称
        String commandName = ((BulkString) array[0]).getContent().toUtf8String().toLowerCase();
        Supplier<Command> supplier = commandMap.get(commandName);
        if (supplier == null) {
            LOGGER.debug("traceId:" + TRACEID.currentTraceId() + " 不支持的命令：" + commandName);
            return null;
        } else {
            try {
                Command command = supplier.get();
                command.setContent(respArray);
                return command;
            } catch (Throwable e) {
                LOGGER.debug("traceId:"+TRACEID.currentTraceId()+" 不支持的命令：{},数据读取异常"+commandName);
                e.printStackTrace();
                return null;
            }
        }
    }

    /**
     * @Description: RESP单行字符串转换为command，主要为无参数command，例如：ping
     * @Param: string
     * @Return: Command
     * @Author: isheihei
     */
    public static Command from(SimpleString string) {
        String commandName = string.getContent().toLowerCase();
        Supplier<Command> supplier = commandMap.get(commandName);
        if (supplier == null) {
            LOGGER.debug("traceId:" + TRACEID.currentTraceId() + " 不支持的命令：" + commandName);
            return null;
        } else {
            try {
                return supplier.get();
            } catch (Throwable e) {
                LOGGER.debug("traceId:"+TRACEID.currentTraceId()+" 不支持的命令：{},数据读取异常"+commandName);
                e.printStackTrace();
                return null;
            }
        }
    }

}
