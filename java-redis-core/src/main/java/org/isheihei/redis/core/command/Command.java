package org.isheihei.redis.core.command;

import io.netty.channel.ChannelHandlerContext;
import org.isheihei.redis.common.consts.ErrorsConst;
import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.resp.impl.BulkString;
import org.isheihei.redis.core.resp.impl.Errors;
import org.isheihei.redis.core.resp.Resp;
import org.isheihei.redis.core.resp.impl.RespArray;
import org.isheihei.redis.core.struct.impl.BytesWrapper;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @ClassName: Command
 * @Description: 操作命令接口
 * @Date: 2022/6/8 18:06
 * @Author: isheihei
 */
public interface Command {
    Charset CHARSET = StandardCharsets.UTF_8;

    org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(Command.class);

    /**
     * @Description:  获取命令类型
     * @Return:  Command
     * @Author: isheihei
     */
    CommandType type();

    /**
     * @Description: 注入操作属性，变量等
     * @Param: arrays
     * @Return: void
     * @Author: isheihei
     */
    void setContent(RespArray arrays);

    /**
     * @Description: 执行操作
     * @Param: ctx 管道
     * @Param: redisClient 执行操作的客户端
     * @Return: void
     * @Author: isheihei
     */
    void handle(ChannelHandlerContext ctx, RedisClient redisClient);

    /**
     * @Description: 获取字符串包装对象
     * @Param: ctx
     * @Param: array
     * @Param: index
     * @Return: BytesWrapper
     * @Author: isheihei
     */
    default BytesWrapper getBytesWrapper(ChannelHandlerContext ctx, Resp[] array, int index){
        if (array.length < (index + 1)) {
            ctx.writeAndFlush(new Errors(String.format(ErrorsConst.COMMAND_WRONG_ARGS_NUMBER, type().toString())));
            return null;
        } else {
            return ((BulkString) array[index]).getContent();
        }
    };

    /**
     * @Description: 获取字符串包装对象
     * @Param: array
     * @Param: index
     * @Return: BytesWrapper
     * @Author: isheihei
     */
    default BytesWrapper getBytesWrapper(Resp[] array, int index){
        if (array.length < (index + 1)) {
            return null;
        } else {
            return ((BulkString) array[index]).getContent();
        }
    };

    /**
     * @Description: 获取包含 subCommand 的 String 类型参数
     * @Param: ctx
     * @Param: array
     * @Param: index
     * @Param: subCommand
     * @Return: String
     * @Author: isheihei
     */
    default String getStringSubCommandArgs(ChannelHandlerContext ctx, Resp[] array, int index, String subCommand) {
        if (array.length < (index + 1)) {
            ctx.writeAndFlush(new Errors(String.format(ErrorsConst.SUBCOMMAND_WRONG_ARGS_NUMBER, type().toString().toUpperCase(), subCommand)));
            return null;
        } else {
            return ((BulkString) array[index]).getContent().toUtf8String().toLowerCase();
        }
    }

}
