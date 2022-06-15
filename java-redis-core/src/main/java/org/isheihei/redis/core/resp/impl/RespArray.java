package org.isheihei.redis.core.resp.impl;

import org.isheihei.redis.core.resp.Resp;

/**
 * @ClassName: RespArray
 * @Description: RESP数组
 * @Date: 2022/6/1 15:49
 * @Author: isheihei
 */
public class RespArray implements Resp {

    Resp[] array;

    public RespArray(Resp[] array) {
        this.array = array;
    }

    public Resp[] getArray() {
        return array;
    }
}
