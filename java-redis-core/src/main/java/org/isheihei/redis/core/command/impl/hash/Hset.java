package org.isheihei.redis.core.command.impl.hash;

import io.netty.channel.ChannelHandlerContext;
import org.isheihei.redis.common.consts.ErrorsConsts;
import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.Command;
import org.isheihei.redis.core.command.CommandType;
import org.isheihei.redis.core.db.RedisDB;
import org.isheihei.redis.core.obj.RedisObject;
import org.isheihei.redis.core.obj.impl.RedisMapObject;
import org.isheihei.redis.core.resp.Errors;
import org.isheihei.redis.core.resp.Resp;
import org.isheihei.redis.core.resp.RespInt;
import org.isheihei.redis.core.struct.RedisDataStruct;
import org.isheihei.redis.core.struct.impl.BytesWrapper;
import org.isheihei.redis.core.struct.impl.RedisMap;

/**
 * @ClassName: Hset
 * @Description: 存储在 key 中的哈希表的 field 字段赋值 value
 * @Date: 2022/6/11 15:16
 * @Author: isheihei
 */
public class Hset implements Command {

    private BytesWrapper key;

    private BytesWrapper field;

    private BytesWrapper value;

    private Resp[] array;

    @Override
    public CommandType type() {
        return CommandType.hset;
    }

    @Override
    public void setContent(Resp[] array) {
        this.array = array;
    }

    @Override
    public void handle(ChannelHandlerContext ctx, RedisClient redisClient) {
        if ((key = getBytesWrapper(ctx, array, 1)) == null) {
            return;
        }
        if ((field = getBytesWrapper(ctx, array, 2)) == null) {
            return;
        }
        if ((value = getBytesWrapper(ctx, array, 3)) == null) {
            return;
        }

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
                map.put(field, value);
                ctx.writeAndFlush(new RespInt(1));
            } else {
                throw new UnsupportedOperationException();
            }
        } else {
            ctx.writeAndFlush(new Errors(ErrorsConsts.WRONG_TYPE_OPERATION));
        }
    }
}
