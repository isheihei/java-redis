package org.isheihei.redis.core.command.impl.hash;


import io.netty.channel.ChannelHandlerContext;
import org.isheihei.redis.common.consts.ErrorsConsts;
import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.Command;
import org.isheihei.redis.core.command.CommandType;
import org.isheihei.redis.core.obj.RedisObject;
import org.isheihei.redis.core.obj.impl.RedisMapObject;
import org.isheihei.redis.core.resp.Errors;
import org.isheihei.redis.core.resp.Resp;
import org.isheihei.redis.core.resp.RespInt;
import org.isheihei.redis.core.struct.RedisDataStruct;
import org.isheihei.redis.core.struct.impl.BytesWrapper;
import org.isheihei.redis.core.struct.impl.RedisMap;

/**
 * @ClassName: Exists
 * @Description: 查看哈希表的指定字段 field 是否存在
 * @Date: 2022/6/11 15:15
 * @Author: isheihei
 */
public class Hexists implements Command {

    private BytesWrapper key;

    private BytesWrapper field;

    private Resp[] array;

    @Override
    public CommandType type() {
        return CommandType.hexists;
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

        RedisObject redisObject = redisClient.getDb().get(key);
        if (redisObject == null) {
            ctx.writeAndFlush(new RespInt(0));
            return;
        }
        if (redisObject instanceof RedisMapObject) {
            RedisDataStruct data = redisObject.data();
            if (data instanceof RedisMap) {
                RedisMap map = (RedisMap) data;
                if (map.containsKey(field)) {
                    ctx.writeAndFlush(new RespInt(1));
                } else {
                    ctx.writeAndFlush(new RespInt(0));
                }
            } else {
                throw new UnsupportedOperationException();
            }
        } else {
            ctx.writeAndFlush(new Errors(ErrorsConsts.WRONG_TYPE_OPERATION));
        }
    }
}