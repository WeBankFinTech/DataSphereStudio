/*
 *
 *  * Copyright 2019 WeBank
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.webank.wedatasphere.dss.appconn.loader.loader;


import com.webank.wedatasphere.dss.appconn.core.AppConn;
import com.webank.wedatasphere.dss.appconn.loader.clazzloader.AppConnClassLoader;
import com.webank.wedatasphere.dss.appconn.loader.exception.NoSuchAppConnException;
import com.webank.wedatasphere.dss.appconn.loader.utils.AppConnUtils;
import com.webank.wedatasphere.dss.common.utils.DSSExceptionUtils;
import com.webank.wedatasphere.linkis.common.exception.ErrorException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Objects;

public class CommonAppConnLoader implements AppConnLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommonAppConnLoader.class);

    @Override
    public AppConn getAppConn(String appConnName) throws Exception {
        return getAppConn(appConnName, null, null);
    }

    @Override
    public AppConn getAppConn(String appConnName, String spi, String classPath) throws Exception {
        ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
        String libPathUrl = null;
        if (StringUtils.isNotEmpty(classPath)){
            libPathUrl = classPath;
        }else{
            String loadClassPath =  Objects.requireNonNull(currentClassLoader.getResource("")).getPath();
            String basePathUrl = loadClassPath + ".." + File.separator + ".." + File.separator + APPCONN_DIR_NAME;
            libPathUrl = basePathUrl + File.separator + appConnName + File.separator + LIB_NAME;
        }
        LOGGER.info("libPath url is {}",  libPathUrl);
        URL finalURL = null;
        try {
            String finalUrlStr = libPathUrl.endsWith("/") ? libPathUrl + "*" : libPathUrl + "/*";
            finalURL = new URL(AppConnLoader.FILE_SCHEMA +  finalUrlStr);
        } catch (MalformedURLException e) {
            DSSExceptionUtils.dealErrorException(70061, libPathUrl + " url is wrong", e, ErrorException.class);
        }
        List<URL> jars = AppConnUtils.getJarsUrlsOfPath(libPathUrl);
        // 为了加载jar包的类，需要URLClassLoader
        ClassLoader classLoader = new AppConnClassLoader(jars.toArray(new URL[1]), currentClassLoader);
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
        if (clazz == null) {
            Thread.currentThread().setContextClassLoader(currentClassLoader);
            return null;
        } else {
            AppConn retAppConn = (AppConn) clazz.newInstance();
            Thread.currentThread().setContextClassLoader(currentClassLoader);
            LOGGER.info("AppConn is {},  retAppConn is {}", appConnName, retAppConn.getClass().toString());
            return retAppConn;
        }
    }
}
