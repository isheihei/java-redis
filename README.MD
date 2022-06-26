

![Java-Redis](log.jpg)

# Java-Redis

[![GitHub](https://img.shields.io/github/license/isheihei/java-redis?color=%23b4bf5f)](https://github.com/isheihei/java-redis/blob/main/LICENSE)

目前基于Java语言实现的功能最完整的Redis-Server，适用于Java程序员学习并实践Redis缓存底层原理。

## **特点**

- 基于netty nio
- 基于 **RESP** 协议并支持redis-cli，jedis等客户端操作
- 拥有五种数据对象，支持**六十多个**常用操作命令
- 支持RDB、AOF持久化机制
- 支持惰性、定时两种过期策略；
- 支持基于jvm内存的8种驱逐策略并可自行扩展
- 支持Redis事务
- 本地吞吐量是 Redis 的 60% - 70%；延迟在 50 并发下做到 98% 以上低于 1ms （基于redis-benchmark测试结果）

## **文档**

[java-redis-doc](http://isheihei.cn/tags/java-redis/)

每篇文档介绍了一个模块的底层原理以及相应的实现

## 使用

使用启动类 `ServerStart` 启动即可, 支持 fluent 风格，可以自行配置

```java
        new RedisNetServer()
                .ip("0.0.0.0")	// 地址
                .port(6379)	// 端口号()
                .channelOption(new SingleChannelSelectStrategy())	// io模型
                .dbNum(16)	// 数据库数量
                .evictStrategy(Evict.NO_EVICT())	// 逐出策略
                .expireStrategy(Expire.DEFAULT_EXPIRE_STRATEGY())	// 过期策略
                .aof(false)	// 是否开启 AOF 持久化
                .rdb(false)	// 是否开启 RDB 持久化
                .init()
                .start();
```

由于本项目基于原版 Redis 协议格式开发，所以兼容各种客户端连接，如 [redis-cli](https://github.com/microsoftarchive/redis)、[jedis](https://github.com/redis/jedis)等。

参考资料：

- [《Redis设计与实现》](http://product.dangdang.com/23501734.html)
- [Redis](https://redis.io/)
- [基于java从零写一个redis？从开发者视角简单聊一聊 - 知乎 (zhihu.com)](https://zhuanlan.zhihu.com/p/434698347)
- [Redis中文网 - Redis开发与运维技术、Redis教程、使用手册](https://www.redis.com.cn/)
- [Redis中的LFU算法 - 再见紫罗兰 - 博客园 (cnblogs.com)](https://www.cnblogs.com/linxiyue/p/10955533.html)
