package org.isheihei.redis.core.resp;

import org.isheihei.redis.core.struct.BytesWrapper;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @ClassName: BulkString
 * @Description: RESP多行字符串
 * @Date: 2022/6/1 15:51
 * @Author: isheihei
 */
public class BulkString implements Resp{
    public static final BulkString NullBulkString = new BulkString(null);
    static final Charset CHARSET = StandardCharsets.UTF_8;
    BytesWrapper content;

    public BulkString(BytesWrapper content) {
        this.content = content;
    }

    public BytesWrapper getContent() {
        return content;
    }
}
