package org.isheihei.redis.core.client;

import org.isheihei.redis.core.db.RedisDB;

import java.util.List;
import java.util.UUID;

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
    private String name;

    // 数据库
    private List<RedisDB> dbs;
    
    // 当前使用的数据库索引
    private int dbIndex = 0;

    // 标志，可以i有多个，暂不实现
//    private String[] flags;

    // 是否通过了身份验证，0：未通过，1：通过
    private int authenticated = 0;

    public RedisNormalClient(String addr, int fd, List<RedisDB> dbs) {
        this.addr = addr;
        this.fd = fd;
        this.dbs = dbs;
        this.name = addr + ":" + UUID.randomUUID();
    }

    @Override
    public RedisDB getDb() {
        return dbs.get(dbIndex);
    }

    @Override
    public boolean setDb(int dbIndex) {
        if (dbIndex < 0 || dbIndex >= dbs.size()) {
            return false;
        } else {
            this.dbIndex = dbIndex;
            return true;
        }
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

    @Override
    public void flushAll() {
        for (RedisDB db : dbs) {
            db.flushDb();
        }
    }
}
