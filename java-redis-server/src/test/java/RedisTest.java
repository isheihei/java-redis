import org.isheihei.redis.server.RedisNetServer;
import org.junit.Assert;
import org.junit.Test;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName: RedisTest
 * @Description: 命令单元测试
 * @Date: 2022/6/10 17:43
 * @Author: isheihei
 */
public class RedisTest {
    private static String OK = "OK";

    private static String STRING_KEY = "string_key";

    private static String LIST_KEY = "list_key";

    private static String HASH_KEY = "hash_key";

    @Test
    public void commandTest() {
        new RedisNetServer().start();
        Jedis jedis = new Jedis("127.0.0.1", 6379);

        // server
        jedis.configGet("no_exit_name");    // config get no_exit_name
        Assert.assertEquals(OK, jedis.configSet("port", "9090")); // config set port 9090
        Assert.assertEquals("9090", jedis.configGet("port").get(1));   // config get port

        jedis.clientSetname("test_client_name");    // client setname test_client_name
        Assert.assertEquals("test_client_name", jedis.clientGetname()); // client getname

        //  connection
        jedis.configSet("requirepass", "password"); // config set requirepass password
        Assert.assertEquals(OK, jedis.auth("password"));    // auth password
        Assert.assertEquals("echo", jedis.echo("echo"));    //  echo echo
        Assert.assertEquals("PONG", jedis.ping());  // ping
        Assert.assertEquals(OK, jedis.select(1));   //  select 1
        Assert.assertEquals(OK, jedis.select(0));   //  select 0

        //  string
        Assert.assertNull(jedis.get(STRING_KEY)); // get string_key
        Assert.assertEquals(OK, jedis.set(STRING_KEY, "string_value"));   // set string_key string_value
        Assert.assertEquals(OK, jedis.mset("s1", "v1", "s2", "v2"));    // mset s1 v1 s2 v2
        Assert.assertEquals("v1", jedis.mget("s1", "s2").get(0));   // mget s1 s2
        Assert.assertEquals("v2", jedis.mget("s1", "s2").get(1));   // mget s1 s2
        Assert.assertEquals(Long.valueOf(2), jedis.append("s3", "v3"));
        Assert.assertEquals(Long.valueOf(4), jedis.append("s3", "v3"));
        Assert.assertEquals("v3v3", jedis.get("s3"));

        //  list
        Assert.assertEquals(0, jedis.lrange(LIST_KEY, 0, 1).size());  // lange list_key 0 1
        jedis.lpush(LIST_KEY, "v3");  // lpush list_key v3
        jedis.rpush(LIST_KEY, "v1");  // lpush list_key v1
        jedis.rpush(LIST_KEY, "v1");  // lpush list_key v1
        jedis.rpush(LIST_KEY, "v5");  // lpush list_key v5
        jedis.rpush(LIST_KEY, "v4");  // lpush list_key v4
        jedis.rpush(LIST_KEY, "v2");  // lpush list_key v2    // 当前列表：v3, v1, v1, v5, v4, v2
        Assert.assertEquals("v3", jedis.lrange(LIST_KEY, 0, 1).get(0)); // lange list_key 0 1
        jedis.lpop(LIST_KEY); //  lpop list_key
        jedis.rpop(LIST_KEY); // rpop list_key    //  当前列表：v1, v1, v5, v4
        Assert.assertEquals("v1", jedis.lrange(LIST_KEY, 0, 1).get(0)); // lange list_key 0 1

        jedis.lrem(LIST_KEY, 2, "v1"); // lrem list_string 2 v1   //  当前列表：v5, v4
        Assert.assertEquals("v5", jedis.lrange(LIST_KEY, 0, 1).get(0)); // lange list_key 0 1

        jedis.lset(LIST_KEY, 0, "v6");// lset list_key 0 v6   //  当前列表：v6, v4
        Assert.assertEquals("v6", jedis.lrange(LIST_KEY, 0, 1).get(0)); // lange list_key 0 1

        // hash
        Assert.assertEquals(0, jedis.hkeys(HASH_KEY).size());   //  hkeys hash_key
        Assert.assertEquals(0, jedis.hvals(HASH_KEY).size());   //   hvals hash_key
        Assert.assertEquals(0, jedis.hgetAll(HASH_KEY).size()); //  hgetall hash_key
        Assert.assertFalse(jedis.hexists(HASH_KEY, "field1"));  // hexists hash_key field1
        Assert.assertNull(jedis.hget(HASH_KEY, "field1"));  //  hget hash_key
        Assert.assertNull(jedis.hmget(HASH_KEY, "field1")); //  hmget hash_key
        jedis.hset(HASH_KEY, "field1", "v1");   //  hset hash_key field1 v1 // 当前hash： [filed1, v1]
        Assert.assertTrue(jedis.hkeys(HASH_KEY).contains("field1"));    // hkeys hash_key
        Assert.assertEquals("v1", jedis.hvals(HASH_KEY).get(0));    // hvals hash_key
        Assert.assertEquals("v1", jedis.hgetAll(HASH_KEY).get("field1"));   //  hexists hash_key
        Assert.assertTrue(jedis.hexists(HASH_KEY, "field1"));   // hexists hash_key field1
        Assert.assertEquals("v1", jedis.hget(HASH_KEY, "field1"));  //  hget hash_key field1
        jedis.hmset(HASH_KEY, new HashMap<String, String>(){{
            put("field2", "v2");
            put("field3", "v3");
        }});    //  hmset hash_key field2 v2 field3 v3  // 当前hash：[field1, v1], [field2, v2], [field3, v3]

        //  hdel hash_key field1 filed4 不存在的 field 忽略
        Assert.assertEquals(Long.valueOf(1), jedis.hdel(HASH_KEY, "field1" ,"field4"));
        //  hmget hash_key field2 field3
        Assert.assertTrue(jedis.hmget(HASH_KEY, "field2", "field3").contains("v2"));
        Assert.assertTrue(jedis.hmget(HASH_KEY, "field2", "field3").contains("v3"));
        Assert.assertEquals(2, jedis.hmget(HASH_KEY, "field2", "field3").size());
        try {
            Thread.sleep(TimeUnit.SECONDS.toMillis(2));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
