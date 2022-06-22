package org.isheihei.redis.core.struct.impl;

/**
 * @ClassName: ZNode
 * @Description: ZSet节点类
 * @Date: 2022/6/18 5:52
 * @Author: isheihei
 */
public class ZNode implements Comparable<ZNode> {
    private BytesWrapper member;
    private double         score;

    public ZNode(double score, BytesWrapper member)
    {
        this.member = member;
        this.score = score;
    }

    public BytesWrapper getMember()
    {
        return member;
    }

    public double getScore()
    {
        return score;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }
        ZNode zNode = (ZNode) o;
        return member.equals(zNode.member);
    }

    @Override
    public int hashCode()
    {
        return member.hashCode();
    }

    @Override
    public int compareTo(ZNode o) {
        int scoreComp = Double.compare(score, o.score);
        int memberComp = member.compareTo(o.member);
        if (memberComp == 0) {
            // member 相等 则相等
            return 0;
        } else if (scoreComp == 0) {
            // member不相等且 score相等 按照member排序
            return memberComp;
        } else {
            // member 和 score都不相等 按照score排序
            return scoreComp;
        }
    }


    @Override
    public String toString() {
        return "member : " + member.toUtf8String() + ", score : " + score;
    }
}
