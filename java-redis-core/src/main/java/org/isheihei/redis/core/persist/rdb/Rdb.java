package org.isheihei.redis.core.persist.rdb;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import org.isheihei.redis.common.util.ConfigUtil;
import org.isheihei.redis.core.db.RedisDB;
import org.isheihei.redis.core.obj.RedisObject;
import org.isheihei.redis.core.obj.impl.RedisListObject;
import org.isheihei.redis.core.obj.impl.RedisMapObject;
import org.isheihei.redis.core.obj.impl.RedisSetObject;
import org.isheihei.redis.core.obj.impl.RedisStringObject;
import org.isheihei.redis.core.obj.impl.RedisZSetObject;
import org.isheihei.redis.core.persist.Persist;
import org.isheihei.redis.core.struct.impl.BytesWrapper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: Rdb
 * @Description: TODO
 * @Date: 2022/6/16 15:45
 * @Author: isheihei
 */
public class Rdb implements Persist {
    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(Rdb.class);

    private static final String suffix = ConfigUtil.getDatabasefilename();

    private String fileName = ConfigUtil.getRdbpath();

    private List<RedisDB> dbs;


    private ByteBuf bufferPolled = ByteBufAllocator.DEFAULT.heapBuffer(8888);

    private static final byte[] REDIS = new byte[]{(byte)'R', (byte)'E', (byte)'D', (byte)'I', (byte)'S'};

    private static final int DB_VERSION = 0001;

    private static final byte SELECTDB = (byte) 0xFE;

    private static final byte EXPIRETIME_MS = (byte) 0xFC;

    private static final byte EOF = (byte) 0xFF;


    public Rdb(List<RedisDB> dbs) {
        createFile();
        this.dbs = dbs;
    }

    public Rdb() {
    }

    private void createFile() {
        File file = new File(this.fileName + suffix);
        if (!file.isDirectory()) {
            File parentFile = file.getParentFile();
            if (null != parentFile && !parentFile.exists()) {
                parentFile.mkdirs();
            }
        }
    }

    private void deleteFile() {
        File file = new File(this.fileName + suffix);
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * RDB文件结构
     * REDIS | db_version | databases | EOF | check_sum
     *  5B   |   4B       |           | 1B  |  8B
     *
     * databases部分
     *  SELECTDB | db_number | key_value_pairs
     *    1B     |    4B     |
     *
     * key_value_pairs部分
     *  EXPIRETIME_MS | ms | TYPE  |           key              |             value
     *      1B        | 8B |  1B   | key_len(4B) + key(ken_len) | value_len(4B) + value(value_len)
     *
     *  TYPE:
     *  string 0
     *  map    1
     *  list   2
     *  set    3
     *  zset   4
     */
    @Override
    public void save() {
        try {
            // 每次持久化需要创建新的文件
            deleteFile();
            createFile();

            long writeIndex = 0L;
            FileChannel channel = new RandomAccessFile(fileName + suffix, "rw").getChannel();
            MappedByteBuffer mappedByteBuffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, 9);
            mappedByteBuffer.put(REDIS); //REDIS 5
            mappedByteBuffer.putInt(DB_VERSION);   // 0001 4
            writeIndex += 9;

            for (int dbIndex = 0; dbIndex < dbs.size(); dbIndex ++) {
                RedisDB db = dbs.get(dbIndex);
                if (db.size() == 0) {
                    continue;
                }
                mappedByteBuffer = channel.map(FileChannel.MapMode.READ_WRITE, writeIndex, 5);
                mappedByteBuffer.put(SELECTDB); //  X 1
                mappedByteBuffer.putInt(dbIndex); // NULL * 4
                writeIndex += 5;

                Map<BytesWrapper, RedisObject> dict = db.dict();
                Map<BytesWrapper, Long> expires = db.expires();
                Iterator<Map.Entry<BytesWrapper, RedisObject>> entryIterator = dict.entrySet().iterator();
                while (entryIterator.hasNext()) {
                    Map.Entry<BytesWrapper, RedisObject> next = entryIterator.next();
                    BytesWrapper nextKey = next.getKey();
                    RedisObject value = next.getValue();
                    mappedByteBuffer = channel.map(FileChannel.MapMode.READ_WRITE, writeIndex, 10);
                    mappedByteBuffer.put(EXPIRETIME_MS);    // V
                    if (db.expires().containsKey(nextKey)) {
                        mappedByteBuffer.putLong(db.getTtl(nextKey));
                    } else {
                        mappedByteBuffer.putLong(0L); // NULL * 4
                    }
                    mappedByteBuffer.put(value.getType()); // 1
                    writeIndex += 10;

                    int nextLen = nextKey.length();
                    mappedByteBuffer = channel.map(FileChannel.MapMode.READ_WRITE, writeIndex, 4 + nextLen);
                    mappedByteBuffer.putInt(nextLen);
                    mappedByteBuffer.put(next.getKey().getByteArray());
                    writeIndex += (nextLen + 4);

                    byte[] objectBytes = next.getValue().objectToBytes();
                    int objectLen = objectBytes.length;
                    mappedByteBuffer = channel.map(FileChannel.MapMode.READ_WRITE, writeIndex, objectLen + 4);
                    mappedByteBuffer.putInt(objectLen);
                    mappedByteBuffer.put(objectBytes);
                    writeIndex += (objectLen + 4);
                }
                mappedByteBuffer = channel.map(FileChannel.MapMode.READ_WRITE, writeIndex, 1);
                mappedByteBuffer.put(EOF);
                writeIndex += 1;
            }
            mappedByteBuffer = channel.map(FileChannel.MapMode.READ_WRITE, writeIndex, 1);
            mappedByteBuffer.put(EOF);
            writeIndex += 1;
            channel.close();
        } catch (FileNotFoundException e) {
            LOGGER.error("未找到.rdb文件");
            throw new RuntimeException(e);
        } catch (IOException e) {
            LOGGER.error("rdb持久化出错");
            throw new RuntimeException(e);
        }
    }


