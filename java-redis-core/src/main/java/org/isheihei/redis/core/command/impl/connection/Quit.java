package org.isheihei.redis.core.command.impl.connection;

import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.AbstractCommand;
import org.isheihei.redis.core.command.CommandType;
import org.isheihei.redis.core.resp.Resp;
import org.isheihei.redis.core.resp.impl.SimpleString;

/**
 * @ClassName: Quit
 * @Description: 请求服务器关闭连接 TODO handler处理关闭
 * @Date: 2022/6/11 16:00
 * @Author: isheihei
 */
public class Quit extends AbstractCommand {
    @Override
    public CommandType type()
    {
        return CommandType.quit;
    }

    @Override
    public Resp handle(RedisClient redisClient)
    {
        return SimpleString.OK;
    }
}
