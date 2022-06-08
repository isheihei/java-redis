package org.isheihei.redis.server;

/**
 * @ClassName: RedisSerber
 * @Description: Redis服务器接口
 * @Date: 2022/5/31 15:53
 * @Author: isheihei
 */
public interface RedisServer {

    void start();

    void close();

}