    /**
     * RDB文件结构
     * REDIS | db_version | databases | EOF | check_sum
     *  5B   |   4B       |           | 1B  |  8B
     *
     * databases部分
     *  SELECTDB | db_number | key_value_pairs
     *    1B     |    4B     |
     *
     * key_value_pairs部分
     *  EXPIRETIME_MS | ms | TYPE  | key | value
     *      1B        | 8B |  1B   |
     *
     *  TYPE:
     *  string 0
     *  map    1
     *  list   2
     *  set    3
     *  zset   4
     */
    @Override
    public void load() {
        try {
            long readIndex = 0L;
            FileChannel channel = new RandomAccessFile(fileName + suffix, "rw").getChannel();
            if (channel.size() == 0) {
                LOGGER.info("rdb文件为空");
                return;
            }
            MappedByteBuffer mappedByteBuffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, 9);
            for (int i = 0; i < 5; i++) {
                if (REDIS[i] != mappedByteBuffer.get()) {
                    LOGGER.error("rdb文件魔数错误");
                    throw new IOException();
                }
            }
            if (DB_VERSION != mappedByteBuffer.getInt()) {
                LOGGER.error("rdb文件版本错误");
                throw new IOException();
            }
            readIndex += 9;



            while (true){
                mappedByteBuffer = channel.map(FileChannel.MapMode.READ_WRITE, readIndex, 1);
                if (SELECTDB != mappedByteBuffer.get()) {
                    LOGGER.info("数据库已经加载完成");
                    break;
                }
                readIndex += 1;


                mappedByteBuffer = channel.map(FileChannel.MapMode.READ_WRITE, readIndex, 4);
                int dbIndex = mappedByteBuffer.getInt();
                RedisDB db = dbs.get(dbIndex);
                readIndex += 4;

                while (EOF != channel.map(FileChannel.MapMode.READ_WRITE, readIndex, 1).get(0)) {
                    mappedByteBuffer = channel.map(FileChannel.MapMode.READ_WRITE, readIndex, 14);
                    if (EXPIRETIME_MS != mappedByteBuffer.get()) {
                        LOGGER.error("rdb文件格式错误");
                        throw new IOException();
                    }
                    long ttl = mappedByteBuffer.getLong();
                    byte type = mappedByteBuffer.get();
                    RedisObject redisObject;
                    if (type == (byte) 0) {
                        redisObject = new RedisStringObject();
                    } else if (type == (byte) 1) {
                        redisObject = new RedisMapObject();
                    } else if (type == (byte) 2) {
                        redisObject = new RedisListObject();
                    } else if (type == (byte) 3) {
                        redisObject = new RedisSetObject();
                    } else{
                        redisObject = new RedisZSetObject();
                    }
                    int keyLen = mappedByteBuffer.getInt();
                    readIndex += 14;

                    mappedByteBuffer = channel.map(FileChannel.MapMode.READ_WRITE, readIndex, keyLen);
                    bufferPolled.writeBytes(mappedByteBuffer);
                    readIndex += keyLen;
                    byte[] keyBytes = ByteBufUtil.getBytes(bufferPolled);
                    bufferPolled.clear();

                    BytesWrapper key = new BytesWrapper(keyBytes);
                    db.put(key, redisObject);
                    db.expire(key, ttl);    // ttl 为0即不设置过期


                    mappedByteBuffer = channel.map(FileChannel.MapMode.READ_WRITE, readIndex, 4);
                    int valueLen = mappedByteBuffer.getInt();
                    readIndex += 4;

                    mappedByteBuffer = channel.map(FileChannel.MapMode.READ_WRITE, readIndex, valueLen);
                    bufferPolled.writeBytes(mappedByteBuffer);
                    redisObject.loadRdb(bufferPolled);
                    bufferPolled.clear();
                    readIndex += valueLen;
                }
                if (EOF != channel.map(FileChannel.MapMode.READ_WRITE, readIndex, 1).get(0)) {
                    channel.close();
                    LOGGER.info("rdb数据全部加载完成");
                    return;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            LOGGER.error("rdb文件加载失败");
        } catch (IOException e) {
            LOGGER.error("rdb文件加载失败");
            e.printStackTrace();
        }
    }


}
