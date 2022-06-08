package org.isheihei.redis.core.command;

import io.netty.channel.ChannelHandlerContext;
import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.resp.Resp;

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



}
