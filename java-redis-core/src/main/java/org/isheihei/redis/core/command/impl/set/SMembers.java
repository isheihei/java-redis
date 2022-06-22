package org.isheihei.redis.core.command.impl.set;

import org.isheihei.redis.common.consts.ErrorsConst;
import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.AbstractCommand;
import org.isheihei.redis.core.command.CommandType;
import org.isheihei.redis.core.obj.RedisObject;
import org.isheihei.redis.core.obj.impl.RedisSetObject;
import org.isheihei.redis.core.resp.Resp;
import org.isheihei.redis.core.resp.impl.BulkString;
import org.isheihei.redis.core.resp.impl.Errors;
import org.isheihei.redis.core.resp.impl.RespArray;
import org.isheihei.redis.core.struct.RedisDataStruct;
import org.isheihei.redis.core.struct.impl.BytesWrapper;
import org.isheihei.redis.core.struct.impl.RedisSet;

/**
 * @ClassName: SMembers
 * @Description: 存储在 key 中的集合的所有的成员，若有一个参数 key 则与 SISMEMBER 有同样的效果
 * @Date: 2022/6/11 15:37
 * @Author: isheihei
 */
public class SMembers extends AbstractCommand {

    private BytesWrapper key;

    @Override
    public CommandType type() {
        return CommandType.smembers;
    }

    @Override
    public Resp handle(RedisClient redisClient) {
        if ((key = getBytesWrapper(array, 1)) == null) {
            return new Errors(String.format(ErrorsConst.COMMAND_WRONG_ARGS_NUMBER, type().toString()));
        }
        if (getBytesWrapper(array, 2) != null) {
            SIsMember sismember = new SIsMember();
            sismember.setContent(respArray);
            return sismember.handle(redisClient);
        }

        RedisObject redisObject = redisClient.getDb().get(key);
        if (redisObject == null) {
            return new RespArray(new Resp[0]);
        }
        if (redisObject instanceof RedisSetObject) {
            RedisDataStruct data = redisObject.data();
            if (data instanceof RedisSet) {
                RedisSet set = (RedisSet) data;
                return new RespArray(set.stream().map(BulkString::new).toArray(Resp[]::new));
            } else {
                throw new UnsupportedOperationException();
            }
        } else {
            return new Errors(ErrorsConst.WRONG_TYPE_OPERATION);
        }
    }
}
