package org.isheihei.redis.core.command.impl.key;

import io.netty.channel.ChannelHandlerContext;
import org.isheihei.redis.common.consts.ErrorsConst;
import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.AbstractCommand;
import org.isheihei.redis.core.command.CommandType;
import org.isheihei.redis.core.db.RedisDB;
import org.isheihei.redis.core.resp.impl.BulkString;
import org.isheihei.redis.core.resp.impl.Errors;
import org.isheihei.redis.core.resp.impl.RespInt;
import org.isheihei.redis.core.struct.impl.BytesWrapper;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName: Exists
 * @Description: 检查给定 key 是否存在
 * @Date: 2022/6/11 15:41
 * @Author: isheihei
 */
public class Exists extends AbstractCommand {

    private List<BytesWrapper> keyList;

    @Override
    public CommandType type() {
        return CommandType.exists;
    }

    @Override
    public void handle(ChannelHandlerContext ctx, RedisClient redisClient) {
        keyList = Arrays.stream(array).skip(1).map(resp -> ((BulkString) resp).getContent()).collect(Collectors.toList());
        if (keyList.size() == 0) {
            ctx.writeAndFlush(new Errors(String.format(ErrorsConst.COMMAND_WRONG_ARGS_NUMBER, type().toString())));
            return;
        }
        RedisDB db = redisClient.getDb();
        int res = db.exist(keyList);
        ctx.writeAndFlush(new RespInt(res));
    }
}
