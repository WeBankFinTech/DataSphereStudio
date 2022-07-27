/*
 * Copyright 2019 WeBank
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.webank.wedatasphere.dss.appconn.loader.loader;


import com.webank.wedatasphere.dss.appconn.core.AppConn;
import com.webank.wedatasphere.dss.appconn.loader.clazzloader.AppConnClassLoader;
import com.webank.wedatasphere.dss.appconn.loader.exception.NoSuchAppConnException;
import com.webank.wedatasphere.dss.appconn.loader.utils.AppConnUtils;
import com.webank.wedatasphere.dss.common.utils.DSSExceptionUtils;
import com.webank.wedatasphere.dss.standard.common.utils.AppStandardClassUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.linkis.common.conf.BDPConfiguration;
import org.apache.linkis.common.exception.ErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;

public class CommonAppConnLoader implements AppConnLoader {

    private static final String LIB_NAME = "lib";
    private static final String APP_CONN_PROPERTIES_NAME = "appconn.properties";

    private static final Logger LOGGER = LoggerFactory.getLogger(CommonAppConnLoader.class);

    @Override
    public AppConn getAppConn(String appConnName, String spi, String homePath) throws Exception {
        ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
        String libPathUrl;
        if (StringUtils.isNotEmpty(homePath)){
            libPathUrl = new File(homePath, LIB_NAME).getPath();
        } else {
            libPathUrl = Paths.get(AppConnUtils.getAppConnHomePath(), appConnName, LIB_NAME).toFile().getPath();
        }
        LOGGER.info("The libPath url of AppConn {} is {}.", appConnName, libPathUrl);
        List<URL> jars = AppConnUtils.getJarsUrlsOfPath(libPathUrl);
        ClassLoader classLoader = AppStandardClassUtils.refreshClassloader(appConnName, () -> new AppConnClassLoader(jars.toArray(new URL[1]), currentClassLoader));
        Thread.currentThread().setContextClassLoader(classLoader);
        String fullClassName;
        if (StringUtils.isEmpty(spi)) {
            try {
                fullClassName = AppConnUtils.getAppConnClassName(appConnName, libPathUrl, classLoader);
            } catch (NoSuchAppConnException e) {
                Thread.currentThread().setContextClassLoader(currentClassLoader);
                throw e;
            }
        } else {
            fullClassName = spi;
        }
        Class<?> clazz = null;
        try {
            clazz = classLoader.loadClass(fullClassName);
        } catch (ClassNotFoundException e) {
            Thread.currentThread().setContextClassLoader(currentClassLoader);
            DSSExceptionUtils.dealErrorException(70062, fullClassName + " class not found ", e, ErrorException.class);
        }
        AppConn retAppConn = (AppConn) clazz.newInstance();
        // 加载 appconn.properties
        File appConnPropertiesPathUrl;
        if (StringUtils.isNotEmpty(homePath)){
            appConnPropertiesPathUrl = new File(homePath, APP_CONN_PROPERTIES_NAME);
        } else {
            appConnPropertiesPathUrl = Paths.get(AppConnUtils.getAppConnHomePath(), appConnName, APP_CONN_PROPERTIES_NAME).toFile();
        }
        if(appConnPropertiesPathUrl.exists() && appConnPropertiesPathUrl.isFile()) {
            Properties properties = new Properties();
            properties.load(FileUtils.openInputStream(appConnPropertiesPathUrl));
            if(!properties.isEmpty()) {
                LOGGER.info("AppConn {} try to load {}, the properties is {}.", appConnName, APP_CONN_PROPERTIES_NAME, properties);
                properties.forEach((key, value) -> {
                    if(key != null && StringUtils.isNotBlank((String) key) &&
                            value != null && StringUtils.isNotBlank((String) value)) {
                        BDPConfiguration.setIfNotExists((String) key, (String) value);
                    }
                });
            }
        }
        Thread.currentThread().setContextClassLoader(currentClassLoader);
        LOGGER.info("Loaded appConn {} with class {}.", appConnName, retAppConn.getClass().getName());
        return retAppConn;
    }
}
