package com.webank.wedatasphere.dss.orange;

import java.util.List;


public class SqlMeta {

    String sql;
    List<Object> jdbcParamValues;

    public SqlMeta(String sql, List<Object> jdbcParamValues) {
        this.sql = sql;
        this.jdbcParamValues = jdbcParamValues;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public List<Object> getJdbcParamValues() {
        return jdbcParamValues;
    }

    public void setJdbcParamValues(List<Object> jdbcParamValues) {
        this.jdbcParamValues = jdbcParamValues;
    }
}
