/*
 * Copyright 2019 WeBank
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
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

package com.webank.wedatasphere.dss.appjoint.loader;

import com.webank.wedatasphere.dss.appjoint.conf.AppJointLoaderConf;
import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * created by cooperyang on 2019/11/8
 * Description:
 */
class AppJointLoaderFactory {


    private static Class<? extends AppJointLoader> clazz = CommonAppJointLoader.class;
    private static AppJointLoader appJointLoader = null;
    private static final Logger logger = LoggerFactory.getLogger(AppJointLoaderFactory.class);


     static AppJointLoader getAppJointLoader(){
        if (appJointLoader == null){
            synchronized (AppJointLoaderFactory.class){
                if (appJointLoader == null){
                    String className = AppJointLoaderConf.CLASS_LOADER_CLASS_NAME().getValue();
                    if (clazz == CommonAppJointLoader.class && StringUtils.isNotBlank(className)){
                        try{
                            clazz = ClassUtils.getClass(className);
                        }catch(ClassNotFoundException e){
                            logger.warn("can not get class {}", className, e);
                        }
                        try {
                            appJointLoader = clazz.newInstance();
                        } catch (Exception e) {
                            logger.error("can not initialize class", e);
                        }
                    }else{
                        try {
                            appJointLoader = clazz.newInstance();
                        } catch (Exception e) {
                            logger.error("can not initialize class", e);
                        }
                    }
                }
            }
        }
        return appJointLoader;
    }
}
