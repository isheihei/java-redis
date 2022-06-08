package org.isheihei.redis.server.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.apache.log4j.Logger;
import org.isheihei.redis.common.util.TRACEID;
import org.isheihei.redis.core.command.Command;
import org.isheihei.redis.core.command.CommandFactory;
import org.isheihei.redis.core.persist.aof.Aof;
import org.isheihei.redis.core.resp.*;


/**
 * @ClassName: CommandDecoder
 * @Description: 数据解码
 * @Date: 2022/6/8 21:00
 * @Author: isheihei
 */
public class CommandDecoder extends LengthFieldBasedFrameDecoder {

    private static final Logger LOGGER = Logger.getLogger(CommandDecoder.class);
    private static final int MAX_FRAME_LENGTH = Integer.MAX_VALUE;

    public CommandDecoder(Aof aof) {
        this();
    }

    public CommandDecoder() {
        super(MAX_FRAME_LENGTH, 0, 4);
    }

    @Override
    public Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {

        TRACEID.newTraceId();
        while (in.readableBytes() != 0) {
            int mark = in.readerIndex();
            try {
                Resp resp = Resp.decode(in);
                if (!(resp instanceof RespArray || resp instanceof SimpleString)) {
                    throw new IllegalStateException("客户端发送的命令应该只能是Resp Array 和 单行命令 类型");
                }
                Command command = null;
                if (resp instanceof RespArray) {
                    command = CommandFactory.from((RespArray) resp);
                } else if (resp instanceof SimpleString) {
                    command = CommandFactory.from((SimpleString) resp);
                }
                if (command == null) {
                    //取出命令
                    ctx.writeAndFlush(new Errors("unsupport command:" + ((BulkString) ((RespArray) resp).getArray()[0]).getContent().toUtf8String()));
                } else {
                    return command;
                }
            } catch (Exception e) {
                in.readerIndex(mark);
                LOGGER.error("解码命令", e);
                break;
            }
        }
        return null;
    }

}
