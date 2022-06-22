package org.isheihei.redis.core.command.impl.set;

import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.CommandType;
import org.isheihei.redis.core.resp.Resp;

/**
 * @ClassName: SDiff
 * @Description: 返回第一个集合中独有的元素。不存在的集合 key 将视为空集
 * @Date: 2022/6/11 15:28
 * @Author: isheihei
 */
public class SDiff extends SetsCommand {
    @Override
    public CommandType type() {
        return CommandType.sdiff;
    }

    @Override
    public Resp handle(RedisClient redisClient) {
        return setsCommand(redisClient, 0);
    }
}
