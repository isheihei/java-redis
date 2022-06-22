package org.isheihei.redis.core.command.impl.hash;

import org.isheihei.redis.common.consts.ErrorsConst;
import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.AbstractWriteCommand;
import org.isheihei.redis.core.command.CommandType;
import org.isheihei.redis.core.db.RedisDB;
import org.isheihei.redis.core.obj.RedisObject;
import org.isheihei.redis.core.obj.impl.RedisMapObject;
import org.isheihei.redis.core.resp.Resp;
import org.isheihei.redis.core.resp.impl.BulkString;
import org.isheihei.redis.core.resp.impl.Errors;
import org.isheihei.redis.core.resp.impl.SimpleString;
import org.isheihei.redis.core.struct.RedisDataStruct;
import org.isheihei.redis.core.struct.impl.BytesWrapper;
import org.isheihei.redis.core.struct.impl.RedisMap;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName: HMSet
 * @Description: 将多个 field-value 对设置到哈希表中
 * @Date: 2022/6/11 15:15
 * @Author: isheihei
 */
public class HMSet extends AbstractWriteCommand {
    private BytesWrapper key;

    private List<BytesWrapper> fvLists;

    @Override
    public CommandType type() {
        return CommandType.hmset;
    }

    @Override
    public Resp handleWrite(RedisClient redisClient) {
        // TODO 原子性未实现
        if ((key = getBytesWrapper(array, 1)) == null) {
            return new Errors(String.format(ErrorsConst.COMMAND_WRONG_ARGS_NUMBER, type().toString()));
        }
        if ((array.length - 2) == 0) {
            return new Errors(String.format(ErrorsConst.COMMAND_WRONG_ARGS_NUMBER, type().toString()));
        }
        if ((array.length - 2) % 2 != 0) {
            return new Errors(String.format(ErrorsConst.WRONG_ARGS_NUMBER, type().toString().toUpperCase()));
        }

        fvLists = Arrays.stream(array).skip(2).map(resp -> ((BulkString) resp).getContent()).collect(Collectors.toList());
        RedisDB db = redisClient.getDb();
        RedisObject redisObject = db.get(key);
        if (redisObject == null) {
            redisObject = new RedisMapObject();
            db.put(key, redisObject);
        }
        if (redisObject instanceof RedisMapObject) {
            RedisDataStruct data = redisObject.data();
            if (data instanceof RedisMap) {
                RedisMap map = ((RedisMap) redisObject.data());
                map.mset(fvLists);
                db.touchWatchKey(key);
                db.plusDirty();
                return SimpleString.OK;
            } else {
                throw new UnsupportedOperationException();
            }
        } else {
            return new Errors(ErrorsConst.WRONG_TYPE_OPERATION);
        }
    }
}
