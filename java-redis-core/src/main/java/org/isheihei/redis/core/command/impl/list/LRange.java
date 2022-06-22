package org.isheihei.redis.core.command.impl.list;

import org.isheihei.redis.common.consts.ErrorsConst;
import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.AbstractCommand;
import org.isheihei.redis.core.command.CommandType;
import org.isheihei.redis.core.db.RedisDB;
import org.isheihei.redis.core.obj.RedisObject;
import org.isheihei.redis.core.obj.impl.RedisListObject;
import org.isheihei.redis.core.resp.impl.BulkString;
import org.isheihei.redis.core.resp.impl.Errors;
import org.isheihei.redis.core.resp.Resp;
import org.isheihei.redis.core.resp.impl.RespArray;
import org.isheihei.redis.core.struct.RedisDataStruct;
import org.isheihei.redis.core.struct.impl.BytesWrapper;
import org.isheihei.redis.core.struct.impl.RedisDoubleLinkedList;

import java.util.List;

/**
 * @ClassName: LRange
 * @Description: 返回列表中指定区间内的元素
 * @Date: 2022/6/10 15:43
 * @Author: isheihei
 */
public class LRange extends AbstractCommand {

    private BytesWrapper key;
    private int          start;
    private int          end;

    @Override
    public CommandType type() {
        return CommandType.lrange;
    }

    @Override
    public Resp handle(RedisClient redisClient) {
        if ((key = getBytesWrapper(array, 1)) == null) {
            return new Errors(String.format(ErrorsConst.COMMAND_WRONG_ARGS_NUMBER, type().toString()));
        }
        BytesWrapper startString;
        if ((startString = getBytesWrapper(array, 2)) == null) {
            return new Errors(String.format(ErrorsConst.COMMAND_WRONG_ARGS_NUMBER, type().toString()));
        }
        BytesWrapper endString;
        if ((endString = getBytesWrapper(array, 3)) == null) {
            return new Errors(String.format(ErrorsConst.COMMAND_WRONG_ARGS_NUMBER, type().toString()));
        }
        try {
            start = Integer.parseInt(startString.toUtf8String());
            end = Integer.parseInt(endString.toUtf8String());
        } catch (NumberFormatException e) {
            LOGGER.error("参数无法转换为数字", e);
            return new Errors(ErrorsConst.VALUE_IS_NOT_INT);
        }
        RedisDB db = redisClient.getDb();
        RedisObject redisObject = db.get(key);
        if (redisObject == null) {
            return new RespArray(new Resp[0]);
        } else if (redisObject instanceof RedisListObject) {
            RedisDataStruct data = redisObject.data();
            if (data instanceof RedisDoubleLinkedList) {
                RedisDoubleLinkedList list = (RedisDoubleLinkedList) data;
                List<BytesWrapper> range = list.lrange(start, end);
                return new RespArray(range.stream().map(BulkString::new).toArray(Resp[]::new));
            } else {
                throw new UnsupportedOperationException();
            }
        } else {
            return new Errors(ErrorsConst.WRONG_TYPE_OPERATION);
        }

    }
}
