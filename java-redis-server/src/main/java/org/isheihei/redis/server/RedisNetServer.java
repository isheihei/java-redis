package org.isheihei.redis.server;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.EventExecutorGroup;
import org.apache.log4j.Logger;
import org.isheihei.redis.core.db.RedisDB;
import org.isheihei.redis.core.db.RedisDBImpl;
import org.isheihei.redis.core.persist.aof.Aof;
import org.isheihei.redis.server.channel.LocalChannelOption;
import org.isheihei.redis.server.channel.SingleChannelSelectStrategy;
import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.client.RedisNormalClient;
import org.isheihei.redis.server.handler.CommandHandler;
import org.isheihei.redis.server.handler.CommandDecoder;
import org.isheihei.redis.server.handler.ResponseEncoder;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @ClassName: RedisServer
 * @Description: Redis服务器类实现
 * @Date: 2022/5/31 14:36
 * @Author: isheihei
 */
public class RedisNetServer implements RedisServer{

    private static final Logger LOGGER = Logger.getLogger(RedisNetServer.class);

    // 客户端列表
    private final ConcurrentHashMap<Integer, RedisClient> clients  = new ConcurrentHashMap<>();

    private final AtomicInteger clientId = new AtomicInteger(0);

    // 数据库列表
    private List<RedisDB> dbs;

    // 数据库数量
    private int dbNum = 16;

    // 修改计数器
    private AtomicInteger dirty = new AtomicInteger();

    // 记录上一次执行持久化的事件
    private long timeStamp;

    // aof缓冲区
    private Aof aof;

    private final ServerBootstrap serverBootstrap = new ServerBootstrap();

    // 处理 redis 核心操作的线程，是单线程的
    private final EventExecutorGroup redisSingleEventExecutor;

    // 处理连接和io操作的线程
    private final LocalChannelOption channelOption;

    public RedisNetServer(){
        // 目前只使用单路select线程模型
        channelOption = new SingleChannelSelectStrategy().select();
        this.redisSingleEventExecutor = new NioEventLoopGroup(1);

        // 初始化db
        dbs = new ArrayList<>();
        for (int i = 0; i < dbNum; i++) {
            dbs.add(new RedisDBImpl());
        }
    }
    @Override
    public void start() {
        start0();
    }

    @Override
    public void close()
    {
        try {
            channelOption.boss().shutdownGracefully();
            channelOption.selectors().shutdownGracefully();
            redisSingleEventExecutor.shutdownGracefully();
        }catch (Exception ignored) {
            LOGGER.warn( "Exception!", ignored);
        }
    }

    private void start0() {
        serverBootstrap.group(channelOption.boss(), channelOption.selectors())
                .channel(channelOption.getChannelClass())
                .handler(new LoggingHandler(LogLevel.INFO))
                .option(ChannelOption.SO_BACKLOG, 1024)
                .option(ChannelOption.SO_REUSEADDR, true)
                //false
//                .option(ChannelOption.SO_KEEPALIVE, PropertiesUtil.getTcpKeepAlive())
//                .childOption(ChannelOption.TCP_NODELAY, true)
//                .childOption(ChannelOption.SO_SNDBUF, 65535)
//                .childOption(ChannelOption.SO_RCVBUF, 65535)
                .localAddress(new InetSocketAddress("127.0.0.1", 6379))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        // 初始化客户端
                        int id = clientId.incrementAndGet();
                        RedisClient client = new RedisNormalClient(socketChannel.localAddress().toString(), id, dbs.get(0));
                        clients.put(id, client);
                        LOGGER.error("Server 日志");
                        //  初始化 channel
                        ChannelPipeline channelPipeline = socketChannel.pipeline();
                        channelPipeline.addLast(
                                new ResponseEncoder(),
                                new CommandDecoder(aof)//,
//                                /*心跳,管理长连接*/
//                                new IdleStateHandler(0, 0, 20)
                        );
                        channelPipeline.addLast(redisSingleEventExecutor, new CommandHandler(client));
                    }
                });

        try {
            ChannelFuture sync = serverBootstrap.bind().sync();
            LOGGER.info(sync.channel().localAddress().toString());
        } catch (InterruptedException e) {
//
            LOGGER.warn( "Interrupted!", e);
            throw new RuntimeException(e);
        }
    }

}
