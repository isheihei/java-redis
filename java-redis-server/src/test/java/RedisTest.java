import org.isheihei.redis.server.RedisNetServer;
import org.junit.Assert;
import org.junit.Test;
import redis.clients.jedis.Jedis;

/**
 * @ClassName: RedisTest
 * @Description: 命令单元测试
 * @Date: 2022/6/10 17:43
 * @Author: isheihei
 */
public class RedisTest {
    private static String OK = "OK";
    @Test
    public void commandTest() {
        new RedisNetServer().start();
        Jedis jedis = new Jedis("127.0.0.1", 6379);

        // 通用命令
        jedis.configGet("no_exit_name");    // config get no_exit_name
        Assert.assertEquals(OK, jedis.configSet("port", "9090")); // config set port 9090
        Assert.assertEquals("9090", jedis.configGet("port").get(1));   // config get port

        jedis.clientSetname("test_client_name");    // client setname test_client_name
        Assert.assertEquals("test_client_name", jedis.clientGetname()); // client getname

        jedis.configSet("requirepass", "password"); // config set requirepass password
        Assert.assertEquals(OK, jedis.auth("password"));    // auth password

        //  string
        Assert.assertNull(jedis.get("stirng_key")); // get string_key
        Assert.assertEquals(OK, jedis.set("string_key", "string_value"));   // set string_key string_value
        Assert.assertEquals(OK, jedis.mset("s1", "v1", "s2", "v2"));    // mset s1 v1 s2 v2
        Assert.assertEquals("v1", jedis.mget("s1", "s2").get(0));   // mget s1 s2
        Assert.assertEquals("v2", jedis.mget("s1", "s2").get(1));   // mget s1 s2

        //  list
        Assert.assertEquals(0, jedis.lrange("list_key", 0, 1).size());  // lange list_key 0 1
        jedis.lpush("list_key", "v3");  // lpush list_key v3
        jedis.rpush("list_key", "v1");  // lpush list_key v1
        jedis.rpush("list_key", "v1");  // lpush list_key v1
        jedis.rpush("list_key", "v5");  // lpush list_key v5
        jedis.rpush("list_key", "v4");  // lpush list_key v4
        jedis.rpush("list_key", "v2");  // lpush list_key v2    // 当前列表：v3, v1, v1, v5, v4, v2
        Assert.assertEquals("v3", jedis.lrange("list_key", 0, 1).get(0)); // lange list_key 0 1
        jedis.lpop("list_key"); //  lpop list_key
        jedis.rpop("list_key"); // rpop list_key    //  当前列表：v1, v1, v5, v4
        Assert.assertEquals("v1", jedis.lrange("list_key", 0, 1).get(0)); // lange list_key 0 1

        jedis.lrem("list_key", 2, "v1"); // lrem list_string 2 v1   //  当前列表：v5, v4
        Assert.assertEquals("v5", jedis.lrange("list_key", 0, 1).get(0)); // lange list_key 0 1

        jedis.lset("list_key", 0, "v6");// lset list_key 0 v6   //  当前列表：v6, v4
        Assert.assertEquals("v6", jedis.lrange("list_key", 0, 1).get(0)); // lange list_key 0 1


    }
}
