package org.isheihei.redis.core.struct.impl;

import org.isheihei.redis.core.struct.RedisDataStruct;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
        long count = fields.stream().map(field -> this.remove(field)).filter(Objects::nonNull).count();
        return (int) count;
    }

    public List<BytesWrapper> getAll() {
        int size = this.size();
        List<BytesWrapper> list = new ArrayList<>(size * 2);
        Iterator<Entry<BytesWrapper, BytesWrapper>> iterator = this.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<BytesWrapper, BytesWrapper> next = iterator.next();
            list.add(next.getKey());
            list.add(next.getValue());
        }
        return list;
    }

    public List<BytesWrapper> keys() {
        return new ArrayList<>(this.keySet());
    }

    public void mset(List<BytesWrapper> fvLists) {
        Iterator<BytesWrapper> iterator = fvLists.iterator();
        while (iterator.hasNext()) {
            this.put(iterator.next(), iterator.next());
        }
    }

    public List<BytesWrapper> vals() {
        return new ArrayList<>(this.values());
    }

    public List<BytesWrapper> mget(List<BytesWrapper> fields) {
        return fields.stream().map(field -> this.get(field)).collect(Collectors.toList());
    }
}
