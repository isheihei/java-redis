package org.isheihei.redis.core.command.impl.set;

import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.CommandType;
import org.isheihei.redis.core.resp.Resp;

/**
 * @ClassName: SUnionStore
 * @Description: SUNION 并保存
 * @Date: 2022/6/11 15:39
 * @Author: isheihei
 */
public class SUnionStore extends SetStoreCommand{
    @Override
    public CommandType type() {
        return CommandType.sunionstore;
    }

    @Override
    public Resp handleWrite(RedisClient redisClient) {
        return setsStoreCommand(redisClient, 2);
    }
}
