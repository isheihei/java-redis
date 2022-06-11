package org.isheihei.redis.core.command.impl.list;

import io.netty.channel.ChannelHandlerContext;
import org.isheihei.redis.common.consts.ErrorsConsts;
import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.Command;
import org.isheihei.redis.core.command.CommandType;
import org.isheihei.redis.core.db.RedisDB;
import org.isheihei.redis.core.obj.RedisObject;
import org.isheihei.redis.core.obj.impl.RedisListObject;
import org.isheihei.redis.core.resp.Errors;
import org.isheihei.redis.core.resp.Resp;
import org.isheihei.redis.core.resp.SimpleString;
import org.isheihei.redis.core.struct.RedisDataStruct;
import org.isheihei.redis.core.struct.impl.BytesWrapper;
import org.isheihei.redis.core.struct.impl.RedisDoubleLinkedList;

/**
 * @ClassName: Lset
 * @Description: 设置列表 key 中 index 位置的元素值为 element
 * @Date: 2022/6/10 17:21
 * @Author: isheihei
 */
public class Lset implements Command {

    private Resp[] array;

    private BytesWrapper key;

    private Integer index;

    private BytesWrapper element;

    @Override
    public CommandType type() {
        return CommandType.lset;
    }

    @Override
    public void setContent(Resp[] array) {
        this.array = array;
    }

    @Override
    public void handle(ChannelHandlerContext ctx, RedisClient redisClient) {
        if ((key = getBytesWrapper(ctx, array, 1)) == null) return;
        BytesWrapper bytesIndex;
        if ((bytesIndex = getBytesWrapper(ctx, array, 2)) == null) return;
        try {
            index = Integer.parseInt(bytesIndex.toUtf8String());
        } catch (NumberFormatException e) {
            LOGGER.error("参数无法转换为数字", e);
            ctx.writeAndFlush(new Errors(ErrorsConsts.VALUE_IS_NOT_INT));
            return;
        }
        if ((element = getBytesWrapper(ctx, array, 3)) == null) return;

        RedisDB db = redisClient.getDb();
        RedisObject redisObject = db.get(key);
        if (redisObject == null) {
            ctx.writeAndFlush(new Errors(ErrorsConsts.NO_SUCH_KEY));
            return;
        }
        if (redisObject instanceof RedisListObject) {
            RedisDataStruct data = redisObject.data();
            if (data instanceof RedisDoubleLinkedList) {
                RedisDoubleLinkedList list = (RedisDoubleLinkedList) data;
                boolean flag = list.lset(index, element);
                if (flag) {
                    ctx.writeAndFlush(SimpleString.OK);
                } else {
                    ctx.writeAndFlush(new Errors(ErrorsConsts.INDEX_OUT_OF_RANGE));
                }
            } else {
                throw new UnsupportedOperationException();
            }
        } else {
            ctx.writeAndFlush(new Errors(ErrorsConsts.WRONG_TYPE_OPERATION));
        }
    }
}
