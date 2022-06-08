package org.isheihei.redis.server.channel;

/**
 * @ClassName: ChannelSelectStrategy
 * @Description: 线程模型策略
 * @Date: 2022/6/8 20:54
 * @Author: isheihei
 */
public interface ChannelSelectStrategy {
    LocalChannelOption select();
}
