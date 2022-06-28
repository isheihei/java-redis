---
title: "Redis设计与实现06-持久化"
description: 
date: 2022-06-26T16:40:26+08:00
categories: ["数据库"]
tags: ["redis","java-redis"]
image: log.jpg
hidden: false
draft: true
weight: 106
---

持久化分为 RDB 和 AOF 两种。相关实现位于`org.isheihei.redis.core.persist`

## RDB

### RDB 文件的创建与载入

**RDB 文件创建的条件有两种**

1. 通过执行 `SVAE` 和 `BGSAVE` 命令手动保存。其中 `SAVE` 会阻塞当前所有命令；而 `BGSAVE` 是在子进程中执行，服务器在期间仍然可以处理命令。
2. 自动间隔性保存

**自动间隔性保存**

在 RDB 中有一些保存条件，他们是一个二元组形式的集合，如果满足其中任何一个条件，就会执行 RDB 文件保存

```java
/**
 * 保存条件
 * 900 1 ： 900s内对数据库进行了至少1次修改
 */
private Map<Long, Long> saveParams = new HashMap<>();
```

除了 `saveParams` 数组之外，还会维护 `dirty`  计数器，以及一个 `lastSave` 属性

- `dirty` 计数器记录距离上一次 RDB 持久化后，数据库的修改次数
- `lastSave` 是一个 UNIX 时间戳，记录了服务器上一次成功执行 RDB 持久化的时间

通过周期事件 `ServerCron` 调用函数判断是否满足间隔保存条件，如果满足条件，则执行持久化操作：

 ```java
 public boolean satisfySaveParams() {
     long dirtyCount = dbs.stream().mapToLong(RedisDB::getDirty).sum();
     long interVal = TimeUnit.MICROSECONDS.toSeconds(System.currentTimeMillis() - lastSave);
     boolean anyMatch = saveParams.entrySet().stream()
         .filter(param -> param.getValue() <= dirtyCount)
         .anyMatch(param -> param.getKey() > interVal);
     return anyMatch;
 }
 ```

**RDB 文件的载入**

会在服务器初始化时进行，如果 RDB 和 AOF 持久化同时开启，会优先使用 AOF 文件进行加载

```java
//  rdb 和 aof 同时开启优先使用aof文件加载
if (dataBase && appendOnlyFile) {
    redisSingleEventExecutor.submit(() -> aof.load());
    serverCron.aof(aof);
    serverCron.rdb(rdb);
} else if (dataBase) {
    redisSingleEventExecutor.submit(() -> rdb.load());
    serverCron.rdb(rdb);
} else if (appendOnlyFile){
    redisSingleEventExecutor.submit(() -> aof.load());
    serverCron.aof(aof);
}
```

### RDB 文件格式

 Redis 的 RDB 持久化文件是二进制文件，这里同样参考 Redis 的格式，可能会有些许差异。

 ```java
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
 ```

文件读写操作使用的是 `MappedByteBuffer`

相对于java io操作中通常采用BufferedReader，BufferedInputStream等带缓冲的IO类处理大文件，java nio中引入了一种基于MappedByteBuffer操作大文件的方式，把文件映射到虚拟内存，其读写性能极高。

