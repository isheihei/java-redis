package org.isheihei.redis.core.command.impl.set;

import io.netty.channel.ChannelHandlerContext;
import org.isheihei.redis.common.consts.ErrorsConst;
import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.AbstractCommand;
import org.isheihei.redis.core.db.RedisDB;
import org.isheihei.redis.core.obj.RedisObject;
import org.isheihei.redis.core.obj.impl.RedisSetObject;
import org.isheihei.redis.core.resp.Resp;
import org.isheihei.redis.core.resp.impl.BulkString;
import org.isheihei.redis.core.resp.impl.Errors;
import org.isheihei.redis.core.resp.impl.RespArray;
import org.isheihei.redis.core.struct.RedisDataStruct;
import org.isheihei.redis.core.struct.impl.BytesWrapper;
import org.isheihei.redis.core.struct.impl.RedisSet;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName: SetCommand
 * @Description: 多个集合之间的逻辑操作
 * @Date: 2022/6/18 4:30
 * @Author: isheihei
 */
public abstract class SetsCommand extends AbstractCommand {
    private List<BytesWrapper> keyList;

    /**
     * @Description: 处理集合交并差
     * @Param: ctx
     * @Param: redisClient
     * @Param: flag  0:diff, 1:inter, 2:union
     * @Author: isheihei
     */
    public void setsCommand(ChannelHandlerContext ctx, RedisClient redisClient, int flag) {
        keyList = Arrays.stream(array)
                .skip(1)
                .map(resp -> ((BulkString) resp).getContent())
                .collect(Collectors.toList());
        if (keyList.size() == 0) {
            ctx.writeAndFlush(new Errors(String.format(ErrorsConst.COMMAND_WRONG_ARGS_NUMBER, type().toString())));
            return;
        }

        RedisDB db = redisClient.getDb();
        RedisSet diffSet = new RedisSet();

        for (int i = 0; i < keyList.size(); i++){
            RedisObject redisObject = db.get(keyList.get(i));
            RedisSet redisSet;
            if (redisObject != null) {
                if (redisObject instanceof RedisSetObject) {
                    RedisDataStruct data = redisObject.data();
                    if (data instanceof RedisSet) {
                        redisSet = ((RedisSet) data);
                    } else {
                        throw new UnsupportedOperationException();
                    }
                } else {
                    ctx.writeAndFlush(new Errors(ErrorsConst.WRONG_TYPE_OPERATION));
                    return;
                }
            } else {
                redisSet = new RedisSet();
            }

            // 先把原始集合放进去
            if (i == 0) {
                diffSet.addAll(redisSet);
            } else {
                if (flag == 0) {
                    // 把后面的集合求差集
                    diffSet.removeAll(redisSet);
                } else if (flag == 1) {
                    // 把后面的集合求交集
                    diffSet.retainAll(redisSet);
                } else if (flag == 2) {
                    // 把后面的集合求并集
                    diffSet.addAll(redisSet);
                }
            }
        }
        ctx.writeAndFlush(new RespArray(diffSet.stream().map(BulkString::new).toArray(Resp[]::new)));
    }
}
