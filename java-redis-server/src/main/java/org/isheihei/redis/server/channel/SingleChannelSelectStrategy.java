package org.isheihei.redis.server.channel;

import org.isheihei.redis.server.channel.single.SingleSelectChannelOption;

/**
 * @ClassName: SingleChannelSelectStrategy
 * @Description: 单路模型策略
 * @Date: 2022/6/7 20:22
 * @Author: isheihei
 */
public class SingleChannelSelectStrategy implements ChannelSelectStrategy{
    @Override
    public LocalChannelOption select() {
        return new SingleSelectChannelOption();
    }
}
