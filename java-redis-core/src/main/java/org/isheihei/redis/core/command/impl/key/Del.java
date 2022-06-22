package org.isheihei.redis.core.command.impl.key;

import org.isheihei.redis.common.consts.ErrorsConst;
import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.AbstractWriteCommand;
import org.isheihei.redis.core.command.CommandType;
import org.isheihei.redis.core.db.RedisDB;
import org.isheihei.redis.core.resp.Resp;
import org.isheihei.redis.core.resp.impl.BulkString;
import org.isheihei.redis.core.resp.impl.Errors;
import org.isheihei.redis.core.resp.impl.RespInt;
import org.isheihei.redis.core.struct.impl.BytesWrapper;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName: Del
 * @Description: 删除给定的一个或多个 key
 * @Date: 2022/6/11 15:41
 * @Author: isheihei
 */
public class Del extends AbstractWriteCommand {

    private List<BytesWrapper> keyList;

    @Override
    public CommandType type() {
        return CommandType.del;
    }

    @Override
    public Resp handleWrite(RedisClient redisClient) {
        keyList = Arrays.stream(array).skip(1).map(resp -> ((BulkString) resp).getContent()).collect(Collectors.toList());
        if (keyList.size() == 0) {
            return new Errors(String.format(ErrorsConst.COMMAND_WRONG_ARGS_NUMBER, type().toString()));
        }
        RedisDB db = redisClient.getDb();
        int delete = db.delete(keyList);
        db.plusDirty();
        return new RespInt(delete);
    }
}
