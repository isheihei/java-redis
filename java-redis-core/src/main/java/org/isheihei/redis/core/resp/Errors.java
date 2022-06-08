package org.isheihei.redis.core.resp;

/**
 * @ClassName: Errors
 * @Description: RESP错误
 * @Date: 2022/6/1 15:40
 * @Author: isheihei
 */
public class Errors implements Resp{

    String content;

    public Errors(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}
