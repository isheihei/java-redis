package org.isheihei.redis.server;

import org.isheihei.redis.server.channel.SingleChannelSelectStrategy;

public class ServerStart {
    public static void main(String[] args) {
        new RedisNetServer()
                .ip("127.0.0.1")
                .port(6379)
                .channelOption(new SingleChannelSelectStrategy())
                .dbNum(16)
                .aof(false)
                .init()
                .start();
    }
}
