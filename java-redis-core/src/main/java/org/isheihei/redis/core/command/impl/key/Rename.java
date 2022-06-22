package org.isheihei.redis.core.command.impl.key;

import io.netty.channel.ChannelHandlerContext;
import org.isheihei.redis.common.consts.ErrorsConst;
import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.AbstractWriteCommand;
import org.isheihei.redis.core.command.CommandType;
import org.isheihei.redis.core.resp.impl.Errors;
import org.isheihei.redis.core.resp.impl.SimpleString;
import org.isheihei.redis.core.struct.impl.BytesWrapper;

/**
 * @ClassName: Rename
 * @Description: 修改 key 的名字为 newkey
 * @Date: 2022/6/11 15:46
 * @Author: isheihei
 */
public class Rename extends AbstractWriteCommand {

    private BytesWrapper oldKey;

    private BytesWrapper newKey;

    @Override
    public CommandType type() {
        return CommandType.rename;
    }

    @Override
    public void handleWrite(ChannelHandlerContext ctx, RedisClient redisClient) {
        if ((oldKey = getBytesWrapper(ctx, array, 1)) == null) {
            return;
        }
        if ((newKey = getBytesWrapper(ctx, array, 2)) == null) {
            return;
        }

        boolean res = redisClient.getDb().reName(oldKey, newKey);
        if (res) {
            ctx.writeAndFlush(SimpleString.OK);
        } else {
            ctx.writeAndFlush(new Errors(ErrorsConst.NO_SUCH_KEY));
        }
    }

    @Override
    public void handleLoadAof(RedisClient redisClient) {
        if ((oldKey = getBytesWrapper(array, 1)) == null) {
            return;
        }
        if ((newKey = getBytesWrapper(array, 2)) == null) {
            return;
        }
        redisClient.getDb().reName(oldKey, newKey);
    }
}
