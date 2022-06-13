package org.isheihei.redis.core.command;

import org.isheihei.redis.core.command.impl.connection.Auth;
import org.isheihei.redis.core.command.impl.connection.Echo;
import org.isheihei.redis.core.command.impl.connection.Ping;
import org.isheihei.redis.core.command.impl.connection.Quit;
import org.isheihei.redis.core.command.impl.connection.Select;
import org.isheihei.redis.core.command.impl.hash.Hdel;
import org.isheihei.redis.core.command.impl.hash.Hexists;
import org.isheihei.redis.core.command.impl.hash.Hget;
import org.isheihei.redis.core.command.impl.hash.Hgetall;
import org.isheihei.redis.core.command.impl.hash.Hkeys;
import org.isheihei.redis.core.command.impl.hash.Hmget;
import org.isheihei.redis.core.command.impl.hash.Hmset;
import org.isheihei.redis.core.command.impl.hash.Hset;
import org.isheihei.redis.core.command.impl.hash.Hvals;
import org.isheihei.redis.core.command.impl.key.Expire;
import org.isheihei.redis.core.command.impl.list.Lindex;
import org.isheihei.redis.core.command.impl.list.Llen;
import org.isheihei.redis.core.command.impl.list.Lpop;
import org.isheihei.redis.core.command.impl.list.Lpush;
import org.isheihei.redis.core.command.impl.list.Lrange;
import org.isheihei.redis.core.command.impl.list.Lrem;
import org.isheihei.redis.core.command.impl.list.Lset;
import org.isheihei.redis.core.command.impl.list.Rpop;
import org.isheihei.redis.core.command.impl.list.Rpush;
import org.isheihei.redis.core.command.impl.server.Client;
import org.isheihei.redis.core.command.impl.server.Config;
import org.isheihei.redis.core.command.impl.string.Append;
import org.isheihei.redis.core.command.impl.string.Get;
import org.isheihei.redis.core.command.impl.string.Mget;
import org.isheihei.redis.core.command.impl.string.Mset;
import org.isheihei.redis.core.command.impl.string.Set;
import org.isheihei.redis.core.command.impl.string.SetEx;
import org.isheihei.redis.core.command.impl.string.SetNx;

import java.util.function.Supplier;

/**
 * @ClassName: CommandType
 * @Description:  操作命令枚举
 * @Date: 2022/6/8 18:19
 * @Author: isheihei
 */
public enum CommandType {
    auth(Auth::new), client(Client::new), config(Config::new), echo(Echo::new), ping(Ping::new), quit(Quit::new), select(Select::new),
    expire(Expire::new),
    get(Get::new), set(Set::new), mget(Mget::new), mset(Mset::new), append(Append::new), setex(SetEx::new), setnx(SetNx::new),
    lpush(Lpush::new), lrange(Lrange::new), lrem(Lrem::new), rpush(Rpush::new), lpop(Lpop::new), rpop(Rpop::new), lset(Lset::new), lindex(Lindex::new), llen(Llen::new),
    hdel(Hdel::new), hexists(Hexists::new), hget(Hget::new), hgetall(Hgetall::new), hkeys(Hkeys::new), hset(Hset::new), hmset(Hmset::new), hvals(Hvals::new), hmget(Hmget::new);

    // 操作类构造器
    private final Supplier<Command> supplier;

    CommandType(Supplier supplier) {
        this.supplier = supplier;
    }

    //
    /**
     * @Description: 获取一个操作类实例对象
     * @Return: Supplier<Command>
     * @Author: isheihei
     */
    public Supplier<Command> getSupplier() {
        return supplier;
    }


}
