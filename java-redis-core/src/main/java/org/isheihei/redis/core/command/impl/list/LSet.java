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
import org.isheihei.redis.core.resp.impl.SimpleString;
import org.isheihei.redis.core.struct.RedisDataStruct;
import org.isheihei.redis.core.struct.impl.BytesWrapper;
import org.isheihei.redis.core.struct.impl.RedisDoubleLinkedList;

/**
 * @ClassName: LSet
 * @Description: 设置列表 key 中 index 位置的元素值为 element
 * @Date: 2022/6/10 17:21
 * @Author: isheihei
 */
public class LSet extends AbstractWriteCommand {

    private BytesWrapper key;

    private Integer index;

    private BytesWrapper element;

    @Override
    public CommandType type() {
        return CommandType.lset;
    }

    @Override
    public Resp handleWrite(RedisClient redisClient) {
        if ((key = getBytesWrapper(array, 1)) == null) {
            return new Errors(String.format(ErrorsConst.COMMAND_WRONG_ARGS_NUMBER, type().toString()));
        }
        BytesWrapper bytesIndex;
        if ((bytesIndex = getBytesWrapper(array, 2)) == null) {
            return new Errors(String.format(ErrorsConst.COMMAND_WRONG_ARGS_NUMBER, type().toString()));
        }
        try {
            index = Integer.parseInt(bytesIndex.toUtf8String());
        } catch (NumberFormatException e) {
            LOGGER.error("参数无法转换为数字", e);
            return new Errors(ErrorsConst.VALUE_IS_NOT_INT);
        }
        if ((element = getBytesWrapper(array, 3)) == null){
            return new Errors(String.format(ErrorsConst.COMMAND_WRONG_ARGS_NUMBER, type().toString()));
        }

        RedisDB db = redisClient.getDb();
        RedisObject redisObject = db.get(key);
        if (redisObject == null) {
            return new Errors(ErrorsConst.NO_SUCH_KEY);
        }
        if (redisObject instanceof RedisListObject) {
            RedisDataStruct data = redisObject.data();
            if (data instanceof RedisDoubleLinkedList) {
                RedisDoubleLinkedList list = (RedisDoubleLinkedList) data;
                boolean flag = list.lset(index, element);
                if (flag) {
                    db.touchWatchKey(key);
                    db.plusDirty();
                    return SimpleString.OK;
                } else {
                    return new Errors(ErrorsConst.INDEX_OUT_OF_RANGE);
                }
            } else {
                throw new UnsupportedOperationException();
            }
        } else {
            return new Errors(ErrorsConst.WRONG_TYPE_OPERATION);
        }
    }
}
