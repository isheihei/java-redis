package org.isheihei.redis.core.struct.impl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import org.isheihei.redis.core.struct.RedisDataStruct;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

import static org.isheihei.redis.core.command.Command.CHARSET;

/**
 * @ClassName: RedisZSet
 * @Description: Redis排序集合数据类型
 * @Date: 2022/5/31 0:25
 * @Author: isheihei
 */
public class RedisZSet extends TreeSet<ZNode> implements RedisDataStruct {

    public int zAdd(List<ZNode> zNodeList) {
        return (int) zNodeList.stream()
                .map(this::addOrUpdate)
                .filter(res -> res)
                .count();
    }

    public boolean addOrUpdate(ZNode zNode) {
        if (contains(zNode)) {
            this.remove(zNode);
            this.add(zNode);
            return false;
        } else {
            return this.add(zNode);
        }
    }

    public int zCount(double min, double max) {
        zSubSet(min, true, max, false);
        return zSubSet(min, true, max, true).size();
    }

    public List<BytesWrapper> zRange(int start, int stop, boolean withScores) {
        LinkedList<BytesWrapper> resList = new LinkedList<>();
        int size = this.size();
        if (start < 0) {
            start += size;
            if (start < 0) {
                start = 0;
            }
        }
        if (stop < 0) {
            stop += size;
            if (stop >= size) {
                stop = size - 1;
            }
        }

        if (start > stop) {
            return resList;
        }
        this.stream().skip(start).limit(stop - start + 1).forEach(zNode -> {
            resList.add(zNode.getMember());
            if (withScores) {
                resList.add(new BytesWrapper(String.valueOf(zNode.getScore()).getBytes(CHARSET)));
            }
        });
        return resList;
    }

    private SortedSet<ZNode> zSubSet(double min, boolean fromInclusive, double max, boolean toInclusive) {
        return this.subSet(
                new ZNode(min, new BytesWrapper()),
                fromInclusive,
                new ZNode(max, new BytesWrapper()),
                toInclusive
        );
    }

    public List<BytesWrapper> zRangeByScores(double min, double max, boolean withScores) {
        SortedSet<ZNode> zNodes = zSubSet(min, true, max, true);
        LinkedList<BytesWrapper> resList = new LinkedList<>();
        zNodes.forEach(zNode -> {
            resList.add(zNode.getMember());
            if (withScores) {
                resList.add(new BytesWrapper(String.valueOf(zNode.getScore()).getBytes(CHARSET)));
            }
        });
        return resList;
    }

    public Integer zRank(BytesWrapper member) {
        AtomicInteger rank = new AtomicInteger();
        boolean match = this.stream().map(zNode ->  {
            rank.getAndIncrement();
            return zNode.getMember();
        }).anyMatch(member::equals);
        return match ? rank.decrementAndGet() : null;
    }

    public int zRem(Set<BytesWrapper> members) {
        int count = 0;
        Iterator<ZNode> iterator = this.iterator();
        while (iterator.hasNext() && !members.isEmpty()) {
            ZNode next = iterator.next();
            if (members.contains(next.getMember())) {
                count++;
                members.remove(next.getMember());
                iterator.remove();
            }
        }
        return count;
    }

    public Double zSCore(BytesWrapper member) {
        for (ZNode next : this) {
            if (member.equals(next.getMember())) {
                return next.getScore();
            }
        }
        return null;
    }

    @Override
    public byte[] toBytes() {
        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.buffer();
        this.forEach(zNode -> {
            double score = zNode.getScore();
            byte[] member = zNode.getMember().getByteArray();
            byteBuf.writeDouble(score);
            byteBuf.writeInt(member.length);
            byteBuf.writeBytes(member);
        });
        return ByteBufUtil.getBytes(byteBuf);
    }

    @Override
    public void loadRdb(ByteBuf byteBuf) {
        while (byteBuf.readableBytes() > 0) {
            double score = byteBuf.readDouble();
            int memberLen = byteBuf.readInt();
            byte[] array = new byte[memberLen];
            byteBuf.readBytes(array);
            this.add(new ZNode(score, new BytesWrapper(array)));
        }
    }
}
