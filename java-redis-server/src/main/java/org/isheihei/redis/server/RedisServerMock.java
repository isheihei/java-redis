package org.isheihei.redis.server;

import org.apache.log4j.Logger;
import org.isheihei.redis.core.db.RedisDB;
import org.isheihei.redis.core.persist.aof.Aof;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @ClassName: RedisServerMock
 * @Description: TODO
 * @Date: 2022/5/31 15:52
 * @Author: isheihei
 */
public class RedisServerMock implements RedisServer{

    private static final Logger LOGGER = Logger.getLogger(RedisNetServer.class);

    // 数据库列表
    private List<RedisDB> dbs = new ArrayList<>();

    // 数据库数量
    private int dbNum = 16;

    // 记录了保存条件的数组
    //private List<SaveParam> saveParams = new ArrayList<>();

    // 修改计数器
    private AtomicInteger dirty = new AtomicInteger();

    // 记录上一次执行持久化的事件
    private Timestamp timeStamp;

    // aof缓冲区
    private Aof aof;

    @Override
    public void start() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            String command = sc.nextLine();
        }
    }

    @Override
    public void close() {

    }
}
