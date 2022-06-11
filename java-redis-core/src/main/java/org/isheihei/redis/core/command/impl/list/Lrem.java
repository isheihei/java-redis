package org.isheihei.redis.core.command.impl.list;

import io.netty.channel.ChannelHandlerContext;
import org.isheihei.redis.common.consts.ErrorsConsts;
import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.Command;
import org.isheihei.redis.core.command.CommandType;
import org.isheihei.redis.core.obj.RedisObject;
import org.isheihei.redis.core.obj.impl.RedisListObject;
import org.isheihei.redis.core.resp.Errors;
import org.isheihei.redis.core.resp.Resp;
import org.isheihei.redis.core.resp.RespInt;
import org.isheihei.redis.core.struct.RedisDataStruct;
import org.isheihei.redis.core.struct.impl.BytesWrapper;
import org.isheihei.redis.core.struct.impl.RedisDoubleLinkedList;

/**
 * @ClassName: Lrem
 * @Description: 从列表 key 中删除前 count 个值等于 element 的元素
 * @Date: 2022/6/10 15:43
 * @Author: isheihei
 */
public class Lrem implements Command {

    private BytesWrapper key;

    private Integer count;

    private BytesWrapper element;

    private Resp[] array;

    @Override
    public CommandType type() {
        return CommandType.lrem;
    }

    @Override
    public void setContent(Resp[] array) {
        this.array = array;
    }

    @Override
    public void handle(ChannelHandlerContext ctx, RedisClient redisClient) {
        key = getBytesWrapper(array, 1);
        if (key == null) {
            ctx.writeAndFlush(new RespInt(0));
            return;
        }

        try {
            count = Integer.valueOf(getBytesWrapper(ctx, array, 2).toUtf8String());
        } catch (NumberFormatException e) {
            LOGGER.error("参数无法转换为数字", e);
            ctx.writeAndFlush(new Errors(ErrorsConsts.VALUE_IS_NOT_INT));
            return;
        }
        element = getBytesWrapper(ctx, array, 3);
        if (count == null || element == null) {
            return;
        }

        RedisObject redisObject = redisClient.getDb().get(key);
        if (redisObject instanceof RedisListObject) {
            RedisDataStruct data = ((RedisListObject) redisObject).data();
            if (data instanceof RedisDoubleLinkedList) {
                int removeCount = ((RedisDoubleLinkedList) data).lrem(count, element);
                ctx.writeAndFlush(new RespInt(removeCount));
            } else {
                throw new UnsupportedOperationException();
            }
        } else {
            ctx.writeAndFlush(new Errors(ErrorsConsts.WRONG_TYPE_OPERATION));
            return;
        }
    }
}
