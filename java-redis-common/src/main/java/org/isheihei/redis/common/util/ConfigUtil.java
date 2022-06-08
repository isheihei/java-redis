package org.isheihei.redis.common.util;

import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;
import io.netty.util.internal.StringUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @ClassName: ConfigUtil
 * @Description: 配置工具类
 * @Date: 2022/6/8 21:36
 * @Author: isheihei
 */
public class ConfigUtil {

    public ConfigUtil() {
    }

    private static String CONFIG_FILE_PATH = "/redis_config.properties";
    private static String DEFAULT_IP = "0.0.0.0";
    private static Integer DEFAULT_PORT = 6379;
    private static String DEFAULT_AUTH = null;

    private static ConfigUtil Instance = new ConfigUtil();

    private static Properties configs;

    static {
        Properties properties = new Properties();
        try(InputStream inputStream = ConfigUtil.class.getResourceAsStream(CONFIG_FILE_PATH)) {
            properties.load(inputStream);
            configs = properties;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static String getAddress() {
        String address = configs.getProperty("ip");
        if (StringUtil.isNullOrEmpty(address)) {
            return DEFAULT_IP;
        }
        return address;
    }

    public static Integer getPort() {
        Integer port = DEFAULT_PORT;
        try {
            String strPort = configs.getProperty("port");
            port = Integer.parseInt(strPort);
        } catch (Exception e) {
            return DEFAULT_PORT;
        }
        if (port <= 0 || port > 60000) {
            return DEFAULT_PORT;
        }
        return port;
    }

    public static String getAuth() {
        return configs.getProperty("requirepass", null);
    }

}
