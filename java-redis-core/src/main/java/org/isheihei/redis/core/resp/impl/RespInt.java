package org.isheihei.redis.core.resp.impl;

import org.isheihei.redis.core.resp.Resp;

/**
 * @ClassName: RespInt
 * @Description: RESP整数
 * @Date: 2022/6/1 15:50
 * @Author: isheihei
 */
public class RespInt implements Resp {
    int value;

    public RespInt(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
