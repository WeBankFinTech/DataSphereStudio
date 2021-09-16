package com.webank.wedatasphere.dss.orange.node;

import java.util.Arrays;
import java.util.List;

public class WhereSqlNode extends TrimSqlNode {

    static List<String> prefixesToOverride = Arrays.asList("AND ", "AND\r", "AND\t", "AND\n", "OR ", "OR\r", "OR\t", "OR\n"
            , "and ", "and\r", "and\t", "and\n", "or ", "or\r", "or\t", "or\n");

    public WhereSqlNode(SqlNode contents) {

        super(contents, "WHERE ", null, prefixesToOverride, null);
    }
}
