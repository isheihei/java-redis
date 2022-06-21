package org.isheihei.redis.core.command.impl.zset;

import io.netty.channel.ChannelHandlerContext;
import org.isheihei.redis.common.consts.ErrorsConst;
import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.AbstractWriteCommand;
import org.isheihei.redis.core.command.CommandType;
import org.isheihei.redis.core.obj.RedisObject;
import org.isheihei.redis.core.obj.impl.RedisZSetObject;
import org.isheihei.redis.core.resp.impl.BulkString;
import org.isheihei.redis.core.resp.impl.Errors;
import org.isheihei.redis.core.resp.impl.RespInt;
import org.isheihei.redis.core.struct.RedisDataStruct;
import org.isheihei.redis.core.struct.impl.BytesWrapper;
import org.isheihei.redis.core.struct.impl.RedisZSet;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @ClassName: ZRem
 * @Description: 从有序集合key中删除指定的成员member
 * @Date: 2022/6/11 15:55
 * @Author: isheihei
 */
public class ZRem extends AbstractWriteCommand {

    private BytesWrapper key;

    private Set<BytesWrapper> members;

    @Override
    public CommandType type() {
        return CommandType.zrem;
    }

    @Override
    public void handleWrite(ChannelHandlerContext ctx, RedisClient redisClient) {
        if ((key = getBytesWrapper(ctx, array, 1)) == null) {
            return;
        }
        members = Arrays.stream(array).skip(2).map(resp -> ((BulkString) resp).getContent()).collect(Collectors.toSet());
        if (members.size() == 0) {
            ctx.writeAndFlush(String.format(ErrorsConst.COMMAND_WRONG_ARGS_NUMBER, type().toString()));
            return;
        }

        RedisObject redisObject = redisClient.getDb().get(key);
        if (redisObject == null) {
            ctx.writeAndFlush(new RespInt(0));
            return;
        }

        if (redisObject instanceof RedisZSetObject) {
            RedisDataStruct data = redisObject.data();
            if (data instanceof RedisZSet) {
                RedisZSet zSet = (RedisZSet) data;
                int res = zSet.zRem(members);
                ctx.writeAndFlush(new RespInt(res));
            } else {
                throw new UnsupportedOperationException();
            }
        } else {
            ctx.writeAndFlush(new Errors(ErrorsConst.WRONG_TYPE_OPERATION));
        }
    }

    @Override
    public void handleLoadAof(RedisClient redisClient) {
        if ((key = getBytesWrapper(array, 1)) == null) {
            return;
        }
        members = Arrays.stream(array).skip(2).map(resp -> ((BulkString) resp).getContent()).collect(Collectors.toSet());
        if (members.size() == 0) {
            return;
        }

        RedisObject redisObject = redisClient.getDb().get(key);
        if (redisObject == null) {
            return;
        }

        if (redisObject instanceof RedisZSetObject) {
            RedisDataStruct data = redisObject.data();
            if (data instanceof RedisZSet) {
                RedisZSet zSet = (RedisZSet) data;
                int res = zSet.zRem(members);
            } else {
                throw new UnsupportedOperationException();
            }
        }
    }
}
