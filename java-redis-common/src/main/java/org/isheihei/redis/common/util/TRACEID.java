package org.isheihei.redis.common.util;

/**
 * @ClassName: TRACEID
 * @Description: 调试追踪id工具类
 * @Date: 2022/6/7 22:01
 * @Author: isheihei
 */
public class TRACEID {
    private static final ThreadLocal<String> TRACEID = new ThreadLocal<String>();
    private static final Uid                 uid     = WinterId.instance();

    public static String newTraceId()
    {
        String traceId = uid.generateDigits();
        TRACEID.set(traceId);
        return traceId;
    }

    public static String currentTraceId()
    {
        String result = TRACEID.get();
        if (result == null)
        {
            return newTraceId();
        }
        else
        {
            return result;
        }
    }

    public static void bind(String traceId)
    {
        TRACEID.set(traceId);
    }
}
