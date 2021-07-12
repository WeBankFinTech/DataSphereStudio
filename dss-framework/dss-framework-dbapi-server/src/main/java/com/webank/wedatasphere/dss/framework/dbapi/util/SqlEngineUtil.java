package com.webank.wedatasphere.dss.framework.dbapi.util;

import com.jq.orange.engine.DynamicSqlEngine;


public class SqlEngineUtil {

    static DynamicSqlEngine engine = new DynamicSqlEngine();

    public static DynamicSqlEngine getEngine() {
        return engine;
    }
}
