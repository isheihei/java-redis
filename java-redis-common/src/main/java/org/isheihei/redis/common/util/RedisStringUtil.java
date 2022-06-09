package org.isheihei.redis.common.util;

/**
 * @ClassName: StringUtil
 * @Description: 字符串操作工具类
 * @Date: 2022/6/9 19:03
 * @Author: isheihei
 */
public class RedisStringUtil {

    /**
     * @Description: 首字母变大写
     * @Param: s
     * @Return: String
     * @Author: isheihei
     */
    public static String upperCaseFirst(String s) {
        char[] ch = s.toCharArray();
        if (ch[0] >= 'a' && ch[0] <= 'z') {
            ch[0] = (char) (ch[0] - 32);
        }
        return new String(ch);
    }

    public static boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }
}
