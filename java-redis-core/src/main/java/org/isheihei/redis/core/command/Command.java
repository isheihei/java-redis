package org.isheihei.redis.core.command;

import io.netty.channel.ChannelHandlerContext;
import org.isheihei.redis.common.consts.ErrorsConsts;
import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.resp.BulkString;
import org.isheihei.redis.core.resp.Errors;
import org.isheihei.redis.core.resp.Resp;
import org.isheihei.redis.core.struct.impl.BytesWrapper;
import org.isheihei.redis.core.struct.impl.RedisDynamicString;

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
     * @Param: array
     * @Return: void
     * @Author: isheihei
     */
    void setContent(Resp[] array);

    /**
     * @Description: 执行操作
     * @Param: ctx 管道
     * @Param: redisClient 执行操作的客户端
     * @Return: void
     * @Author: isheihei
     */
    void handle(ChannelHandlerContext ctx, RedisClient redisClient);


    /**
     * @Description: 获取键字符串包装对象
     * @Param: ctx
     * @Param: array
     * @Param: index
     * @Return: BytesWrapper
     * @Author: isheihei
     */
    default BytesWrapper getDs(Resp[] array, int index){
        if (array.length < (index + 1)) {
            return null;
        } else {
            return ((BulkString) array[index]).getContent();
        }
    };
    /**
     * @Description: 获取子命令或第一个参数
     * @Param: ctx
     * @Param: array
     * @Param: index 数组索引（取第几个参数）
     * @Return: String
     * @Author: isheihei
     */
    default String getFirstArgsOrSubCommand(ChannelHandlerContext ctx, Resp[] array, int index){
        if (array.length < (index + 1)) {
            ctx.writeAndFlush(new Errors(String.format(ErrorsConsts.COMMAND_WRONG_ARGS_NUMBER, type().toString())));
            return null;
        } else {
            return ((BulkString) array[index]).getContent().toUtf8String().toLowerCase();
        }
    };

    /**
     * @Description: 获取参数
     * @Param: ctx
     * @Param: array
     * @Param: index
     * @Param: subCommand
     * @Return: String
     * @Author: isheihei
     */
    default String getArguments(ChannelHandlerContext ctx, Resp[] array, int index, String subCommand) {
        if (array.length < (index + 1)) {
            ctx.writeAndFlush(new Errors(String.format(ErrorsConsts.WRONG_ARGS_NUMBER, type().toString().toUpperCase(), subCommand)));
            return null;
        } else {
            return ((BulkString) array[index]).getContent().toUtf8String().toLowerCase();
        }
    }




}
