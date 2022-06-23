package org.isheihei.redis.core.struct.impl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import org.isheihei.redis.core.struct.RedisDataStruct;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @ClassName: RedisMap
 * @Description: Redis字典数据类型
 * @Date: 2022/5/31 0:25
 * @Author: isheihei
 */
public class RedisMap extends HashMap<BytesWrapper, BytesWrapper> implements RedisDataStruct {
    public int del(List<BytesWrapper> fields) {
        long count = fields.stream().map(this::remove).filter(Objects::nonNull).count();
        return (int) count;
    }

    public List<BytesWrapper> getAll() {
        List<BytesWrapper> list = new LinkedList<>();
        for (Entry<BytesWrapper, BytesWrapper> entry : entrySet()) {
            list.add(entry.getKey());
            list.add(entry.getValue());
        }
        return list;
    }

    public List<BytesWrapper> keys() {
        return new ArrayList<>(this.keySet());
    }

    public void mSet(List<BytesWrapper> fvLists) {
        Iterator<BytesWrapper> iterator = fvLists.iterator();
        while (iterator.hasNext()) {
            this.put(iterator.next(), iterator.next());
        }
    }

    public List<BytesWrapper> vals() {
        return new ArrayList<>(this.values());
    }

    public List<BytesWrapper> mGet(List<BytesWrapper> fields) {
        return fields.stream().map(this::get).collect(Collectors.toList());
    }

    @Override
    public byte[] toBytes() {
        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.buffer();
        for (Entry<BytesWrapper, BytesWrapper> entry : this.entrySet()) {
            byte[] key = entry.getKey().getByteArray();
            byte[] value = entry.getValue().getByteArray();
            byteBuf.writeInt(key.length);
            byteBuf.writeBytes(key);
            byteBuf.writeInt(value.length);
            byteBuf.writeBytes(value);
        }
        return ByteBufUtil.getBytes(byteBuf);
    }

    @Override
    public void loadRdb(ByteBuf byteBuf) {
        while (byteBuf.readableBytes() > 0) {
            int keyLen = byteBuf.readInt();
            byte[] key = new byte[keyLen];
            byteBuf.readBytes(key);
            int valueLen = byteBuf.readInt();
            byte[] value = new byte[valueLen];
            byteBuf.readBytes(value);
            this.put(new BytesWrapper(key), new BytesWrapper(value));
        }
    }
}
