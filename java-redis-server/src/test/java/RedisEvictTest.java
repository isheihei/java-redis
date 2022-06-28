import org.junit.Test;

/**
 * @ClassName: RedisEvictTest
 * @Description: 测试逐出策略 需要自行配置合适的虚拟机参数并打开 ServerCron 中注释掉的日志 参考jvm参数：-Xms20m -Xmx40m
 * @Date: 2022/6/15 21:27
 * @Author: isheihei
 */
public class RedisEvictTest {

    org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(RedisEvictTest.class);

    @Test
    public void evictTest() {
//        new RedisNetServer()
//                .ip("0.0.0.0")
//                .port(6379)
//                .channelOption(new SingleChannelSelectStrategy())
//                .dbNum(16)
//                .evictStrategy(Evict.ALL_KEYS_RANDOM_EVICT())
//                .rdb(false)
//                .aof(false)
//                .init()
//                .start();
//        Jedis jedis = new Jedis("127.0.0.1", 6379, ((int) TimeUnit.SECONDS.toMillis(100)));
//        LOGGER.info("最大内存：" + Runtime.getRuntime().maxMemory());
//        LOGGER.info("最大内存：" + Runtime.getRuntime().totalMemory());
//        int i = 1;
//        while (true) {
//            i++;
//            String key = String.valueOf(i);
//            String value = String.valueOf(i);
//            jedis.set(key,value);
//            jedis.expire(key, ((long) 100));
//        }
    }
}
