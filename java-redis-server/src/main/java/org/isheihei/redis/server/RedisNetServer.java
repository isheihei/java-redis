package org.isheihei.redis.server;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.EventExecutorGroup;
import org.apache.log4j.Logger;
import org.isheihei.redis.common.util.ConfigUtil;
import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.client.RedisNormalClient;
import org.isheihei.redis.core.db.RedisDB;
import org.isheihei.redis.core.db.RedisDBImpl;
import org.isheihei.redis.core.evict.Evict;
import org.isheihei.redis.core.evict.EvictStrategy;
import org.isheihei.redis.core.expired.Expire;
import org.isheihei.redis.core.expired.ExpireStrategy;
import org.isheihei.redis.core.persist.aof.Aof;
import org.isheihei.redis.core.persist.rdb.Rdb;
import org.isheihei.redis.server.channel.ChannelSelectStrategy;
import org.isheihei.redis.server.channel.LocalChannelOption;
import org.isheihei.redis.server.channel.SingleChannelSelectStrategy;
import org.isheihei.redis.server.handler.CommandDecoder;
import org.isheihei.redis.server.handler.CommandHandler;
import org.isheihei.redis.server.handler.ResponseEncoder;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @ClassName: RedisServer
 * @Description: Redis服务器类实现
 * @Date: 2022/5/31 14:36
 * @Author: isheihei
 */
public class RedisNetServer implements RedisServer {

    private static final Logger LOGGER = Logger.getLogger(RedisNetServer.class);

    private final AtomicInteger clientId = new AtomicInteger(0);

    private String ip = ConfigUtil.getIp();

    private int port = ConfigUtil.getPort();

    // 数据库列表
    private List<RedisDB> dbs;

    // 数据库数量
    private int dbNum = ConfigUtil.getDbNum();

    // aof缓冲区
    private Aof aof;

    private boolean appendOnlyFile = ConfigUtil.getAppendOnly();

    private Rdb rdb;

    private boolean dataBase = ConfigUtil.getRdb();

    private EvictStrategy evictStrategy;

    private ExpireStrategy expireStrategy;

    private final ServerBootstrap serverBootstrap = new ServerBootstrap();

    // 处理 redis 核心操作的线程，是单线程的
    private final EventExecutorGroup redisSingleEventExecutor = new NioEventLoopGroup(1);

    // 处理连接和io操作的线程
    private LocalChannelOption channelOption;

    public RedisNetServer() {
        this.channelOption = new SingleChannelSelectStrategy().select();
        this.evictStrategy = Evict.NO_EVICT();
        this.expireStrategy = Expire.DEFAULT_EXPIRE_STRATEGY();
    }

    public RedisNetServer ip(String ip) {
        boolean success = ConfigUtil.setConfig("ip", ip);
        if (success) {
            this.ip = ip;
        }
        return this;
    }
    public RedisNetServer port(int port) {
        boolean success = ConfigUtil.setConfig("port", String.valueOf(port));
        if (success) {
            this.port = port;
        }
        return this;
    }

    public RedisNetServer channelOption(ChannelSelectStrategy channelSelectStrategy) {
        this.channelOption = channelSelectStrategy.select();
        return this;
    }

    public RedisNetServer dbNum(int dbNum) {
        boolean success = ConfigUtil.setConfig("dbnum", String.valueOf(dbNum));
        if (success) {
            this.dbNum = dbNum;
        }
        return this;
    }

    public RedisNetServer evictStrategy(EvictStrategy evictStrategy) {
        this.evictStrategy = evictStrategy;
        return this;
    }

    public RedisNetServer expireStrategy(ExpireStrategy expireStrategy) {
        this.expireStrategy = expireStrategy;
        return this;
    }

    public RedisNetServer aof(boolean on) {
        boolean success = ConfigUtil.setConfig("appendonly", String.valueOf(on));
        if (success) {
            this.appendOnlyFile = on;
        }
        return this;
    }

    public RedisNetServer rdb(boolean on) {
        boolean success = ConfigUtil.setConfig("rdb", String.valueOf(on));
        if (success) {
            this.dataBase = on;
        }
        return this;
    }

    public RedisNetServer init() {
        // 初始化db
        dbs = new ArrayList<>();
        for (int i = 0; i < dbNum; i++) {
            dbs.add(new RedisDBImpl());
        }

        // 初始化aof
        if (appendOnlyFile) {
            this.aof = new Aof(dbs);
        }

        // 初始化rdb
        if (dataBase) {
            this.rdb = new Rdb(dbs);
        }
        return this;
    }

    @Override
    public void start() {
        start0();
    }

    @Override
    public void close() {
        try {
            channelOption.boss().shutdownGracefully();
            channelOption.selectors().shutdownGracefully();
            redisSingleEventExecutor.shutdownGracefully();
            System.exit(0);
        } catch (Exception e) {
            LOGGER.warn("Exception!", e);
        }
    }

    private void start0() {
        serverBootstrap.group(channelOption.boss(), channelOption.selectors())
                .channel(channelOption.getChannelClass())
                .handler(new LoggingHandler(LogLevel.INFO))
                .option(ChannelOption.SO_BACKLOG, 1024)
                .option(ChannelOption.SO_REUSEADDR, true)
                .localAddress(new InetSocketAddress(ip, port))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) {
                        // 初始化客户端
                        int id = clientId.incrementAndGet();
                        RedisClient client = new RedisNormalClient(socketChannel.localAddress().toString(), id, dbs);

                        //  初始化 channel
                        ChannelPipeline channelPipeline = socketChannel.pipeline();
                        channelPipeline.addLast(
                                new ResponseEncoder(),
                                new CommandDecoder(aof)
                        );
                        channelPipeline.addLast(redisSingleEventExecutor, new CommandHandler(client, rdb));
                    }
                });

        //  执行定时操作 serverCron
        ServerCron serverCron = new ServerCron(dbs);
        serverCron.expireStrategy(expireStrategy);
        serverCron.evictStrategy(evictStrategy);
        //  rdb 和 aof 同时开启优先使用aof文件加载
        if (dataBase && appendOnlyFile) {
            redisSingleEventExecutor.submit(() -> aof.load());
            serverCron.aof(aof);
            serverCron.rdb(rdb);
        } else if (dataBase) {
            redisSingleEventExecutor.submit(() -> rdb.load());
            serverCron.rdb(rdb);
        } else if (appendOnlyFile){
            redisSingleEventExecutor.submit(() -> aof.load());
            serverCron.aof(aof);
        }
        redisSingleEventExecutor.scheduleWithFixedDelay(serverCron, 100, 100, TimeUnit.MILLISECONDS);
        try {
            ChannelFuture sync = serverBootstrap.bind().sync();
            LOGGER.info(sync.channel().localAddress().toString());
        } catch (InterruptedException e) {
            LOGGER.warn("Interrupted!", e);
            throw new RuntimeException(e);
        }
    }

}
