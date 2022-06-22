package org.isheihei.redis.core.command.impl.zset;

import org.isheihei.redis.common.consts.ErrorsConst;
import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.AbstractCommand;
import org.isheihei.redis.core.command.CommandType;
import org.isheihei.redis.core.obj.RedisObject;
import org.isheihei.redis.core.obj.impl.RedisZSetObject;
import org.isheihei.redis.core.resp.Resp;
import org.isheihei.redis.core.resp.impl.Errors;
import org.isheihei.redis.core.resp.impl.RespInt;
import org.isheihei.redis.core.struct.RedisDataStruct;
import org.isheihei.redis.core.struct.impl.BytesWrapper;
import org.isheihei.redis.core.struct.impl.RedisZSet;

/**
 * @ClassName: ZCount
 * @Description: 有序集 key 中， score 值在 min 和 max 之间(默认包括 score 值等于 min 但不包括 max )的成员的数量
 * @Date: 2022/6/11 15:52
 * @Author: isheihei
 */
public class ZCount extends AbstractCommand {

    private BytesWrapper key;

    private double min;

    private double max;

    @Override
    public CommandType type() {
        return CommandType.zcount;
    }

    @Override
    public Resp handle(RedisClient redisClient) {
        if ((key = getBytesWrapper(array, 1)) == null) {
            return new Errors(String.format(ErrorsConst.COMMAND_WRONG_ARGS_NUMBER, type().toString()));
        }
        BytesWrapper minBytes;
        BytesWrapper maxBytes;
        if ((minBytes = getBytesWrapper(array, 2)) == null) {
            return new Errors(String.format(ErrorsConst.COMMAND_WRONG_ARGS_NUMBER, type().toString()));
        }
        if ((maxBytes = getBytesWrapper(array, 3)) == null) {
            return new Errors(String.format(ErrorsConst.COMMAND_WRONG_ARGS_NUMBER, type().toString()));
        }
        try {
            min = Double.parseDouble(minBytes.toUtf8String());
            max = Double.parseDouble(maxBytes.toUtf8String());
        } catch (NumberFormatException e) {
            LOGGER.error("参数无法转换为数字", e);
            return new Errors(ErrorsConst.MIN_OR_MAX_NOT_FLOAT);
        }

        RedisObject redisObject = redisClient.getDb().get(key);
        if (redisObject == null) {
            return new RespInt(0);
        }
        if (redisObject instanceof RedisZSetObject) {
            RedisDataStruct data = redisObject.data();
            if (data instanceof RedisZSet) {
                RedisZSet zSet = (RedisZSet) data;
                int res = zSet.zCount(min, max);
                return new RespInt(res);
            } else {
                throw new UnsupportedOperationException();
            }
        } else {
            return new Errors(ErrorsConst.WRONG_TYPE_OPERATION);
        }
    }
}
