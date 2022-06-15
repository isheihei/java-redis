package org.isheihei.redis.core.resp.impl;

import org.isheihei.redis.core.resp.Resp;

/**
 * @ClassName: SimpleString
 * @Description: RESP单行字符串
 * @Date: 2022/6/1 15:12
 * @Author: isheihei
 */
public class SimpleString implements Resp {

    public static final SimpleString OK = new SimpleString("OK");

    private final String content;

    public SimpleString(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}
