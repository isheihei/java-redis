package org.isheihei.redis.core.command.impl.transaction;

import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.command.AbstractCommand;
import org.isheihei.redis.core.command.Command;
import org.isheihei.redis.core.command.CommandType;
import org.isheihei.redis.core.resp.Resp;
import org.isheihei.redis.core.resp.impl.BulkString;
import org.isheihei.redis.core.resp.impl.RespArray;

import java.util.ArrayList;

/**
 * @ClassName: Exec
 * @Description: 执行事务 (transaction )队列内的所有命令
 * @Date: 2022/6/22 15:50
 * @Author: isheihei
 */
public class Exec extends AbstractCommand {
    @Override
    public CommandType type() {
        return CommandType.exec;
    }

    @Override
    public Resp handle(RedisClient redisClient) {
        // 如果watch key被改动了 则全部拒绝执行
        // 如果watch未被改动，则全部执行（执行出错或者语法错误的命令都会失败）
        ArrayList<Resp> resps = new ArrayList<>();
        if (redisClient.getDirtyCas()) {
            resps.add(BulkString.NullBulkString);
        } else {
            Command cmd;
            while ((cmd = redisClient.getCommand()) != null) {
                Resp resp = cmd.handle(redisClient);
//                if (resp instanceof Errors) {
//                    resps.add(BulkString.NullBulkString);
//                    continue;
//                }
                resps.add(resp);
            }
        }
        redisClient.flushCommand();
        redisClient.setDirtyCas(false);
        redisClient.getDb().unWatchKeys(redisClient);
        redisClient.setFlag(false);
        return new RespArray(resps.toArray(new Resp[0]));
    }
}
