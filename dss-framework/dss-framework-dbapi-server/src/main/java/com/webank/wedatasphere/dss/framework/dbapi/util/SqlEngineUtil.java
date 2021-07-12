package com.webank.wedatasphere.dss.framework.dbapi.util;

import com.webank.wedatasphere.dss.orange.engine.DynamicSqlEngine;


public class SqlEngineUtil {

    static DynamicSqlEngine engine = new DynamicSqlEngine();

    public static DynamicSqlEngine getEngine() {
        return engine;
    }
}
