package org.isheihei.redis.server.channel;

import io.netty.channel.epoll.Epoll;
import io.netty.channel.kqueue.KQueue;
import org.isheihei.redis.server.channel.epoll.EpollChannelOption;
import org.isheihei.redis.server.channel.kqueue.KqueueChannelOption;
import org.isheihei.redis.server.channel.select.NioSelectChannelOption;

/**
 * @ClassName: DefaultChannelSelectStrategy
 * @Description: 默认的 nio 实现
 * @Date: 2022/6/11 16:23
 * @Author: isheihei
 */
public class DefaultChannelSelectStrategy implements ChannelSelectStrategy{
    @Override
    public LocalChannelOption select() {
        if(KQueue.isAvailable()){
            return new KqueueChannelOption();
        }
        if(Epoll.isAvailable()){
            return new EpollChannelOption();
        }
        return new NioSelectChannelOption();
    }
}
