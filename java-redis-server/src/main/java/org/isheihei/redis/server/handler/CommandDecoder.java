package org.isheihei.redis.server.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
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

import java.util.List;


/**
 * @ClassName: CommandDecoder
 * @Description: 数据解码
 * @Date: 2022/6/8 21:00
 * @Author: isheihei
 */
public class CommandDecoder extends ByteToMessageDecoder {

    private static final Logger LOGGER = Logger.getLogger(CommandDecoder.class);

    private Aof aof;
    public CommandDecoder(Aof aof) {
        this.aof = aof;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
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
                    out.add(command);
                }
            } catch (Exception e) {
                in.readerIndex(mark);
                LOGGER.error("解码命令", e);
                break;
            }
        }
    }

}
