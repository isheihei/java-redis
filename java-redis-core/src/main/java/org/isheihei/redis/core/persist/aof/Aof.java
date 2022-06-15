package org.isheihei.redis.core.persist.aof;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import org.isheihei.redis.common.util.ConfigUtil;
import org.isheihei.redis.core.client.RedisClient;
import org.isheihei.redis.core.client.RedisNormalClient;
import org.isheihei.redis.core.command.Command;
import org.isheihei.redis.core.command.CommandFactory;
import org.isheihei.redis.core.command.WriteCommand;
import org.isheihei.redis.core.db.RedisDB;
import org.isheihei.redis.core.resp.Resp;
import org.isheihei.redis.core.resp.impl.RespArray;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @ClassName: Aof
 * @Description: Aof持久化
 * @Date: 2022/5/31 15:09
 * @Author: isheihei
 */
public class Aof {
    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(Aof.class);

    private static final String suffix = ConfigUtil.getAppendfilename();

    private String fileName = ConfigUtil.getAofpath();

    private LinkedBlockingQueue<Resp> bufferQueue = new LinkedBlockingQueue<>();
    private ByteBuf bufferPolled = ByteBufAllocator.DEFAULT.directBuffer(8888);

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

    public void save() {
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(fileName + suffix, "rw");
            FileChannel channel = randomAccessFile.getChannel();
            long len = channel.size();
            int putIndex = (int) len;
            do {
                len = channel.size();
                Resp resp = bufferQueue.peek();
                if (resp == null) {
                    randomAccessFile.close();
                    return;
                }
                Resp.write(resp, bufferPolled);
                int respLen = bufferPolled.readableBytes();
                MappedByteBuffer mappedByteBuffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, len + respLen);
//                LOGGER.error("len " + len);
//                LOGGER.error("mappedByteBuffer.position()" + mappedByteBuffer.position());
//                LOGGER.error("mappedByteBuffer.capacity()" + mappedByteBuffer.capacity());
                while (respLen > 0) {
                    respLen--;
//                    LOGGER.error("respLen" + respLen);
//                    LOGGER.error("bufferPolled 可读 + " + bufferPolled.readableBytes());
//                    LOGGER.error("putIndex" + putIndex);
                    mappedByteBuffer.put(putIndex++, bufferPolled.readByte());
                }
                mappedByteBuffer.force();
                clean(mappedByteBuffer);
                bufferQueue.poll();
                bufferPolled.clear();
            } while (true);

        } catch (IOException e) {
            LOGGER.error("aof IOException ", e);
        } catch (Exception e) {
            LOGGER.error("aof Exception ", e);
        }
    }

    public void load() {
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(fileName + suffix, "rw");
            FileChannel channel = randomAccessFile.getChannel();
            long len = channel.size();
            MappedByteBuffer mappedByteBuffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, len);
            ByteBuf bufferPolled = new PooledByteBufAllocator().buffer((int) len);
            bufferPolled.writeBytes(mappedByteBuffer);
            while (bufferPolled.readableBytes() > 0){
                Resp resp = null;
                try {
                    resp = Resp.decode(bufferPolled);
                }catch (Exception e) {
                    clean(mappedByteBuffer);
                    randomAccessFile.close();
                    bufferPolled.release();
                    break;
                }
                Command command = CommandFactory.from((RespArray) resp);
                WriteCommand writeCommand = (WriteCommand) command;
                writeCommand.handleLoadAof(this.mockClient);
            }

        } catch (IOException e) {
            LOGGER.error("aof IOException ", e);
        } catch (Exception e) {
            LOGGER.error("aof Exception ", e);
        }
    }

    public static void clean(final MappedByteBuffer buffer) throws Exception {
        if (buffer == null) {
            return;
        }
        buffer.force();
        AccessController.doPrivileged(new PrivilegedAction<Object>() {
            @Override
            public Object run() {
                try {
                    Method getCleanerMethod = buffer.getClass().getMethod("cleaner", new Class[0]);
                    getCleanerMethod.setAccessible(true);
                    sun.misc.Cleaner cleaner = (sun.misc.Cleaner) getCleanerMethod.invoke(buffer, new Object[0]);
                    cleaner.clean();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        });
    }
}
