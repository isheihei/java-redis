package org.isheihei.redis.server.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.apache.log4j.Logger;
import org.isheihei.redis.common.consts.ErrorsConst;
import org.isheihei.redis.core.command.AbstractWriteCommand;
import org.isheihei.redis.core.command.Command;
import org.isheihei.redis.core.command.CommandFactory;
import org.isheihei.redis.core.persist.aof.Aof;
import org.isheihei.redis.core.resp.Resp;
import org.isheihei.redis.core.resp.impl.BulkString;
import org.isheihei.redis.core.resp.impl.Errors;
import org.isheihei.redis.core.resp.impl.RespArray;
import org.isheihei.redis.core.resp.impl.SimpleString;


/**
 * @ClassName: CommandDecoder
 * @Description: 数据解码
 * @Date: 2022/6/8 21:00
 * @Author: isheihei
 */
public class CommandDecoder extends LengthFieldBasedFrameDecoder {

    private static final Logger LOGGER = Logger.getLogger(CommandDecoder.class);
    private static final int MAX_FRAME_LENGTH = Integer.MAX_VALUE;

    private Aof aof;
    public CommandDecoder(Aof aof) {
        this();
        this.aof = aof;
    }

    public CommandDecoder() {
        // 读取完整数据包
        super(MAX_FRAME_LENGTH, 0, 0);
    }

    @Override
    public Object decode(ChannelHandlerContext ctx, ByteBuf in) {
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
                    ctx.writeAndFlush(new Errors(String.format(ErrorsConst.UNKNOWN_COMMAND, ((BulkString) ((RespArray) resp).getArray()[0]).getContent().toUtf8String())));
                } else {
                    if (aof != null && command instanceof AbstractWriteCommand) {
                        ((AbstractWriteCommand) command).setAof(aof);
                    }
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
