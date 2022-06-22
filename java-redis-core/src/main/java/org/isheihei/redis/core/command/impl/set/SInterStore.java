package org.isheihei.redis.core.command.impl.set;

import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.CommandType;
import org.isheihei.redis.core.resp.Resp;

/**
 * @ClassName: SInterStore
 * @Description: SINTER 并保存
 * @Date: 2022/6/11 15:37
 * @Author: isheihei
 */
public class SInterStore extends SetStoreCommand {

    @Override
    public CommandType type() {
        return CommandType.sinterstore;
    }

    @Override
    public Resp handleWrite(RedisClient redisClient) {
        return setsStoreCommand(redisClient, 1);
    }

}
