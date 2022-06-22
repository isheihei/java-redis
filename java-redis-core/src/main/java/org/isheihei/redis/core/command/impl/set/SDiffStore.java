package org.isheihei.redis.core.command.impl.set;

import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.CommandType;
import org.isheihei.redis.core.resp.Resp;

/**
 * @ClassName: SDiffStore
 * @Description: SDIFF 并保存
 * @Date: 2022/6/11 15:28
 * @Author: isheihei
 */
public class SDiffStore extends SetStoreCommand {

    @Override
    public CommandType type() {
        return CommandType.sdiffstore;
    }

    @Override
    public Resp handleWrite(RedisClient redisClient) {
       return setsStoreCommand(redisClient, 0);
    }
}
