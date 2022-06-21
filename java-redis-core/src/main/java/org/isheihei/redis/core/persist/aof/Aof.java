package org.isheihei.redis.core.persist.aof;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.PooledByteBufAllocator;
import org.isheihei.redis.common.util.ConfigUtil;
import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.client.RedisNormalClient;
import org.isheihei.redis.core.command.Command;
import org.isheihei.redis.core.command.CommandFactory;
import org.isheihei.redis.core.command.AbstractWriteCommand;
import org.isheihei.redis.core.db.RedisDB;
import org.isheihei.redis.core.persist.Persist;
import org.isheihei.redis.core.resp.Resp;
import org.isheihei.redis.core.resp.impl.RespArray;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

/**
 * @ClassName: Aof
 * @Description: Aof持久化
 * @Date: 2022/5/31 15:09
 * @Author: isheihei
 */
public class Aof implements Persist {
    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(Aof.class);

    private static final String suffix = ConfigUtil.getAppendFileName();

    private String fileName = ConfigUtil.getAofPath();

    private Deque<Resp> bufferQueue = new LinkedList<>();
    private ByteBuf bufferPolled = PooledByteBufAllocator.DEFAULT.directBuffer(8888);

    private RedisClient mockClient;

    public Aof(List<RedisDB> dbs) {
        File file = new File(this.fileName + suffix);
        if (!file.isDirectory()) {
            File parentFile = file.getParentFile();
            if (null != parentFile && !parentFile.exists()) {
                parentFile.mkdirs();
            }
        }
        mockClient = new RedisNormalClient("fake_address", -1, dbs);
    }
    public void put(Resp resp) {
        bufferQueue.offer(resp);
    }

    @Override
    public void save() {
        if (bufferQueue.isEmpty()) {
            LOGGER.info("aof缓冲队列为空");
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
//                LOGGER.error("len " + len);
//                LOGGER.error("mappedByteBuffer.position()" + mappedByteBuffer.position());
//                LOGGER.error("mappedByteBuffer.capacity()" + mappedByteBuffer.capacity());
//                    LOGGER.error("respLen" + respLen);
//                    LOGGER.error("bufferPolled 可读 + " + bufferPolled.readableBytes());
//                    LOGGER.error("putIndex" + putIndex);
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
                AbstractWriteCommand writeCommand = (AbstractWriteCommand) command;
                writeCommand.handleLoadAof(this.mockClient);
            }
            LOGGER.info("加载aof文件完成");
        } catch (Exception e) {
            bufferPolled.release();
            LOGGER.error("加载aof文件失败");
            LOGGER.error("aof Exception ", e);
        }
    }
}
