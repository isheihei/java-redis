package org.isheihei.redis.core.struct;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * @ClassName: BytesWrapper
 * @Description: 内存复用，减少内存新建/销毁的开销
 * @Date: 2022/6/1 15:58
 * @Author: isheihei
 */
public class BytesWrapper implements Comparable<BytesWrapper>{
    static final Charset CHARSET = StandardCharsets.UTF_8;

    private final byte[] content;

    public BytesWrapper(byte[] content) {
        this.content = content;
    }

    public byte[] getByteArray() {
        return content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BytesWrapper that = (BytesWrapper) o;
        return Arrays.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(content);
    }

    public String toUtf8String() {
        return new String(content, CHARSET);
    }

    @Override
    public int compareTo(BytesWrapper o) {
        int len1 = content.length;
        int len2 = o.getByteArray().length;
        int lim = Math.min(len1, len2);
        byte v1[] = content;
        byte v2[] = o.getByteArray();

        int k = 0;
        while (k < lim) {
            byte c1 = v1[k];
            byte c2 = v2[k];
            if (c1 != c2) {
                return c1 - c2;
            }
            k++;
        }
        return len1 - len2;
    }
}