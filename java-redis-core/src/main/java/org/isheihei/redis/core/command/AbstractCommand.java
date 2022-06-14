package org.isheihei.redis.core.command;

import io.netty.channel.ChannelHandlerContext;
import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.resp.Resp;
import org.isheihei.redis.core.resp.RespArray;

/**
 * @ClassName: AbstractCommand
 * @Description: TODO
 * @Date: 2022/6/14 19:57
 * @Author: isheihei
 */
public abstract class AbstractCommand implements Command {

    public RespArray arrays;

    public Resp[] array;

    @Override
    public void setContent(RespArray arrays) {
        this.arrays = arrays;
        this.array = arrays.getArray();
    }

    @Override
    public abstract CommandType type();

    @Override
    public abstract void handle(ChannelHandlerContext ctx, RedisClient redisClient);

}
