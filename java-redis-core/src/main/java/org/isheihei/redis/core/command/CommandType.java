package org.isheihei.redis.core.command;

import org.isheihei.redis.core.command.impl.Auth;
import org.isheihei.redis.core.command.impl.Client;
import org.isheihei.redis.core.command.impl.Config;
import org.isheihei.redis.core.command.impl.list.Lpop;
import org.isheihei.redis.core.command.impl.list.Lpush;
import org.isheihei.redis.core.command.impl.list.Lrange;
import org.isheihei.redis.core.command.impl.list.Lrem;
import org.isheihei.redis.core.command.impl.list.Lset;
import org.isheihei.redis.core.command.impl.list.Rpop;
import org.isheihei.redis.core.command.impl.list.Rpush;
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
    auth(Auth::new), client(Client::new), config(Config::new),
    get(Get::new), set(Set::new), mget(Mget::new), mset(Mset::new), setex(SetEx::new), setnx(SetNx::new),
    lpush(Lpush::new), lrange(Lrange::new), lrem(Lrem::new), rpush(Rpush::new), lpop(Lpop::new), rpop(Rpop::new), lset(Lset::new);

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
