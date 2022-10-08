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


import com.webank.wedatasphere.dss.appconn.loader.conf.AppConnLoaderConf;
import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppConnLoaderFactory {

    private static final Logger logger = LoggerFactory.getLogger(AppConnLoaderFactory.class);

    private static Class<? extends AppConnLoader> clazz = CommonAppConnLoader.class;
    private static volatile AppConnLoader appConnLoader = null;

    @SuppressWarnings("unchecked")
    public static AppConnLoader getAppConnLoader(){
        if (appConnLoader == null){
            synchronized (AppConnLoaderFactory.class){
                if (appConnLoader == null){
                    // The corresponding classes can be loaded by configuration
                    String className = AppConnLoaderConf.CLASS_LOADER_CLASS_NAME().getValue();
                    if (StringUtils.isNotBlank(className)){
                        try{
                            clazz = ClassUtils.getClass(className);
                        }catch(ClassNotFoundException e){
                            logger.warn(String.format("Can not get AppConnLoader class %s, CommonAppConnLoader will be used by default.", className), e);
                        }
                    }
                    try {
                        appConnLoader = clazz.newInstance();
                    } catch (Exception e) {
                        logger.error(String.format("Can not initialize AppConnLoader class %s.", clazz.getSimpleName()), e);
                    }
                    logger.info("Use {} to load all AppConns.", clazz.getSimpleName());
                }
            }
        }
        return appConnLoader;
    }

}
