package org.isheihei.redis.core.struct.impl;

import io.netty.buffer.ByteBuf;
import org.isheihei.redis.core.struct.RedisDataStruct;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @ClassName: RedisSet
 * @Description: Redis集合数据类型
 * @Date: 2022/5/31 0:25
 * @Author: isheihei
 */
public class RedisSet extends HashSet<BytesWrapper> implements RedisDataStruct {
    // TODO
    @Override
    public byte[] toBytes() {
        return new byte[0];
    }

    // TODO
    @Override
    public void loadRdb(ByteBuf bufferPolled) {
    }

    public int addItems(List<BytesWrapper> items) {

        return (int) items.stream()
                .map(item -> this.add(item))
                .filter(item -> item == true)
                .count();
    }

    public RedisSet diff(RedisSet set, Set<BytesWrapper> diffSet) {
        RedisSet res = new RedisSet();
        res.addAll(set);
        res.removeAll(diffSet);
        return res;
    }

    public RedisSet inter(RedisSet set, Set<BytesWrapper> retainSet) {
        RedisSet res = new RedisSet();
        res.addAll(set);
        res.retainAll(retainSet);
        return res;
    }

    public int rem(List<BytesWrapper> items) {
        return (int) items.stream()
                .map(item -> this.remove(item))
                .filter(res -> res == true)
                .count();
    }
}
