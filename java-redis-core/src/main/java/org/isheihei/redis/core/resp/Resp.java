package org.isheihei.redis.core.resp;

import io.netty.buffer.ByteBuf;
import org.isheihei.redis.core.resp.impl.BulkString;
import org.isheihei.redis.core.resp.impl.Errors;
import org.isheihei.redis.core.resp.impl.RespArray;
import org.isheihei.redis.core.resp.impl.RespInt;
import org.isheihei.redis.core.resp.impl.RespType;
import org.isheihei.redis.core.resp.impl.SimpleString;
import org.isheihei.redis.core.struct.impl.BytesWrapper;

/**
 * @ClassName: Resp
 * @Description: Redis Serialization Protocol协议 TODO 读写byte
 * @Date: 2022/6/1 13:15
 * @Author: isheihei
 */
public interface Resp {

    org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(Resp.class);

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
            char[] charArray = content.toCharArray();
            for (char each : charArray) {
                buffer.writeByte((byte) each);
            }
            writeEof(buffer);
        } else if (resp instanceof Errors) {
            buffer.writeByte(RespType.ERROR.getCode());
            String content = ((Errors) resp).getContent();
            char[] charArray = content.toCharArray();
            for (char each : charArray) {
                buffer.writeByte((byte) each);
            }
            writeEof(buffer);
        } else if (resp instanceof RespInt) {
            buffer.writeByte(RespType.INTEGER.getCode());
            String content = String.valueOf(((RespInt) resp).getValue());
            char[] charArray = content.toCharArray();
            for (char each : charArray) {
                buffer.writeByte((byte) each);
            }
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
                char[] charArray = length.toCharArray();
                for (char each : charArray) {
                    buffer.writeByte((byte) each);
                }
                writeEof(buffer);
                buffer.writeBytes(content.getByteArray());
                writeEof(buffer);
            }
        } else if (resp instanceof RespArray) {
            buffer.writeByte(RespType.MULTYBULK.getCode());
            Resp[] array = ((RespArray) resp).getArray();
            String length = String.valueOf(array.length);
            char[] charArray = length.toCharArray();
            for (char each : charArray) {
                buffer.writeByte((byte) each);
            }
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

        char c = (char) buffer.readByte();
        if (c == RespType.STATUS.getCode()) {
            return new SimpleString(getString(buffer));
        } else if (c == RespType.ERROR.getCode()) {
            return new Errors(getString(buffer));
        } else if (c == RespType.INTEGER.getCode()) {
            int value = getNumber(buffer);
            return new RespInt(value);
        } else if (c == RespType.BULK.getCode()) {
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
        } else if (c == RespType.MULTYBULK.getCode()) {
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
        char t;
        t = (char) buffer.readByte();
        boolean positive = true;
        int value = 0;
        // 错误（Errors）： 响应的首字节是 "-"
        if (t == RespType.ERROR.getCode()) {
            positive = false;
        } else {
            value = t - RespType.ZERO.getCode();
        }
        while (buffer.readableBytes() > 0 && (t = (char) buffer.readByte()) != RespType.R.getCode()) {
            value = value * 10 + (t - RespType.ZERO.getCode());
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
        char c;
        StringBuilder builder = new StringBuilder();

        // 以终止符 /R 为结束标志
        while (buffer.readableBytes() > 0 && (c = (char) buffer.readByte()) != RespType.R.getCode()) {
            builder.append(c);
        }
        // /R 后面必须紧接 /N
        if (buffer.readableBytes() == 0 || buffer.readableBytes() != RespType.N.getCode()) {
            throw new IllegalStateException("没有读取到完整的命令");
        }
        return builder.toString();
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
