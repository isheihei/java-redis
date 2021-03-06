package org.isheihei.redis.core.command.impl.connection;

import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.AbstractCommand;
import org.isheihei.redis.core.command.CommandType;
import org.isheihei.redis.core.resp.Resp;
import org.isheihei.redis.core.resp.impl.BulkString;
import org.isheihei.redis.core.resp.impl.SimpleString;
import org.isheihei.redis.core.struct.impl.BytesWrapper;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName: Ping
 * @Description: 用来测试连接是否存活
 * @Date: 2022/6/11 16:00
 * @Author: isheihei
 */
public class Ping extends AbstractCommand {

    private List<BytesWrapper> message;

    @Override
    public CommandType type() {
        return CommandType.ping;
    }

    @Override
    public Resp handle(RedisClient redisClient) {
        message = Arrays.stream(array).skip(1).map(resp -> ((BulkString) resp).getContent()).collect(Collectors.toList());
        if (message.size() == 0) {
            return new SimpleString("PONG");
        } else {
            return new SimpleString(message.get(0).toUtf8String());
        }
    }
}
