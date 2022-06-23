package org.isheihei.redis.core.struct.impl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
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

    public int addItems(List<BytesWrapper> items) {
        return (int) items.stream()
                .map(this::add)
                .filter(item -> item)
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
                .map(this::remove)
                .filter(res -> res)
                .count();
    }

    @Override
    public byte[] toBytes() {
        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.buffer();
        this.forEach(item -> {
            byte[] array = item.getByteArray();
            byteBuf.writeInt(array.length);
            byteBuf.writeBytes(array);
        });
        return ByteBufUtil.getBytes(byteBuf);
    }

    @Override
    public void loadRdb(ByteBuf byteBuf) {
        while (byteBuf.readableBytes() > 0) {
            int len = byteBuf.readInt();
            byte[] array = new byte[len];
            byteBuf.readBytes(array);
            this.add(new BytesWrapper(array));
        }
    }
}
