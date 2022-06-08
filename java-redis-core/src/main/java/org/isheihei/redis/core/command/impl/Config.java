package org.isheihei.redis.core.command.impl;

import io.netty.channel.ChannelHandlerContext;
import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.Command;
import org.isheihei.redis.core.command.CommandType;
import org.isheihei.redis.core.resp.BulkString;
import org.isheihei.redis.core.resp.Resp;

/**
 * @ClassName: Config
 * @Description: 服务配置相关操作
 * @Date: 2022/6/8 19:25
 * @Author: isheihei
 */
public class Config implements Command {

    private Resp[] array;

    private String subCommand;

    @Override
    public CommandType type() {
        return CommandType.config;
    }

    @Override
    public void setContent(Resp[] array) {
        this.array = array;
        subCommand = ((BulkString) array[1]).getContent().toUtf8String();
    }

    @Override
    public void handle(ChannelHandlerContext ctx, RedisClient redisClient) {

    }
}
