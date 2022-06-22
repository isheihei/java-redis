package org.isheihei.redis.core.command.impl.key;

import org.isheihei.redis.common.consts.ErrorsConst;
import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.AbstractCommand;
import org.isheihei.redis.core.command.CommandType;
import org.isheihei.redis.core.db.RedisDB;
import org.isheihei.redis.core.obj.RedisObject;
import org.isheihei.redis.core.resp.Resp;
import org.isheihei.redis.core.resp.impl.Errors;
import org.isheihei.redis.core.resp.impl.RespInt;
import org.isheihei.redis.core.struct.impl.BytesWrapper;

import java.util.concurrent.TimeUnit;

/**
 * @ClassName: Ttl
 * @Description: 以秒为单位返回 key 的剩余过期时间
 * @Date: 2022/6/11 15:47
 * @Author: isheihei
 */
public class Ttl extends AbstractCommand {

    private BytesWrapper key;

    @Override
    public CommandType type() {
        return CommandType.ttl;
    }

    @Override
    public Resp handle(RedisClient redisClient) {
        if ((key = getBytesWrapper(array, 1)) == null) {
            return new Errors(String.format(ErrorsConst.COMMAND_WRONG_ARGS_NUMBER, type().toString()));
        }
        RedisDB db = redisClient.getDb();
        RedisObject redisObject = db.get(key);
        if (redisObject == null) {
            return new RespInt(-2);
        }
        Long ttl = db.getTtl(key);
        if (ttl == null) {
            return new RespInt(-1);
        } else {
            return new RespInt(((int) TimeUnit.MILLISECONDS.toSeconds(ttl - System.currentTimeMillis())));
        }
    }
}
