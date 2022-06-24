package org.isheihei.redis.server.handler;


import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.isheihei.redis.core.resp.Resp;

/**
 * @ClassName: CommandDecoder
 * @Description: 数据编码
 * @Date: 2022/6/8 21:10
 * @Author: isheihei
 */
public class ResponseEncoder extends MessageToByteEncoder<Resp> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Resp resp, ByteBuf out) {
        try {
            Resp.write(resp, out);
        } catch (Exception e) {
            ctx.close();
        }
    }
}
