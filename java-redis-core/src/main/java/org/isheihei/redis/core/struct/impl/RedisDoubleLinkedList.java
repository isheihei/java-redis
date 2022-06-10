package org.isheihei.redis.core.struct.impl;

import org.isheihei.redis.core.struct.RedisDataStruct;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName: RedisDoubleLinkedList
 * @Description: Redis双端列表数据类型
 * @Date: 2022/5/31 0:24
 * @Author: isheihei
 */
public class RedisDoubleLinkedList extends LinkedList<BytesWrapper> implements RedisDataStruct {
    public List<BytesWrapper> lrange(int start, int end) {
        // TODO 复数表示从后向前 以及返回
        return this.stream().skip(start).limit(end - start >= 0 ? end - start + 1 : 0).collect(Collectors.toList());
    }

    public void rpush(List<BytesWrapper> values) {
        values.stream().forEach(value -> this.offerFirst(value));
    }

    public void lpush(List<BytesWrapper> values) {
        values.stream().forEach(value -> this.offerFirst(value));

    }

    public BytesWrapper lpop() {
        return this.pollFirst();
    }

    public BytesWrapper rpop() {
        return this.pollLast();
    }
}
