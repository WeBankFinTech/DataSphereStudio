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

package com.webank.wedatasphere.dss.standard.common.utils;

import com.webank.wedatasphere.dss.common.utils.ClassUtils.ClassHelper;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * This class is defined for AppConn jar, if some classes in AppConn want to load a class in AppConn jar.
 */
public class AppStandardClassUtils extends ClassHelper {

    private static final Map<String, AppStandardClassUtils> INSTANCES = new ConcurrentHashMap<>();
    private static final Map<String, ClassLoader> CLASS_LOADER_MAP = new ConcurrentHashMap<>();

    private static final Logger LOGGER = LoggerFactory.getLogger(AppStandardClassUtils.class);

    public static ClassLoader refreshClassloader(String appConnName, Supplier<ClassLoader> createClassLoader) {
        if(CLASS_LOADER_MAP.containsKey(appConnName)) {
            synchronized (AppStandardClassUtils.class) {
                if(CLASS_LOADER_MAP.containsKey(appConnName)) {
                    CLASS_LOADER_MAP.remove(appConnName);
                    INSTANCES.remove(appConnName);
                }
            }
        }
        return getClassLoader(appConnName, createClassLoader);
    }

    public static ClassLoader getClassLoader(String appConnName, Supplier<ClassLoader> createClassLoader) {
        if(!CLASS_LOADER_MAP.containsKey(appConnName)) {
            synchronized (AppStandardClassUtils.class) {
                if(!CLASS_LOADER_MAP.containsKey(appConnName)) {
                    CLASS_LOADER_MAP.put(appConnName, createClassLoader.get());
                    LOGGER.info("Has stored {} ClassLoader.", CLASS_LOADER_MAP.size());
                }
            }
        }
        return CLASS_LOADER_MAP.get(appConnName);
    }


    public static AppStandardClassUtils getInstance(String appConnName) {
        if(!INSTANCES.containsKey(appConnName)) {
            synchronized (AppStandardClassUtils.class) {
                if(!INSTANCES.containsKey(appConnName)) {
                    INSTANCES.put(appConnName, new AppStandardClassUtils(appConnName));
                    LOGGER.info("Has stored {} AppStandardClassUtils.", INSTANCES.size());
                }
            }
        }
        return INSTANCES.get(appConnName);
    }

    private volatile Reflections reflection;
    private String appConnName;

    private AppStandardClassUtils(String appConnName) {
        this.appConnName = appConnName;
    }

    @Override
    protected Reflections getReflections(Class<?> clazz) {
        if(reflection == null) {
            synchronized (this) {
                if(reflection == null) {
                    ClassLoader classLoader = clazz.getClassLoader();
                    reflection = new Reflections("com.webank.wedatasphere.dss", CLASS_LOADER_MAP.get(appConnName), classLoader);
                }
            }
        }
        return reflection;
    }

}
