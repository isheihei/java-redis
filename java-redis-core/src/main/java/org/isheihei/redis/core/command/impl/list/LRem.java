package org.isheihei.redis.core.command.impl.list;

import org.isheihei.redis.common.consts.ErrorsConst;
import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.AbstractWriteCommand;
import org.isheihei.redis.core.command.CommandType;
import org.isheihei.redis.core.db.RedisDB;
import org.isheihei.redis.core.obj.RedisObject;
import org.isheihei.redis.core.obj.impl.RedisListObject;
import org.isheihei.redis.core.resp.Resp;
import org.isheihei.redis.core.resp.impl.Errors;
import org.isheihei.redis.core.resp.impl.RespInt;
import org.isheihei.redis.core.struct.RedisDataStruct;
import org.isheihei.redis.core.struct.impl.BytesWrapper;
import org.isheihei.redis.core.struct.impl.RedisDoubleLinkedList;

/**
 * @ClassName: LRem
 * @Description: 从列表 key 中删除前 count 个值等于 element 的元素
 * @Date: 2022/6/10 15:43
 * @Author: isheihei
 */
public class LRem extends AbstractWriteCommand {

    private BytesWrapper key;

    private Integer count;

    private BytesWrapper element;

    @Override
    public CommandType type() {
        return CommandType.lrem;
    }

    @Override
    public Resp handleWrite(RedisClient redisClient) {
        if ((key = getBytesWrapper(array, 1)) == null) {
            return new Errors(String.format(ErrorsConst.COMMAND_WRONG_ARGS_NUMBER, type().toString()));
        }

        try {
            count = Integer.valueOf(getBytesWrapper(array, 2).toUtf8String());
        } catch (NumberFormatException e) {
            LOGGER.error("参数无法转换为数字", e);
            return new Errors(ErrorsConst.VALUE_IS_NOT_INT);
        }
        element = getBytesWrapper(array, 3);
        if (count == null || element == null) {
            return new Errors(String.format(ErrorsConst.COMMAND_WRONG_ARGS_NUMBER, type().toString()));
        }
        RedisDB db = redisClient.getDb();
        RedisObject redisObject = db.get(key);
        if (redisObject instanceof RedisListObject) {
            RedisDataStruct data = redisObject.data();
            if (data instanceof RedisDoubleLinkedList) {
                int removeCount = ((RedisDoubleLinkedList) data).lrem(count, element);
                db.touchWatchKey(key);
                db.plusDirty();
                return new RespInt(removeCount);
            } else {
                throw new UnsupportedOperationException();
            }
        } else {
            return new Errors(ErrorsConst.WRONG_TYPE_OPERATION);
        }
    }
}
