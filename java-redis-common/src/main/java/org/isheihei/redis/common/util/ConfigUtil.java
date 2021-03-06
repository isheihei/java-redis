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

    private static String DEFAULT_AOF_PATH    = "./persist/aof/";

    private static String DEFAULT_RDB_PATH    = "./persist/rdb/";

    private static final String DEFAULT_APPEND_FILE_NAME = "appendonly.aof";

    private static final String DEFAULT_DATABASE_FILE_NAME = "database.rdb";

    private static String DEFAULT_APPEND_ONLY    = "false";

    private static final String DEFAULT_RDB = "false";

    private static final String DEFAULT_DB_NUM = "16";

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
                Method method  = configUtilClass.getDeclaredMethod(configParam);
                method.setAccessible(true);
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
    public static boolean setConfig(String configParam, String newValue) {
        if (RedisStringUtil.isNullOrEmpty(configParam) || RedisStringUtil.isNullOrEmpty(newValue)) {
            return false;
        }
        String oldValue = configs.getProperty(configParam);
        configs.setProperty(configParam, newValue);
        Class<?> configUtilClass = ConfigUtil.class;
        try {
            Method method = configUtilClass.getDeclaredMethod(configParam);
            method.setAccessible(true);
            String check = ((String) method.invoke(null, null));
            if (newValue.equals(check)) {
                return true;
            } else {
                configs.setProperty(configParam, oldValue);
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static String ip() {
        String address = configs.getProperty("ip");
        if (RedisStringUtil.isNullOrEmpty(address)) {
            return DEFAULT_IP;
        }
        return address;
    }

    public static String getIp() {
        return ip();
    }

    private static String port() {
        int port;
        String strPort;
        try {
            strPort = configs.getProperty("port");
            port = Integer.parseInt(strPort);
        } catch (Exception e) {
            return DEFAULT_PORT;
        }
        if (port <= 0 || port > 60000) {
            return DEFAULT_PORT;
        }
        return strPort;
    }

    public static int getPort() {
        return Integer.parseInt(port());
    }

    private static String requirepass() {
        return configs.getProperty("requirepass", null);
    }

    public static String getRequirePass() {
        return requirepass();
    }

    private static String aofpath() {
        return DEFAULT_AOF_PATH;
    }

    public static String getAofPath() {
        return aofpath();
    }

    private static String appendfilename() {
        return configs.getProperty("appendfilename", DEFAULT_APPEND_FILE_NAME);
    }

    public static String getAppendFileName() {
        return appendfilename();
    }
    private static String appendonly() {
        return "true".equals(configs.getProperty("appendonly", DEFAULT_APPEND_ONLY))  ? "true"  : "false";
    }

    public static boolean getAppendOnly() {
        return Boolean.parseBoolean(appendonly());
    }

    private static String dbnum() {
        int dbNum;
        String strDbNum;
        try {
            strDbNum = configs.getProperty("dbnum");
            dbNum = Integer.parseInt(strDbNum);
        } catch (Exception e) {
            return DEFAULT_DB_NUM;
        }
        if (dbNum <= 0 || dbNum > 64) {
            return DEFAULT_DB_NUM;
        }
        return strDbNum;
    }

    public static int getDbNum() {
        return Integer.parseInt(dbnum());
    }

    private static String databasefilename() {
        return configs.getProperty("databasefilename", DEFAULT_DATABASE_FILE_NAME);
    }

    public static String getDataBaseFileName() {
        return databasefilename();
    }

    private static String rdbpath() {
        return DEFAULT_RDB_PATH;
    }

    public static String getRdbPath() {
        return rdbpath();
    }

    private static String rdb() {
        return "true".equals(configs.getProperty("rdb", DEFAULT_RDB))  ? "true"  : "false";
    }

    public static boolean getRdb() {
        return Boolean.parseBoolean(rdb());
    }
}
