package org.isheihei.redis.core.persist;

/**
 * @ClassName: Persist
 * @Description: 持久化
 * @Date: 2022/6/16 19:37
 * @Author: isheihei
 */
public interface Persist {

    void save();

    void load();
}
