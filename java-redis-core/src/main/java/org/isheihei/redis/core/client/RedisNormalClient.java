package org.isheihei.redis.core.client;

import org.isheihei.redis.core.db.RedisDB;

/**
 * @ClassName: RedisNormalClient
 * @Description: 客户端实现类
 * @Date: 2022/6/7 20:43
 * @Author: isheihei
 */
public class RedisNormalClient implements RedisClient{
    // 套接字
    private String addr;

    // 伪客户端为1；普通客户端的值为大于0的值
    private int fd;

    // 默认没有名字， 可以使用命令 client setname 设置
    private String name = null;

    // 当前客户端正在操作的数据库
    private RedisDB db;

    // 标志，可以i有多个，暂不实现
//    private String[] flags;

    // 是否通过了身份验证，0：未通过，1：通过
    private int authenticated = 0;

    // 创建客户端的时间
    private long ctime;

    // 客户端与服务器最后一次进行互动的时间
    private long lastInteraction;

    public RedisNormalClient(String addr, int fd, RedisDB db) {
        this.addr = addr;
        this.fd = fd;
        this.db = db;
        ctime = System.currentTimeMillis();
        lastInteraction = System.currentTimeMillis();
    }

    @Override
    public RedisDB getDb() {
        return db;
    }

    @Override
    public int getAuth() {
        return authenticated;
    }

    @Override
    public void setAuth(int authenticated) {
        this.authenticated = authenticated;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
