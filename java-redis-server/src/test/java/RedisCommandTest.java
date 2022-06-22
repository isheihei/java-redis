import org.isheihei.redis.server.RedisNetServer;
import org.isheihei.redis.server.channel.SingleChannelSelectStrategy;
import org.junit.Assert;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: RedisCommandTest
 * @Description: 命令单元测试
 * @Date: 2022/6/10 17:43
 * @Author: isheihei
 */
public class RedisCommandTest {
    private static final String OK = "OK";
    private static final String STRING_KEY = "string_key";
    private static final String LIST_KEY = "list_key";
    private static final String HASH_KEY = "hash_key";
    private static final String SET_KEY1 = "set_key1";
    private static final String SET_KEY2 = "set_key2";
    private static final String SET_KEY3 = "set_key3";
    private static final String Z_SET_KEY = "zset_key";

    @Test
    public void commandTest() {
        new RedisNetServer()
                .ip("0.0.0.0")
                .port(6389)
                .channelOption(new SingleChannelSelectStrategy())
                .dbNum(16)
                .aof(false)
                .rdb(false)
                .init()
                .start();
        Jedis jedis = new Jedis("127.0.0.1", 6389);

        // server
        jedis.flushAll();
        jedis.configGet("no_exit_name");    // config get no_exit_name
        Assert.assertEquals(OK, jedis.configSet("port", "9090")); // config set port 9090
        Assert.assertEquals("9090", jedis.configGet("port").get(1));   // config get port

        jedis.clientSetname("test_client_name");    // client setname test_client_name
        Assert.assertEquals("test_client_name", jedis.clientGetname()); // client getname


        //  connection
        jedis.flushAll();
        jedis.configSet("requirepass", "password"); // config set requirepass password
        Assert.assertEquals(OK, jedis.auth("password"));    // auth password
        Assert.assertEquals("echo", jedis.echo("echo"));    //  echo echo
        Assert.assertEquals("PONG", jedis.ping());  // ping
        Assert.assertEquals(OK, jedis.select(1));   //  select 1
        Assert.assertEquals(OK, jedis.select(0));   //  select 0


        //  string
        jedis.flushAll();
        Assert.assertNull(jedis.get(STRING_KEY)); // get string_key
        Assert.assertEquals(OK, jedis.set(STRING_KEY, "string_value"));   // set string_key string_value
        Assert.assertEquals(OK, jedis.mset("s1", "v1", "s2", "v2"));    // mset s1 v1 s2 v2
        Assert.assertEquals("v1", jedis.mget("s1", "s2").get(0));   // mget s1 s2
        Assert.assertEquals("v2", jedis.mget("s1", "s2").get(1));   // mget s1 s2
        Assert.assertEquals(2, jedis.append("s3", "v3").longValue());
        Assert.assertEquals(4, jedis.append("s3", "v3").longValue());
        Assert.assertEquals("v3v3", jedis.get("s3"));


        //  list
        jedis.flushAll();
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
        jedis.flushAll();
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


        //  set
        jedis.flushAll();
        jedis.sadd(SET_KEY1, "v1", "v2");
        jedis.sadd(SET_KEY2, "v1", "v3");
        Assert.assertEquals(2, jedis.scard(SET_KEY1).longValue());
        Assert.assertEquals(2, jedis.smembers(SET_KEY1).size());

        Assert.assertTrue(jedis.sdiff(SET_KEY1, SET_KEY2).contains("v2"));
        jedis.sdiffstore(SET_KEY3, SET_KEY1, SET_KEY2);
        Assert.assertTrue(jedis.sismember(SET_KEY3, "v2"));

        Assert.assertTrue(jedis.sinter(SET_KEY1, SET_KEY2).contains("v1"));
        jedis.sinterstore(SET_KEY3, SET_KEY1, SET_KEY2);
        Assert.assertTrue(jedis.sismember(SET_KEY3, "v1"));

        Assert.assertTrue(jedis.sunion(SET_KEY1, SET_KEY2).contains("v1"));
        Assert.assertTrue(jedis.sunion(SET_KEY1, SET_KEY2).contains("v2"));
        Assert.assertTrue(jedis.sunion(SET_KEY1, SET_KEY2).contains("v3"));
        jedis.sunionstore(SET_KEY3, SET_KEY1, SET_KEY2);
        Assert.assertEquals(3, jedis.smembers(SET_KEY3).size());

        jedis.srem(SET_KEY1, "v1");
        Assert.assertFalse(jedis.sismember(SET_KEY1, "v1"));


        // zset
        jedis.flushAll();
        Map<String, Double> members = new HashMap<String, Double>(){{put("a", 1.0);put("b", 2.0);put("c", 3.0);}};
        jedis.zadd(Z_SET_KEY, 1.0, "a");
        jedis.zadd(Z_SET_KEY, 2.0, "b");
        jedis.zadd(Z_SET_KEY, 3.0, "c");
        Assert.assertEquals(3, jedis.zcard(Z_SET_KEY).longValue());
        Assert.assertEquals(2, jedis.zcount(Z_SET_KEY, 1.0, 3.0).longValue());
        Assert.assertEquals(members.keySet(), jedis.zrange(Z_SET_KEY, 0, -1));
        Assert.assertEquals(members.keySet(), jedis.zrangeByScore(Z_SET_KEY, 0, 4));
        Assert.assertEquals(1, jedis.zrank(Z_SET_KEY, "b").longValue());
        Assert.assertEquals(Double.valueOf(1.0), jedis.zscore(Z_SET_KEY, "a"));
        Assert.assertEquals(1, jedis.zrem(Z_SET_KEY, "a").longValue());
        Assert.assertEquals(2, jedis.zcard(Z_SET_KEY).longValue());

        // transaction
        jedis.flushAll();
        jedis.watch(STRING_KEY);
        jedis.set(STRING_KEY, "value");
        Transaction transaction1 = jedis.multi();
        transaction1.get(STRING_KEY);
        Assert.assertNull(transaction1.exec().get(0));
        jedis.watch(STRING_KEY);
        Transaction transaction2 = jedis.multi();
        transaction2.get(STRING_KEY);
        Assert.assertEquals("value", transaction2.exec().get(0));
        Transaction transaction3 = jedis.multi();
        transaction3.get(STRING_KEY);
        Assert.assertEquals(OK, transaction3.discard());
    }
}
