package org.isheihei.redis.core.resp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import org.isheihei.redis.core.resp.impl.BulkString;
import org.isheihei.redis.core.resp.impl.Errors;
import org.isheihei.redis.core.resp.impl.RespArray;
import org.isheihei.redis.core.resp.impl.RespInt;
import org.isheihei.redis.core.resp.impl.SimpleString;
import org.isheihei.redis.core.struct.impl.BytesWrapper;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @ClassName: Resp
 * @Description: Redis Serialization Protocol协议
 * @Date: 2022/6/1 13:15
 * @Author: isheihei
 */
public interface Resp {

    org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(Resp.class);

    Charset CHARSET = StandardCharsets.UTF_8;

    /**
     * @Description: 回写
     * @Param: resp 
     * @Param: buffer 
     * @Return: void
     * @Author: isheihei
     */
    static void write(Resp resp, ByteBuf buffer) {
        if (resp instanceof SimpleString) {
            buffer.writeByte(RespType.STATUS.getCode());
            String content = ((SimpleString) resp).getContent();
            buffer.writeBytes(content.getBytes(CHARSET));
            writeEof(buffer);
        } else if (resp instanceof Errors) {
            buffer.writeByte(RespType.ERROR.getCode());
            String content = ((Errors) resp).getContent();
            buffer.writeBytes(content.getBytes(CHARSET));
            writeEof(buffer);
        } else if (resp instanceof RespInt) {
            buffer.writeByte(RespType.INTEGER.getCode());
            String content = String.valueOf(((RespInt) resp).getValue());
            buffer.writeBytes(content.getBytes(CHARSET));
            writeEof(buffer);
        } else if (resp instanceof BulkString) {
            buffer.writeByte(RespType.BULK.getCode());
            BytesWrapper content = ((BulkString) resp).getContent();
            if (content == null) {
                // null: "$-1\r\n"
                buffer.writeByte(RespType.ERROR.getCode());
                buffer.writeByte(RespType.ONE.getCode());
                writeEof(buffer);
            } else if (content.getByteArray().length == 0) {
                // 空串: "$0\r\n\r\n"
                buffer.writeByte(RespType.ZERO.getCode());
                writeEof(buffer);
                writeEof(buffer);
            } else {
                // 正常编码："foobar" 的编码为 "$6\r\nfoobar\r\n"，其中 6 是字节数
                String length = String.valueOf(content.getByteArray().length);
                buffer.writeBytes(length.getBytes(CHARSET));
                writeEof(buffer);
                buffer.writeBytes(content.getByteArray());
                writeEof(buffer);
            }
        } else if (resp instanceof RespArray) {
            buffer.writeByte(RespType.MULTYBULK.getCode());
            Resp[] array = ((RespArray) resp).getArray();
            String length = String.valueOf(array.length);
            buffer.writeBytes(length.getBytes(CHARSET));
            writeEof(buffer);
            for (Resp each : array) {
                write(each, buffer);
            }
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * @Description: 解码为协议对应具体格式
     * @Param: buffer
     * @Return: Resp
     * @Author: isheihei
     */
    static Resp decode(ByteBuf buffer) {
        if (buffer.readableBytes() <= 0) {
            throw new IllegalStateException("没有读取到完整的命令");
        }

        byte b =buffer.readByte();
        if (b == RespType.STATUS.getCode()) {
            return new SimpleString(getString(buffer));
        } else if (b == RespType.ERROR.getCode()) {
            return new Errors(getString(buffer));
        } else if (b == RespType.INTEGER.getCode()) {
            int value = getNumber(buffer);
            return new RespInt(value);
        } else if (b == RespType.BULK.getCode()) {
            int length = getNumber(buffer);
            if (buffer.readableBytes() < length + 2) {
                throw new IllegalStateException("没有读取到完整的命令");
            }
            byte[] content;
            if (length == -1) {
                content = null;
            } else {
                content = new byte[length];
                buffer.readBytes(content);
            }
            if (buffer.readByte() != RespType.R.getCode() || buffer.readByte() != RespType.N.getCode()) {
                throw new IllegalStateException("没有读取到完整的命令");
            }
            return new BulkString(new BytesWrapper(content));
        } else if (b == RespType.MULTYBULK.getCode()) {
            int numOfElement = getNumber(buffer);
            Resp[] array = new Resp[numOfElement];
            for (int i = 0; i < numOfElement; i++) {
                array[i] = decode(buffer);
            }
            return new RespArray(array);
        } else {
            throw new IllegalStateException("无法解析命令");
        }
    }


    /**
     * @Description: 读取整数类型
     * @Param: buffer
     * @Return: int
     * @Author: isheihei
     */
    static int getNumber(ByteBuf buffer) {
        byte b;
        b = buffer.readByte();
        boolean positive = true;
        int value = 0;
        // 错误（Errors）： 响应的首字节是 "-"
        if (b == RespType.ERROR.getCode()) {
            positive = false;
        } else {
            value = b - RespType.ZERO.getCode();
        }
        while (buffer.readableBytes() > 0 && (b = buffer.readByte()) != RespType.R.getCode()) {
            value = value * 10 + (b - RespType.ZERO.getCode());
        }
        if (buffer.readableBytes() == 0 || buffer.readByte() != RespType.N.getCode()) {
            throw new IllegalStateException("没有读取到完整的命令");
        }
        if (!positive) {
            value = -value;
        }
        return value;
    }

    /**
     * @Description: 读取一条字符串
     * @Param: buffer
     * @Return: String
     * @Author: isheihei
     */
    static String getString(ByteBuf buffer) {
        byte b;
        ByteBuf byteBuf = PooledByteBufAllocator.DEFAULT.directBuffer();
        // 以终止符 /R 为结束标志
        while (buffer.readableBytes() > 0 && (b = buffer.readByte()) != RespType.R.getCode()) {
            byteBuf.writeByte(b);
        }
        // /R 后面必须紧接 /N
        if (buffer.readableBytes() == 0 || buffer.readableBytes() != RespType.N.getCode()) {
            throw new IllegalStateException("没有读取到完整的命令");
        }
        return byteBuf.toString(CHARSET);
    }

    /**
     * @Description: 写协议终止符："\r\n" (CRLF)
     * @Param: buffer
     * @Return: void
     * @Author: isheihei
     */
    static void writeEof(ByteBuf buffer) {
        buffer.writeByte(RespType.R.getCode());
        buffer.writeByte(RespType.N.getCode());
    }
}
