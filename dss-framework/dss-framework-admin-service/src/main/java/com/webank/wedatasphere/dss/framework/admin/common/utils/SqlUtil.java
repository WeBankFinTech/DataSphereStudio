package com.webank.wedatasphere.dss.framework.admin.common.utils;

import com.webank.wedatasphere.dss.framework.admin.exception.DSSAdminWarnException;

public class SqlUtil {

    public static String SQL_PATTERN = "[a-zA-Z0-9_\\ \\,\\.]+";
    public static String escapeOrderBySql(String value)
    {
        if (StringUtils.isNotEmpty(value) && !isValidOrderBySql(value))
        {
            throw new DSSAdminWarnException("参数不符合规范，不能进行查询");
        }
        return value;
    }
    public static boolean isValidOrderBySql(String value)
    {
        return value.matches(SQL_PATTERN);
    }
}
