package org.isheihei.redis.common.util;



import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
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
    private static String DEFAULT_PORT = "6379";

    private static String DEFAULT_REQUIREPASS    = null;

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

    /**
     * @Description: 获取配置信息
     * @Param: configParam 配置属性名
     * @Return: String 如果不存在该配置，则返回null
     * @Author: isheihei
     */
    public static String getConfig(String configParam) throws Exception{
        if (configParam == null) {
            return null;
        } else if (configs.getProperty(configParam) == null) {
            return null;
        } else {
                Class<?> configUtilClass = ConfigUtil.class;
                Method method  = configUtilClass.getMethod("get" + RedisStringUtil.upperCaseFirst(configParam));
                return (String) method.invoke(null, null);
        }
    }

    /**
     * @Description: 修改配置属性，不能持久化到redis_config中，成功返回true，失败false
     * @Param: configParam 配置名
     * @Param: newValue 配置值
     * @Return: String
     * @Author: isheihei
     */
    public static boolean setConfig(String configParam, String newValue) throws Exception {
        if (RedisStringUtil.isNullOrEmpty(configParam) || RedisStringUtil.isNullOrEmpty(newValue)) {
            return false;
        }
        String oldValue = configs.getProperty(configParam);
        configs.setProperty(configParam, newValue);
        Class<?> configUtilClass = ConfigUtil.class;
        Method method  = configUtilClass.getMethod("get" + RedisStringUtil.upperCaseFirst(configParam));
        String check = (String) method.invoke(null, null);
        if (newValue.equals(check)) {
            return true;
        } else {
            configs.setProperty(configParam, oldValue);
            return false;
        }
    }


    public static String getIp() {
        String address = configs.getProperty("ip");
        if (RedisStringUtil.isNullOrEmpty(address)) {
            return DEFAULT_IP;
        }
        return address;
    }

    public static String getPort() {
        Integer port = Integer.parseInt(DEFAULT_PORT);
        try {
            String strPort = configs.getProperty("port");
            port = Integer.parseInt(strPort);
        } catch (Exception e) {
            return DEFAULT_PORT;
        }
        if (port <= 0 || port > 60000) {
            return DEFAULT_PORT;
        }
        return String.valueOf(port);
    }

    public static String getRequirepass() {
        return configs.getProperty("requirepass", null);
    }

}
