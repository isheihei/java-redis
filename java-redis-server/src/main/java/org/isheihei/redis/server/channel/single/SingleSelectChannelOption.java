package org.isheihei.redis.server.channel.single;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.isheihei.redis.server.channel.LocalChannelOption;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @ClassName: SingleSelectChannelOption
 * @Description: 单路模型
 * @Date: 2022/6/8 20:56
 * @Author: isheihei
 */
public class SingleSelectChannelOption implements LocalChannelOption {
    private final NioEventLoopGroup single;

    public SingleSelectChannelOption(NioEventLoopGroup single) {
        this.single = single;
    }
    public SingleSelectChannelOption()
    {
        this.single = new NioEventLoopGroup(1, new ThreadFactory() {
            private AtomicInteger index = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "Server_boss_" + index.getAndIncrement());
            }
        });

    }
    @Override
    public EventLoopGroup boss() {
        return  this.single;
    }

    @Override
    public EventLoopGroup selectors() {
        return  this.single;
    }

    @Override
    public Class getChannelClass() {
        return NioServerSocketChannel.class;
    }
}
