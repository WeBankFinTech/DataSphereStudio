package com.webank.wedatasphere.dss.data.common.utils;

import com.webank.wedatasphere.dss.data.common.conf.AtlasConf;


import com.alibaba.druid.pool.DruidDataSource;

/** @Classname DataSourceUtil @Description TODO @Date 2021/8/23 15:36 @Created by suyc */
public class DataSourceUtil {
    private static DruidDataSource druidDataSource = null;

    private DataSourceUtil() {}

    public static DruidDataSource getDataSource() {
        if (druidDataSource == null) {
            synchronized (DataSourceUtil.class) {
                if (druidDataSource == null) {
                    druidDataSource = new DruidDataSource();
                    druidDataSource.setDriverClassName(
                            AtlasConf.METASTORE_DATASOURCE_DRIVER.getValue());
                    druidDataSource.setUrl(AtlasConf.METASTORE_DATASOURCE_URL.getValue());
                    druidDataSource.setUsername(AtlasConf.METASTORE_DATASOURCE_USERNAME.getValue());
                    druidDataSource.setPassword(AtlasConf.METASTORE_DATASOURCE_PASSWORD.getValue());
                }
            }
        }
        return druidDataSource;
    }
}
