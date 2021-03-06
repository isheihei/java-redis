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

import java.util.List;

/**
 * @ClassName: Push
 * @Description: lpush rpush 提取公共父类 降低重复代码
 * @Date: 2022/6/10 16:17
 * @Author: isheihei
 */
public abstract class Push extends AbstractWriteCommand {

    @Override
    public abstract CommandType type();

    @Override
    public abstract Resp handleWrite(RedisClient redisClient);

    /**
     * @Description: 提取列表插入公共方法
     * @Param: redisClient
     * @Param: direct
     * @Param: key
     * @Param: values
     * @Return: Resp
     * @Author: isheihei
     */
    public Resp lrPush(RedisClient redisClient, boolean direct, BytesWrapper key, List<BytesWrapper> values) {
        if (values.size() == 0) {
            return new RespInt(0);
        }
        RedisDB db = redisClient.getDb();
        RedisObject redisObject = db.get(key);
        if (redisObject == null) {
            // 不存在列表 则创建
            redisObject = new RedisListObject();
            db.put(key, redisObject);
        }
        if (redisObject instanceof RedisListObject) {
            RedisDataStruct data = redisObject.data();
            if (data instanceof RedisDoubleLinkedList) {
                RedisDoubleLinkedList list = (RedisDoubleLinkedList) data;
                if (direct) {
                    list.lpush(values);
                } else {
                    list.rpush(values);
                }
                db.touchWatchKey(key);
                db.plusDirty();
                return new RespInt(list.size());
            } else {
                throw  new UnsupportedOperationException();
            }
        } else {
            return new Errors(ErrorsConst.WRONG_TYPE_OPERATION);
        }
    }

}
