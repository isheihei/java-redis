package org.isheihei.redis.common.consts;

/**
 * @ClassName: ErrorsConsts
 * @Description: TODO 重构成类的形式
 * @Date: 2022/6/8 23:18
 * @Author: isheihei
 */
public class ErrorsConsts {
    public static final String INVALID_PASSWORD = "ERR invalid password";
    public static final String NO_AUTH = "NOAUTH Authentication required.";
    public static final String UNKNOWN_COMMAND = "ERR unknown command '%s'";


    public static final String INTERNEL_ERROR = "ERR internal error!";
    public static final String UNSUPOORT_CONFIG = "ERR Unsupported CONFIG parameter: %s";

    public static final String WRONG_ARGS_NUMBER = "ERR Wrong number of arguments for %s %s";

    public static final String COMMAND_WRONG_ARGS_NUMBER = "ERR Wrong number of arguments for '%s' command";

    public static final String CONFIG_SUB_COMMAND_ERROR = "ERR CONFIG subcommand must be one of GET, SET";
    public static final String CLIENT_SUB_COMMAND_ERROR = "ERR Syntax error, try CLIENT (GETNAME | SETNAME connection-name)";
}
