package org.isheihei.redis.core.resp.impl;

/**
 * @ClassName: RespType
 * @Description: RESP类型枚举
 * @Date: 2022/6/1 15:10
 * @Author: isheihei
 */
public enum RespType {
    ERROR((byte) '-'),
    STATUS((byte) '+'),
    BULK((byte) '$'),
    INTEGER((byte) ':'),
    MULTYBULK((byte) '*'),
    R((byte) '\r'),
    N((byte) '\n'),
    ZERO((byte) '0'),
    ONE((byte) '1'),;

    private byte code;

    RespType(byte code) {
        this.code = code;
    }

    public byte getCode() {
        return this.code;
    }
}
