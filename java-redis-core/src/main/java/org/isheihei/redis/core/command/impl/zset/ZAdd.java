package org.isheihei.redis.core.command.impl.zset;

import org.isheihei.redis.common.consts.ErrorsConst;
import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.AbstractWriteCommand;
import org.isheihei.redis.core.command.CommandType;
import org.isheihei.redis.core.db.RedisDB;
import org.isheihei.redis.core.obj.RedisObject;
import org.isheihei.redis.core.obj.impl.RedisZSetObject;
import org.isheihei.redis.core.resp.Resp;
import org.isheihei.redis.core.resp.impl.Errors;
import org.isheihei.redis.core.resp.impl.RespInt;
import org.isheihei.redis.core.struct.RedisDataStruct;
import org.isheihei.redis.core.struct.impl.BytesWrapper;
import org.isheihei.redis.core.struct.impl.RedisZSet;
import org.isheihei.redis.core.struct.impl.ZNode;

import java.util.LinkedList;
import java.util.List;

/**
 * @ClassName: ZAdd
 * @Description: 将一个或多个 member 元素及其 score 值加入到有序集 key 当中
 * @Date: 2022/6/11 15:51
 * @Author: isheihei
 */
public class ZAdd extends AbstractWriteCommand {

    private BytesWrapper key;

    private List<ZNode> zNodeList;

    @Override
    public CommandType type() {
        return CommandType.zadd;
    }

    @Override
    public Resp handleWrite(RedisClient redisClient) {
        if ((key = getBytesWrapper(array, 1)) == null) {
            return new Errors(String.format(ErrorsConst.COMMAND_WRONG_ARGS_NUMBER, type().toString()));
        }
        if ((array.length - 2) == 0) {
            return new Errors(String.format(ErrorsConst.COMMAND_WRONG_ARGS_NUMBER, type().toString()));
        }
        if ((array.length - 2) % 2 != 0) {
            return new Errors(ErrorsConst.SYNTAX_ERROR);
        }

        zNodeList = new LinkedList<>();
        for (int i = 2; i < array.length; i += 2) {
            try {
                double score = Double.parseDouble(getBytesWrapper(array, i).toUtf8String());
                BytesWrapper member = getBytesWrapper(array, i + 1);
                zNodeList.add(new ZNode(score, member));
            } catch (NumberFormatException e) {
                LOGGER.error("score 转换为浮点数错误");
                return new Errors(String.format(ErrorsConst.INVALID_FLOAT));
            }
        }

        // 如果 key 不存在 则创建
        RedisDB db = redisClient.getDb();
        RedisObject redisObject = db.get(key);
        if (redisObject == null) {
            redisObject = new RedisZSetObject();
            db.put(key, redisObject);
        }

        if (redisObject instanceof RedisZSetObject) {
            RedisDataStruct data = redisObject.data();
            if (data instanceof RedisZSet) {
                RedisZSet zSet = (RedisZSet) data;
                int res = zSet.zAdd(zNodeList);
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
