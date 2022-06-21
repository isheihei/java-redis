package org.isheihei.redis.core.client;

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
}
