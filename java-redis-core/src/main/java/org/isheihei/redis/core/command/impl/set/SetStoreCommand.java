package org.isheihei.redis.core.command.impl.set;

import io.netty.channel.ChannelHandlerContext;
import org.isheihei.redis.common.consts.ErrorsConst;
import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.AbstractWriteCommand;
import org.isheihei.redis.core.db.RedisDB;
import org.isheihei.redis.core.obj.RedisObject;
import org.isheihei.redis.core.obj.impl.RedisSetObject;
import org.isheihei.redis.core.resp.impl.BulkString;
import org.isheihei.redis.core.resp.impl.Errors;
import org.isheihei.redis.core.resp.impl.RespInt;
import org.isheihei.redis.core.struct.RedisDataStruct;
import org.isheihei.redis.core.struct.impl.BytesWrapper;
import org.isheihei.redis.core.struct.impl.RedisSet;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName: SetStoreCommand
 * @Description: 多个集合之间的逻辑操作并保存为新的key
 * @Date: 2022/6/18 4:31
 * @Author: isheihei
 */
public abstract class SetStoreCommand extends AbstractWriteCommand {
    private BytesWrapper key;

    private List<BytesWrapper> keyList;

    /**
     * @Description: 集合之间交并差并保存
     * @Param: ctx
     * @Param: redisClient
     * @Param: flag  0:diff, 1:inter, 2:union
     * @Author: isheihei
     */
    public void setsStoreCommand(ChannelHandlerContext ctx, RedisClient redisClient, int flag) {
        if ((key = getBytesWrapper(ctx, array, 1)) == null) {
            return;
        }
        keyList = Arrays.stream(array)
                .skip(2)
                .map(resp -> ((BulkString) resp).getContent())
                .collect(Collectors.toList());
        if (keyList.size() == 0) {
            ctx.writeAndFlush(new Errors(String.format(ErrorsConst.COMMAND_WRONG_ARGS_NUMBER, type().toString())));
            return;
        }

        RedisSetObject redisSetObject = new RedisSetObject();
        RedisSet res = (RedisSet) redisSetObject.data();
        RedisDB db = redisClient.getDb();

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
                res.addAll(redisSet);
            } else {
                if (flag == 0) {
                    // 把后面的集合求差集
                    res.removeAll(redisSet);
                } else if (flag == 1) {
                    // 把后面的集合求交集
                    res.retainAll(redisSet);
                } else if (flag == 2) {
                    // 把后面的集合求并集
                    res.addAll(redisSet);
                }
            }
        }
        db.put(key, redisSetObject);
        ctx.writeAndFlush(new RespInt(res.size()));
    }

    public void loadRdbSetsStoreCommand(RedisClient redisClient, int flag) {
        if ((key = getBytesWrapper(array, 1)) == null) {
            return;
        }
        keyList = Arrays.stream(array)
                .skip(2)
                .map(resp -> ((BulkString) resp).getContent())
                .collect(Collectors.toList());
        if (keyList.size() == 0) {
            return;
        }

        RedisSetObject redisSetObject = new RedisSetObject();
        RedisSet res = (RedisSet) redisSetObject.data();
        RedisDB db = redisClient.getDb();

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
                    return;
                }
            } else {
                redisSet = new RedisSet();
            }

            // 先把原始集合放进去
            if (i == 0) {
                res.addAll(redisSet);
            } else {
                if (flag == 0) {
                    // 把后面的集合求差集
                    res.removeAll(redisSet);
                } else if (flag == 1) {
                    // 把后面的集合求交集
                    res.retainAll(redisSet);
                } else if (flag == 2) {
                    // 把后面的集合求并集
                    res.addAll(redisSet);
                }
            }
        }
        db.put(key, redisSetObject);
    }
}
