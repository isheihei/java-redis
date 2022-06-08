package org.isheihei.redis.server.channel;

import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;

/**
 * @ClassName: LocalChannelOption
 * @Description: EventLoopGroup 和 ServerChannel 选项
 * @Date: 2022/6/8 20:49
 * @Author: isheihei
 */
public interface LocalChannelOption< C extends Channel> {

    /**
     * @Description: 返回处理连接线程
     * @Return: EventLoopGroup
     * @Author: isheihei
     */
    EventLoopGroup boss();


    /**
     * @Description: 返回处理事件线程
     * @Return: EventLoopGroup
     * @Author: isheihei
     */
    EventLoopGroup selectors();

    /**
     * @Description: 获取ServerChannel类型
     * @Return: Class
     * @Author: isheihei
     */
    Class<? extends C> getChannelClass();
}
