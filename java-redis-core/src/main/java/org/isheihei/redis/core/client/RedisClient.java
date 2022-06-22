package org.isheihei.redis.core.client;

import org.isheihei.redis.core.command.Command;
import org.isheihei.redis.core.db.RedisDB;

/**
 * @ClassName: RedisClient
 * @Description: 客户端接口
 * @Date: 2022/6/7 20:43
 * @Author: isheihei
 */
public interface RedisClient {

    /**
     * @Description: 获取当前客户端使用的数据库
     * @Return:  RedisDB
     * @Author: isheihei
     */
    RedisDB getDb();

    /**
     * @Description: 设置当前客户端使用的数据库
     * @Param: dbIndex
     * @Author: isheihei
     */
    boolean setDb(int dbIndex);

    /**
     * @Description: 设置事务标志
     * @Param: flag
     * @Author: isheihei
     */
    void setFlag(boolean flag);

    /**
     * @Description: 获取事务标志
     * @Param: flag
     * @Return: boolean
     * @Author: isheihei
     */
    boolean getFlag();

    /**
     * @Description: 设置事务安全性标志
     * @Param: dirtyCas
     * @Author: isheihei
     */
    void setDirtyCas(boolean dirtyCas);

    /**
     * @Description: 获取事务安全性标志
     * @Return: boolean
     * @Author: isheihei
     */
    boolean getDirtyCas();

    /**
     * @Description: 向事务队列添加一条命令
     * @Param: command
     * @Author: isheihei
     */
    void addCommand(Command command);

    /**
     * @Description: 清空事务队列
     * @Author: isheihei
     */
    void flushCommand();

    /**
     * @Description: 事务队列中取出一个命令
     * @Return:
     * @Author: isheihei
     */
    Command getCommand();
    /**
     * @Description: 获取标志认证标志
     * @Return: int
     * @Author: isheihei
     */
    int getAuth();
    /**
     * @Description: 设置认证标志
     * @Param: authenticated
     * @Return: void
     * @Author: isheihei
     */
    void setAuth(int authenticated);

    /**
     * @Description: 设置客户端名称
     * @Param: name
     * @Return: void
     * @Author: isheihei
     */
    void setName(String name);

    /**
     * @Description: 获取客户端名称
     * @Return: String
     * @Author: isheihei
     */
    String getName();

    /**
     * @Description: 清空所有数据库
     * @Author: isheihei
     */
    void flushAll();

    void unWatchKeys(RedisClient redisClient);
}
