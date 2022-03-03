package com.webank.wedatasphere.dss.data.governance.utils;

import com.alibaba.druid.pool.DruidDataSource;
import com.webank.wedatasphere.dss.data.governance.conf.GovernanceConf;


public class DataSourceUtil {
    private static DruidDataSource druidDataSource =null;

    private DataSourceUtil(){}

    public static DruidDataSource getDataSource(){
        if(druidDataSource ==null) {
            synchronized (DataSourceUtil.class){
                if(druidDataSource ==null){
                    druidDataSource = new DruidDataSource();
                    druidDataSource.setDriverClassName(GovernanceConf.METASTORE_DATASOURCE_DRIVER.getValue());
                    druidDataSource.setUrl(GovernanceConf.METASTORE_DATASOURCE_URL.getValue());
                    druidDataSource.setUsername(GovernanceConf.METASTORE_DATASOURCE_USERNAME.getValue());
                    druidDataSource.setPassword(GovernanceConf.METASTORE_DATASOURCE_PASSWORD.getValue());
                }
            }
        }
        return druidDataSource;
    }

}
