# Java-Redis

基于Java实现Redis-Server

核心内容

- 基于netty nio
- 还原 **RESP** 协议支持redis-cli，jedis等客户端操作
- 拥有五种数据对象，支持六十多个常用操作命令
- 支持RDB、AOF持久化机制
- 支持惰性、定时两种过期策略；8种驱逐策略并可自行扩展
- 支持Redis事务

Redis 设计原理文档以及本项目的对应实现分析在 doc 目录

参考资料：

《redis设计与实现第二版》- 机械工业出版社

[Redis](https://redis.io/)

[基于java从零写一个redis？从开发者视角简单聊一聊 - 知乎 (zhihu.com)](https://zhuanlan.zhihu.com/p/434698347)

[Redis中文网 - Redis开发与运维技术、Redis教程、使用手册](https://www.redis.com.cn/)

[Redis中的LFU算法 - 再见紫罗兰 - 博客园 (cnblogs.com)](https://www.cnblogs.com/linxiyue/p/10955533.html)

