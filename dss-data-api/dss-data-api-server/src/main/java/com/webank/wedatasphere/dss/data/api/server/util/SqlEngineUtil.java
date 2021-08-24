package com.webank.wedatasphere.dss.data.api.server.util;
import com.webank.wedatasphere.dss.orange.DynamicSqlEngine;


public class SqlEngineUtil {

    static DynamicSqlEngine engine = new DynamicSqlEngine();

    public static DynamicSqlEngine getEngine() {
        return engine;
    }
}
