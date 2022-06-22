package org.isheihei.redis.core.command;

import org.isheihei.redis.core.command.impl.connection.Auth;
import org.isheihei.redis.core.command.impl.connection.Echo;
import org.isheihei.redis.core.command.impl.connection.Ping;
import org.isheihei.redis.core.command.impl.connection.Quit;
import org.isheihei.redis.core.command.impl.connection.Select;
import org.isheihei.redis.core.command.impl.hash.HDel;
import org.isheihei.redis.core.command.impl.hash.HExists;
import org.isheihei.redis.core.command.impl.hash.HGet;
import org.isheihei.redis.core.command.impl.hash.HGetAll;
import org.isheihei.redis.core.command.impl.hash.HKeys;
import org.isheihei.redis.core.command.impl.hash.HMGet;
import org.isheihei.redis.core.command.impl.hash.HMSet;
import org.isheihei.redis.core.command.impl.hash.HSet;
import org.isheihei.redis.core.command.impl.hash.HVals;
import org.isheihei.redis.core.command.impl.key.Del;
import org.isheihei.redis.core.command.impl.key.Exists;
import org.isheihei.redis.core.command.impl.key.Expire;
import org.isheihei.redis.core.command.impl.key.Keys;
import org.isheihei.redis.core.command.impl.key.Persist;
import org.isheihei.redis.core.command.impl.key.Rename;
import org.isheihei.redis.core.command.impl.key.Ttl;
import org.isheihei.redis.core.command.impl.key.Type;
import org.isheihei.redis.core.command.impl.list.LIndex;
import org.isheihei.redis.core.command.impl.list.LLen;
import org.isheihei.redis.core.command.impl.list.LPop;
import org.isheihei.redis.core.command.impl.list.LPush;
import org.isheihei.redis.core.command.impl.list.LRange;
import org.isheihei.redis.core.command.impl.list.LRem;
import org.isheihei.redis.core.command.impl.list.LSet;
import org.isheihei.redis.core.command.impl.list.RPop;
import org.isheihei.redis.core.command.impl.list.RPush;
import org.isheihei.redis.core.command.impl.server.BgSave;
import org.isheihei.redis.core.command.impl.server.Client;
import org.isheihei.redis.core.command.impl.server.Config;
import org.isheihei.redis.core.command.impl.server.DbSize;
import org.isheihei.redis.core.command.impl.server.FlushAll;
import org.isheihei.redis.core.command.impl.server.FlushDb;
import org.isheihei.redis.core.command.impl.server.Save;
import org.isheihei.redis.core.command.impl.set.SAdd;
import org.isheihei.redis.core.command.impl.set.SCard;
import org.isheihei.redis.core.command.impl.set.SDiff;
import org.isheihei.redis.core.command.impl.set.SDiffStore;
import org.isheihei.redis.core.command.impl.set.SInter;
import org.isheihei.redis.core.command.impl.set.SInterStore;
import org.isheihei.redis.core.command.impl.set.SIsMember;
import org.isheihei.redis.core.command.impl.set.SMembers;
import org.isheihei.redis.core.command.impl.set.SRem;
import org.isheihei.redis.core.command.impl.set.SUnion;
import org.isheihei.redis.core.command.impl.set.SUnionStore;
import org.isheihei.redis.core.command.impl.string.Append;
import org.isheihei.redis.core.command.impl.string.Get;
import org.isheihei.redis.core.command.impl.string.MGet;
import org.isheihei.redis.core.command.impl.string.MSet;
import org.isheihei.redis.core.command.impl.string.Set;
import org.isheihei.redis.core.command.impl.string.SetEx;
import org.isheihei.redis.core.command.impl.string.SetNx;
import org.isheihei.redis.core.command.impl.zset.ZAdd;
import org.isheihei.redis.core.command.impl.zset.ZCard;
import org.isheihei.redis.core.command.impl.zset.ZCount;
import org.isheihei.redis.core.command.impl.zset.ZRange;
import org.isheihei.redis.core.command.impl.zset.ZRangeByScore;
import org.isheihei.redis.core.command.impl.zset.ZRank;
import org.isheihei.redis.core.command.impl.zset.ZRem;
import org.isheihei.redis.core.command.impl.zset.ZScore;

import java.util.function.Supplier;

/**
 * @ClassName: CommandType
 * @Description:  操作命令枚举
 * @Date: 2022/6/8 18:19
 * @Author: isheihei
 */
public enum CommandType {
    auth(Auth::new), client(Client::new), config(Config::new), echo(Echo::new), ping(Ping::new), quit(Quit::new), select(Select::new),flushall(FlushAll::new), dbsize(DbSize::new), flushdb(FlushDb::new),bgsave(BgSave::new), save(Save::new),
    expire(Expire::new),del(Del::new), exists(Exists::new), keys(Keys::new), persist(Persist::new), rename(Rename::new), ttl(Ttl::new), type(Type::new),
    get(Get::new), set(Set::new), mget(MGet::new), mset(MSet::new), append(Append::new), setex(SetEx::new), setnx(SetNx::new),
    lpush(LPush::new), lrange(LRange::new), lrem(LRem::new), rpush(RPush::new), lpop(LPop::new), rpop(RPop::new), lset(LSet::new), lindex(LIndex::new), llen(LLen::new),
    hdel(HDel::new), hexists(HExists::new), hget(HGet::new), hgetall(HGetAll::new), hkeys(HKeys::new), hset(HSet::new), hmset(HMSet::new), hvals(HVals::new), hmget(HMGet::new),
    sadd(SAdd::new), scard(SCard::new), sdiff(SDiff::new), smembers(SMembers::new), sismember(SIsMember::new), sdiffstore(SDiffStore::new), sinter(SInter::new), sinterstore(SInterStore::new), srem(SRem::new), sunion(SUnion::new), sunionstore(SUnionStore::new),
    zadd(ZAdd::new), zcard(ZCard::new), zcount(ZCount::new), zrange(ZRange::new), zrangebyscore(ZRangeByScore::new), zrank(ZRank::new), zrem(ZRem::new), zscore(ZScore::new);
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
