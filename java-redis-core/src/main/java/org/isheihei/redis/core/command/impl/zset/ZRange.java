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
import org.isheihei.redis.core.resp.impl.RespArray;
import org.isheihei.redis.core.struct.RedisDataStruct;
import org.isheihei.redis.core.struct.impl.BytesWrapper;
import org.isheihei.redis.core.struct.impl.RedisZSet;

import java.util.List;

/**
 * @ClassName: ZRange
 * @Description: 返回有序集中，指定区间内的成员
 * @Date: 2022/6/11 15:54
 * @Author: isheihei
 */
public class ZRange extends AbstractCommand {

    private BytesWrapper key;

    private int start;

    private int stop;

    private boolean withScores;

    @Override
    public CommandType type() {
        return CommandType.zrange;
    }

    @Override
    public Resp handle(RedisClient redisClient) {
        if ((key = getBytesWrapper(array, 1)) == null) {
            return new Errors(String.format(ErrorsConst.COMMAND_WRONG_ARGS_NUMBER, type().toString()));
        }
        BytesWrapper startBytes;
        BytesWrapper stopBytes;
        if ((startBytes = getBytesWrapper(array, 2)) == null) {
            return new Errors(String.format(ErrorsConst.COMMAND_WRONG_ARGS_NUMBER, type().toString()));
        }
        if ((stopBytes = getBytesWrapper(array, 3)) == null) {
            return new Errors(String.format(ErrorsConst.COMMAND_WRONG_ARGS_NUMBER, type().toString()));
        }
        try {
            start = Integer.parseInt(startBytes.toUtf8String());
            stop = Integer.parseInt(stopBytes.toUtf8String());
        } catch (NumberFormatException e) {
            LOGGER.error("参数无法转换为数字", e);
            return new Errors(ErrorsConst.MIN_OR_MAX_NOT_FLOAT);
        }

        BytesWrapper arg = getBytesWrapper(array, 4);
        if (arg != null) {
            if ("withscores".equalsIgnoreCase(arg.toUtf8String())) {
                withScores = true;
            }
        }


        RedisObject redisObject = redisClient.getDb().get(key);
        if (redisObject instanceof RedisZSetObject) {
            RedisDataStruct data = redisObject.data();
            if (data instanceof RedisZSet) {
                RedisZSet zSet = (RedisZSet) data;
                List<BytesWrapper> resList = zSet.zRange(start, stop, withScores);
                return new RespArray(resList.stream().map(BulkString::new).toArray(Resp[]::new));
            } else {
                throw new UnsupportedOperationException();
            }
        } else {
            return new Errors(ErrorsConst.WRONG_TYPE_OPERATION);
        }
    }
}
