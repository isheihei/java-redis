import org.isheihei.redis.core.struct.impl.BytesWrapper;
import org.isheihei.redis.core.struct.impl.RedisZSet;
import org.isheihei.redis.core.struct.impl.ZNode;
import org.junit.Test;

import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @ClassName: ZsetTest
 * @Description: 测试 TreeSet
 * @Date: 2022/6/19 19:19
 * @Author: isheihei
 */
public class ZSetTest {

    @Test
    public void zSetTest() {
        byte[] bytes1 = new byte[]{'a'};
        byte[] bytes2 = new byte[]{'b'};

        ZNode zNode1 = new ZNode(0.1, new BytesWrapper(bytes1));
        ZNode zNode2 = new ZNode(0.2, new BytesWrapper(bytes1));
        RedisZSet zSet = new RedisZSet();
        zSet.add(zNode1);
        zSet.contains(zNode2);
        // treeSet 按照 compare 结果来决定是否是相同元素 如果两个元素比较相等 则说明两个元素相等
        // compare相同 但是equals不相同则新增
        // compare不同 equals相同无操作 需要先删除再添加
        zSet.add(zNode2);
        zSet.remove(zNode2);
        zSet.add(zNode2);
        System.out.println("s");
    }

    @Test
    public void SubSetTest() {
        byte[] bytes1 = new byte[]{'a'};
        byte[] bytes2 = new byte[]{'b'};
        byte[] bytes3 = new byte[]{'c'};
        byte[] bytes4 = new byte[]{'d'};
        byte[] bytes5 = new byte[]{'e'};

        ZNode zNode1 = new ZNode(0.1, new BytesWrapper(bytes1));
        ZNode zNode2 = new ZNode(0.2, new BytesWrapper(bytes2));
        ZNode zNode3 = new ZNode(0.3, new BytesWrapper(bytes3));
        ZNode zNode4 = new ZNode(0.4, new BytesWrapper(bytes4));

        RedisZSet zSet = new RedisZSet();
        zSet.add(zNode1);
        zSet.add(zNode2);
        zSet.add(zNode3);
        zSet.add(zNode4);

        ZNode ceiling = zSet.ceiling(new ZNode(0.1, new BytesWrapper()));
        ZNode floor = zSet.floor(new ZNode(0.4, new BytesWrapper()));

        SortedSet<ZNode> zNodes = zSet.subSet(ceiling, true, floor, true);
        for (ZNode zNode : zNodes) {
            System.out.println(zNode);
        }
    }

    @Test
    public void test() {
        TreeSet<Integer> numbers = new TreeSet<>();
        numbers.add(2);
        numbers.add(5);
        numbers.add(4);
        numbers.add(6);
        System.out.println("TreeSet: " + numbers);

        // 使用 subSet() with default boolean value
        System.out.println("subSet()使用默认布尔值: " + numbers.subSet(0, 100));

        // 使用 subSet() 使用指定的布尔值
        System.out.println("subSet()使用指定的布尔值: " + numbers.subSet(0, false, 100, true));
    }

}
