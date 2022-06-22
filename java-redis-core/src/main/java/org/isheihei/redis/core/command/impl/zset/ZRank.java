package org.isheihei.redis.core.command.impl.zset;

import org.isheihei.redis.common.consts.ErrorsConst;
import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.AbstractCommand;
import org.isheihei.redis.core.command.CommandType;
import org.isheihei.redis.core.obj.RedisObject;
import org.isheihei.redis.core.obj.impl.RedisZSetObject;
import org.isheihei.redis.core.resp.Resp;
import org.isheihei.redis.core.resp.impl.BulkString;
import org.isheihei.redis.core.resp.impl.Errors;
import org.isheihei.redis.core.resp.impl.RespInt;
import org.isheihei.redis.core.struct.RedisDataStruct;
import org.isheihei.redis.core.struct.impl.BytesWrapper;
import org.isheihei.redis.core.struct.impl.RedisZSet;

/**
 * @ClassName: ZRank
 * @Description: 返回有序集key中成员member的排名
 * @Date: 2022/6/11 15:54
 * @Author: isheihei
 */
public class ZRank extends AbstractCommand {

    private BytesWrapper key;

    private BytesWrapper member;

    @Override
    public CommandType type() {
        return CommandType.zrank;
    }

    @Override
    public Resp handle(RedisClient redisClient) {
        if ((key = getBytesWrapper(array, 1)) == null) {
            return new Errors(String.format(ErrorsConst.COMMAND_WRONG_ARGS_NUMBER, type().toString()));
        }
        if ((member = getBytesWrapper(array, 2)) == null) {
            return new Errors(String.format(ErrorsConst.COMMAND_WRONG_ARGS_NUMBER, type().toString()));
        }

        RedisObject redisObject = redisClient.getDb().get(key);
        if (redisObject == null) {
            return BulkString.NullBulkString;
        }

        if (redisObject instanceof RedisZSetObject) {
            RedisDataStruct data = redisObject.data();
            if (data instanceof RedisZSet) {
                RedisZSet zSet = (RedisZSet) data;
                Integer rank = zSet.zRank(member);
                if (rank != null) {
                    return new RespInt(rank);
                } else {
                    return BulkString.NullBulkString;
                }
            } else {
                throw new UnsupportedOperationException();
            }
        } else {
            return new Errors(ErrorsConst.WRONG_TYPE_OPERATION);
        }
    }
}