```java
    @Override
    public synchronized void save() {
        LOGGER.info("开始进行rdb持久化...");
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
                    mappedByteBuffer.put(value.getCode()); // 1
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
            lastSave = System.currentTimeMillis();
            resetDbDirty();
            LOGGER.info("rdb持久化完成");
        } catch (FileNotFoundException e) {
            LOGGER.error("未找到.rdb文件");
            throw new RuntimeException(e);
        } catch (IOException e) {
            LOGGER.error("rdb持久化出错");
            throw new RuntimeException(e);
        }
    }

    @Override
    public void load() {
        try {
            long readIndex = 0L;
            FileChannel channel = new RandomAccessFile(fileName + suffix, "rw").getChannel();
            if (channel.size() == 0) {
                LOGGER.info("rdb文件为空");
                return;
            }
            MappedByteBuffer mappedByteBuffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, 9);
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
                mappedByteBuffer = channel.map(FileChannel.MapMode.READ_ONLY, readIndex, 1);
                if (SELECTDB != mappedByteBuffer.get()) {
                    LOGGER.info("数据库已经加载完成");
                    break;
                }
                readIndex += 1;
                mappedByteBuffer = channel.map(FileChannel.MapMode.READ_ONLY, readIndex, 4);
                int dbIndex = mappedByteBuffer.getInt();
                RedisDB db = dbs.get(dbIndex);
                readIndex += 4;
                while (EOF != channel.map(FileChannel.MapMode.READ_ONLY, readIndex, 1).get(0)) {
                    mappedByteBuffer = channel.map(FileChannel.MapMode.READ_ONLY, readIndex, 14);
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
                    mappedByteBuffer = channel.map(FileChannel.MapMode.READ_ONLY, readIndex, keyLen);
                    bufferPolled.writeBytes(mappedByteBuffer);
                    readIndex += keyLen;
                    byte[] keyBytes = ByteBufUtil.getBytes(bufferPolled);
                    bufferPolled.clear();
                    BytesWrapper key = new BytesWrapper(keyBytes);
                    db.put(key, redisObject);
                    db.expire(key, ttl);    // ttl 为0即不设置过期
                    mappedByteBuffer = channel.map(FileChannel.MapMode.READ_ONLY, readIndex, 4);
                    int valueLen = mappedByteBuffer.getInt();
                    readIndex += 4;
                    mappedByteBuffer = channel.map(FileChannel.MapMode.READ_ONLY, readIndex, valueLen);
                    bufferPolled.writeBytes(mappedByteBuffer);
                    redisObject.loadRdb(bufferPolled);
                    bufferPolled.clear();
                    readIndex += valueLen;
                }
                if (EOF != channel.map(FileChannel.MapMode.READ_ONLY, readIndex, 1).get(0)) {
                    channel.close();
                    LOGGER.info("rdb数据全部加载完成");
                    return;
                }
                lastSave = System.currentTimeMillis();
                deleteFile();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            LOGGER.error("rdb文件加载失败");
        } catch (IOException e) {
            LOGGER.error("rdb文件加载失败");
            e.printStackTrace();
        }
    }

    public void bgSave() {
        new Thread(this::save).start();
    }
```

## AOF 持久化

AOF 持久化主要通过将修改命令追加到持久化文件中。在[Redis设计与实现03-命令 (isheihei.cn)](http://isheihei.cn/posts/数据库/redis设计与实现03-命令/)中介绍了写命令的命令执行过程。即：先执行数据库写命令，如果 AOF 开启，则将命令写入 AOF 写队列中。

AOF 写队列持久化到磁盘的过程还是在周期事件 `ServerCron` 中被调用。周期是每 100ms 一次。目前只实现了这一种同步方式，所以可能会有极端情况导致数据丢失的情况。

文件读写操作依然使用的是 `MappedByteBuffer`

```java
@Override
public void save() {
    if (bufferQueue.isEmpty()) {
        return;
    }
    try (FileChannel channel = new RandomAccessFile(fileName + suffix, "rw").getChannel()){
        LOGGER.info("开始rdb持久化...");
        do {
            bufferPolled.clear();
            long len = channel.size();
            Resp resp = bufferQueue.peek();
            Resp.write(resp, bufferPolled);
            int respLen = bufferPolled.readableBytes();
            MappedByteBuffer mappedByteBuffer = channel.map(FileChannel.MapMode.READ_WRITE, len, respLen);
            mappedByteBuffer.put(ByteBufUtil.getBytes(bufferPolled));
            bufferQueue.poll();
        } while (!bufferQueue.isEmpty());
        LOGGER.info("rdb持久化完成");
    } catch (Exception e) {
        bufferPolled.release();
        LOGGER.error("aof Exception ", e);
    }
}

@Override
public void load() {
    try (FileChannel channel = new RandomAccessFile(fileName + suffix, "rw").getChannel()) {
        long len = channel.size();
        if (len == 0) {
            LOGGER.info("aof文件为空");
            return;
        }
        MappedByteBuffer mappedByteBuffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, len);
        bufferPolled.writeBytes(mappedByteBuffer);
        while (bufferPolled.readableBytes() > 0) {
            Resp resp = Resp.decode(bufferPolled);
            Command command = CommandFactory.from((RespArray) resp);
            if (command != null) {
                AbstractWriteCommand writeCommand = (AbstractWriteCommand) command;
                writeCommand.handleLoadAof(this.mockClient);
            }
        }
        LOGGER.info("加载aof文件完成");
    } catch (Exception e) {
        bufferPolled.release();
        LOGGER.error("加载aof文件失败");
        LOGGER.error("aof Exception ", e);
    }
}
```

