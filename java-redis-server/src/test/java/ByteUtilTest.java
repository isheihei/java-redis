import org.isheihei.redis.common.util.ByteUtil;
import org.junit.Assert;
import org.junit.Test;

/**
 * @ClassName: ByteUtilTest
 * @Description: TODO
 * @Date: 2022/6/16 21:16
 * @Author: isheihei
 */
public class ByteUtilTest {
    private static final int AN_INT = 1;
    private static final long AN_LONG = 0L;


    @Test
    public void bytesFromToIntTest() {
        byte[] bytes = ByteUtil.intToBytes(AN_INT);
        Assert.assertEquals(4, bytes.length);
        Assert.assertEquals(AN_INT, ByteUtil.bytesToInt(bytes));
        String s = String.valueOf(1);
        char[] chars = s.toCharArray();
    }

    @Test
    public void bytesFromToLongTest() {
        byte[] bytes = ByteUtil.longToBytes(AN_LONG);
        Assert.assertEquals(8, bytes.length);
        Assert.assertEquals(AN_LONG, ByteUtil.bytesToLong(bytes));
    }

}
