package org.isheihei.redis.core.command.impl.set;

import org.isheihei.redis.common.consts.ErrorsConst;
import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.AbstractWriteCommand;
import org.isheihei.redis.core.command.CommandType;
import org.isheihei.redis.core.db.RedisDB;
import org.isheihei.redis.core.obj.RedisObject;
import org.isheihei.redis.core.obj.impl.RedisSetObject;
import org.isheihei.redis.core.resp.Resp;
import org.isheihei.redis.core.resp.impl.BulkString;
import org.isheihei.redis.core.resp.impl.Errors;
import org.isheihei.redis.core.resp.impl.RespInt;
import org.isheihei.redis.core.struct.RedisDataStruct;
import org.isheihei.redis.core.struct.impl.BytesWrapper;
import org.isheihei.redis.core.struct.impl.RedisSet;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName: SAdd
 * @Description: 将一个或多个成员元素加入到集合中
 * @Date: 2022/6/11 15:27
 * @Author: isheihei
 */
public class SAdd extends AbstractWriteCommand {

    private BytesWrapper key;

    private List<BytesWrapper> items;

    @Override
    public CommandType type() {
        return CommandType.sadd;
    }

    @Override
    public Resp handleWrite(RedisClient redisClient) {
        if ((key = getBytesWrapper(array, 1)) == null) {
            return new Errors(String.format(ErrorsConst.COMMAND_WRONG_ARGS_NUMBER, type().toString()));
        }
        items = Arrays.stream(array).skip(2).map(resp -> ((BulkString) resp).getContent()).collect(Collectors.toList());
        if (items.size() == 0) {
            return new Errors(String.format(ErrorsConst.COMMAND_WRONG_ARGS_NUMBER, type().toString()));
        }
        RedisDB db = redisClient.getDb();
        RedisObject redisObject = db.get(key);
        if (redisObject == null) {
            redisObject = new RedisSetObject();
            db.put(key, redisObject);
        }
        if (redisObject instanceof RedisSetObject) {
            RedisDataStruct data = redisObject.data();
            if (data instanceof RedisSet) {
                RedisSet set = (RedisSet) data;
                int res = set.addItems(items);
                db.touchWatchKey(key);
                db.plusDirty();
                return new RespInt(res);
            } else {
                throw new UnsupportedOperationException();
            }
        } else {
            return new Errors(ErrorsConst.WRONG_TYPE_OPERATION);
        }
    }
}
