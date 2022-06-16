package org.isheihei.redis.server;

import org.isheihei.redis.server.channel.SingleChannelSelectStrategy;

public class ServerStart {
    public static void main(String[] args) {
        RedisNetServer server = new RedisNetServer()
                .ip("0.0.0.0")
                .port(6379)
                .channelOption(new SingleChannelSelectStrategy())
                .dbNum(16)
                .aof(false)
                .rdb(true);

        //  JVM 最大内存 可以通过 -Xmx 进行设置
        long maxMemory = Runtime.getRuntime().maxMemory();
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-ip":
                    server.ip(args[++i]);
                    break;
                case "-port":
                    server.port(Integer.parseInt(args[++i]));
                    System.out.println(args[i]);
                    break;
                case "-aof":
                    server.aof(Boolean.getBoolean(args[++i]));
                    break;
                case "-dbNum":
                    server.dbNum(Integer.parseInt(args[++i]));
                    break;
                default:
                    i++;
                    continue;
            }
        }
        server.init().start();
    }
}
