package org.isheihei.redis.server;

import org.isheihei.redis.core.evict.Evict;
import org.isheihei.redis.core.expired.Expire;
import org.isheihei.redis.server.channel.DefaultChannelSelectStrategy;

public class ServerStart {
    public static void main(String[] args) {
        RedisNetServer server = new RedisNetServer()
                .ip("0.0.0.0")
                .port(6379)
                .channelOption(new DefaultChannelSelectStrategy())
                .dbNum(16)
                .evictStrategy(Evict.NO_EVICT())
                .expireStrategy(Expire.DEFAULT_EXPIRE_STRATEGY())
                .aof(false)
                .rdb(false);

        //  JVM 最大内存 可以通过 -Xmx 进行设置
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
                    server.aof(Boolean.parseBoolean(args[++i]));
                    break;
                case "-dbNum":
                    server.dbNum(Integer.parseInt(args[++i]));
                    break;
                case "-rdb":
                    server.rdb(Boolean.valueOf(args[++i]));
                    break;
                default:
                    i++;
                    continue;
            }
        }
        server.init().start();
    }
}
