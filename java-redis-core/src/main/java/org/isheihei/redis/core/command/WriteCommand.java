package org.isheihei.redis.core.command;

import io.netty.channel.ChannelHandlerContext;
import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.resp.Resp;

/**
 * @ClassName: AbstractCommand
 * @Description: 模板方法模式 按需赋予 aof 等功能
 * @Date: 2022/6/13 20:48
 * @Author: isheihei
 */
public abstract class WriteCommand implements Command{
    public Resp[] array;

    @Override
    public void handle(ChannelHandlerContext ctx, RedisClient redisClient) {
        handleCommand(ctx, redisClient);
        if (needWrite()) {
            aof();
        }
    }

    public abstract void handleCommand(ChannelHandlerContext ctx, RedisClient redisClient);

    public abstract boolean needWrite();


    // TODO
    public void aof() {
        return;
    }
}
