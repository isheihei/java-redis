package org.isheihei.redis.core.command;

import org.isheihei.redis.core.command.impl.Auth;
import org.isheihei.redis.core.command.impl.Client;
import org.isheihei.redis.core.command.impl.Config;
import org.isheihei.redis.core.command.impl.list.*;
import org.isheihei.redis.core.command.impl.string.*;

import java.util.function.Supplier;

/**
 * @ClassName: CommandType
 * @Description:  操作命令枚举
 * @Date: 2022/6/8 18:19
 * @Author: isheihei
 */
public enum CommandType {
    auth(Auth::new), client(Client::new), config(Config::new),
    get(Get::new), set(Set::new), mget(Mget::new), mset(Mset::new), setex(SetEx::new), setnx(SetNx::new),
    lpush(Lpush::new), lrange(Lrange::new), lrem(Lrem::new), rpush(Rpush::new), lpop(Lpop::new), rpop(Rpop::new);
//    info(Info::new), client(Client::new), set(Set::new), type(Type::new),//
//    ttl(Ttl::new), get(Get::new), quit(Quit::new),//
//    setnx(SetNx::new), lpush(Lpush::new), lrange(Lrange::new), lrem(Lrem::new), rpush(Rpush::new), del(Del::new), sadd(Sadd::new),//
//    sscan(Sscan::new), srem(Srem::new), hset(Hset::new), hscan(Hscan::new), hdel(Hdel::new),//
//    zadd(Zadd::new), zrevrange(Zrevrange::new), zrem(Zrem::new), setex(SetEx::new), exists(Exists::new), expire(Expire::new),
//    ping(Ping::new),select(Select::new),keys(Keys::new),incr(Incr::new),decr(Decr::new),mset(Mset::new),mget(Mget::new),
        //
    ;

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
