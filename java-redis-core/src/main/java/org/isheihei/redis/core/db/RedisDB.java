package org.isheihei.redis.core.db;

import io.netty.channel.Channel;
import org.isheihei.redis.core.obj.RedisObject;
import org.isheihei.redis.core.struct.BytesWrapper;

import java.util.List;
import java.util.Set;

/**
 * @ClassName: RedisDB
 * @Description: 数据库接口
 * @Date: 2022/6/8 15:21
 * @Author: isheihei
 */
public interface RedisDB {
    /**
     * @Description: 获取所有键
     * @Return: Set<BytesWrapper>
     * @Author: isheihei
     */
    Set<BytesWrapper> keys();

    /**
     * @Description: 判断某个键是否存在
     * @Param: key
     * @Return: boolean
     * @Author: isheihei
     */
    boolean exist(BytesWrapper key);

    /**
     * @Description: put一个键值对
     * @Param: key
     * @Param: redisObject
     * @Return: void
     * @Author: isheihei
     */
    void put(BytesWrapper key, RedisObject redisObject);

    /**
     * @Description: 获取一个键对应的值
     * @Param: key
     * @Return: RedisObject
     * @Author: isheihei
     */
    RedisObject get(BytesWrapper key);

    /**
     * @Description: 删除一批键值对
     * @Param: keys
     * @Return: long
     * @Author: isheihei
     */
    long remove(List<BytesWrapper> keys);

    /**
     * @Description: 清空数据库
     * @Return: void
     * @Author: isheihei
     */
    void cleanAll();
}
