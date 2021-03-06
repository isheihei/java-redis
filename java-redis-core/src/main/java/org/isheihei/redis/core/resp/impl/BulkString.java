package org.isheihei.redis.core.resp.impl;

import org.isheihei.redis.core.resp.Resp;
import org.isheihei.redis.core.struct.impl.BytesWrapper;

/**
 * @ClassName: BulkString
 * @Description: RESP多行字符串
 * @Date: 2022/6/1 15:51
 * @Author: isheihei
 */
public class BulkString implements Resp {
    public static final BulkString NullBulkString = new BulkString(null);
    BytesWrapper content;

    public BulkString(BytesWrapper content) {
        this.content = content;
    }

    public BytesWrapper getContent() {
        return content;
    }
}
