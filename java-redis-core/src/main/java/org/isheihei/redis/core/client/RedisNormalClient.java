package org.isheihei.redis.core.client;

import org.isheihei.redis.core.command.Command;
import org.isheihei.redis.core.db.RedisDB;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
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

    // 事务标志 true表示开启了一个事务
    private boolean flag = false;

    private final Queue<Command> multiCmd = new LinkedList<>();

    // 事务安全性标志 false表示安全
    private boolean dirtyCas = false;

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
    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    @Override
    public boolean getFlag() {
        return this.flag;
    }

    @Override
    public void setDirtyCas(boolean dirtyCas) {
        this.dirtyCas = dirtyCas;
    }

    @Override
    public boolean getDirtyCas() {
        return dirtyCas;
    }

    @Override
    public void addCommand(Command command) {
        multiCmd.add(command);
    }

    @Override
    public void flushCommand() {
        multiCmd.clear();
    }

    @Override
    public Command getCommand() {
        return multiCmd.poll();
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
            db.plusDirty(db.size());
            db.flushDb();
        }
    }

    @Override
    public void unWatchKeys(RedisClient redisClient) {
        for (RedisDB db : dbs) {
            db.unWatchKeys(redisClient);
        }
    }
}
