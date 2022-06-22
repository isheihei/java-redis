package org.isheihei.redis.core.command.impl.hash;

import org.isheihei.redis.common.consts.ErrorsConst;
import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.AbstractCommand;
import org.isheihei.redis.core.command.CommandType;
import org.isheihei.redis.core.obj.RedisObject;
import org.isheihei.redis.core.obj.impl.RedisMapObject;
import org.isheihei.redis.core.resp.impl.BulkString;
import org.isheihei.redis.core.resp.impl.Errors;
import org.isheihei.redis.core.resp.Resp;
import org.isheihei.redis.core.resp.impl.RespArray;
import org.isheihei.redis.core.struct.RedisDataStruct;
import org.isheihei.redis.core.struct.impl.BytesWrapper;
import org.isheihei.redis.core.struct.impl.RedisMap;

import java.util.List;

/**
 * @ClassName: HKeys
 * @Description: 返回存储在 key 中哈希表的所有 field
 * @Date: 2022/6/11 15:16
 * @Author: isheihei
 */
public class HKeys extends AbstractCommand {

    private BytesWrapper key;

    @Override
    public CommandType type() {
        return CommandType.hkeys;
    }


    @Override
    public Resp handle(RedisClient redisClient) {
        if ((key = getBytesWrapper(array, 1)) == null) {
            return new Errors(String.format(ErrorsConst.COMMAND_WRONG_ARGS_NUMBER, type().toString()));
        }
        RedisObject redisObject = redisClient.getDb().get(key);
        if (redisObject == null) {
            return new RespArray(new Resp[0]);
        }

        if (redisObject instanceof RedisMapObject) {
            RedisDataStruct data = redisObject.data();
            if (data instanceof RedisMap) {
                RedisMap map = (RedisMap) data;
                List<BytesWrapper> res = map.keys();
                return new RespArray(res.stream().map(BulkString::new).toArray(Resp[]::new));
            } else {
                throw new UnsupportedOperationException();
            }
        } else {
            return new Errors(ErrorsConst.WRONG_TYPE_OPERATION);
        }
    }
}
