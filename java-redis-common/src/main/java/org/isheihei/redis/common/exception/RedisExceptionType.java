package org.isheihei.redis.common.exception;

/**
 * @ClassName: RedisExceptionCode
 * @Description: TODO
 * @Date: 2022/6/1 14:49
 * @Author: isheihei
 */
public enum RedisExceptionType {

    REDIS_READ_EXCEPTION("0101", "没有读取到完整的命令");

    private final String code;

    private final String msg;

    RedisExceptionType(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public String toString() {
        return  "code: " + code + ", message: " + msg;
    }

    public Exception initRedisException() {
        return new RuntimeException(this.toString());
    }

    public Exception initRedisException(String detailMsg) {
        return new RuntimeException(this.toString() + ", detail message: " + detailMsg);
    }
}
