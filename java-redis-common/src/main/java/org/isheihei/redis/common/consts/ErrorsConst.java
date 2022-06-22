package org.isheihei.redis.common.consts;

/**
 * @ClassName: ErrorsConst
 * @Description: 错误返回信息
 * @Date: 2022/6/8 23:18
 * @Author: isheihei
 */
public class ErrorsConst {
    public static final String INVALID_PASSWORD = "ERR invalid password";
    public static final String NO_AUTH = "NOAUTH Authentication required.";
    public static final String UNKNOWN_COMMAND = "ERR unknown command '%s'";
    public static final String INTERNEL_ERROR = "ERR internal error!";
    public static final String UNSUPOORT_CONFIG = "ERR Unsupported CONFIG parameter: %s";
    // 子命令参数错误
    public static final String SUBCOMMAND_WRONG_ARGS_NUMBER = "ERR Wrong number of arguments for %s %s";
    // 命令没有参数
    public static final String COMMAND_WRONG_ARGS_NUMBER = "ERR Wrong number of arguments for '%s' command";
    // 命令参数数量错误
    public static final String WRONG_ARGS_NUMBER = "ERR Wrong number of arguments for %s";
    public static final String CONFIG_SUB_COMMAND_ERROR = "ERR CONFIG subcommand must be one of GET, SET";
    public static final String CLIENT_SUB_COMMAND_ERROR = "ERR Syntax error, try CLIENT (GETNAME | SETNAME connection-name)";
    public static final String NO_SUCH_KEY = "ERR no such key";
    public static final String WRONG_TYPE_OPERATION = "WRONGTYPE Operation against a key holding the wrong kind of value";
    public static final String INDEX_OUT_OF_RANGE = "ERR index out of range";
    public static final String VALUE_IS_NOT_INT = "ERR value is not an integer or out of range";
    public static final String INVALID_DB_INDEX = "ERR invalid DB index";

    public static final String INVALID_FLOAT = "ERR value is not a valid float";

    public static final String SYNTAX_ERROR = "ERR syntax error";

    public static final String MIN_OR_MAX_NOT_FLOAT = "ERR min or max is not a float";
    public static final String MULTI_CAN_NOT_NESTED = "ERR MULTI calls can not be nested";
    public static final String EXEC_WITHOUT_MULTI = "ERR EXEC without MULTI";
    public static final String DISCARD_WITHOUT_MULTI = "ERR DISCARD without MULTI";
}
