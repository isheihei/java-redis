package org.isheihei.redis.core.command.impl.connection;

import org.isheihei.redis.common.consts.ErrorsConst;
import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.AbstractWriteCommand;
import org.isheihei.redis.core.command.CommandType;
import org.isheihei.redis.core.resp.Resp;
import org.isheihei.redis.core.resp.impl.Errors;
import org.isheihei.redis.core.resp.impl.SimpleString;
import org.isheihei.redis.core.struct.impl.BytesWrapper;

/**
 * @ClassName: Select
 * @Description: 切换到指定的数据库
 * @Date: 2022/6/11 16:01
 * @Author: isheihei
 */
public class Select extends AbstractWriteCommand {

    private int index;

    @Override
    public CommandType type() {
        return CommandType.select;
    }

    @Override
    public Resp handleWrite(RedisClient redisClient) {
        BytesWrapper indexBytes;
        if ((indexBytes = getBytesWrapper(array, 1)) == null) {
            return  new Errors(ErrorsConst.INVALID_DB_INDEX);
        }
        try {
            index = Integer.parseInt(indexBytes.toUtf8String());
        } catch (NumberFormatException e) {
            LOGGER.error("数据库索引不是整数类型");
            return new Errors(ErrorsConst.INVALID_DB_INDEX);
        }
        if (redisClient.setDb(index)) {
            return SimpleString.OK;
        } else {
            return new Errors(ErrorsConst.INVALID_DB_INDEX);
        }
    }
}
