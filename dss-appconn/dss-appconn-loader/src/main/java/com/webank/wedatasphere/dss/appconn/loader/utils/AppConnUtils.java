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

package com.webank.wedatasphere.dss.appconn.loader.utils;

import com.webank.wedatasphere.dss.appconn.core.AppConn;
import com.webank.wedatasphere.dss.appconn.loader.exception.NoSuchAppConnException;
import com.webank.wedatasphere.dss.appconn.loader.loader.AppConnLoader;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class AppConnUtils {

    private static final Logger logger = LoggerFactory.getLogger(AppConnUtils.class);

    private static Class PARENT_CLASS = AppConn.class;


    /**
     * 获得需要实例化的AppConn的全限定名
     * */
    public static String getAppConnClassName(String appConnName, String libPath,
                                             ClassLoader classLoader) throws NoSuchAppConnException {
        //1.获取目录下面所有的jar包
        List<String> jars = getJarsOfPath(libPath);
        //2.从所有的jar中获取到AppConn的子类
        for (String jar : jars) {
            for (String clazzName : getClassNameFrom(jar)) {
                //3. 再在对应的jar包中寻找是AppConn的子类
                if (isChildClass(clazzName, PARENT_CLASS, classLoader)) {
                    return clazzName;
                }
            }
        }
        throw new NoSuchAppConnException(appConnName + " does not exist");
    }

    public static List<String> getJarsOfPath(String path) {
        File file = new File(path);
        List<String> jars = new ArrayList<>();
        if (file.listFiles() != null) {
            for (File f : file.listFiles()) {
                // dss-xxxxx.jar
                if (!f.isDirectory() && f.getName().endsWith(AppConnLoader.JAR_SUF_NAME) && f.getName().startsWith("dss")) {
                    jars.add(f.getPath());
                }
            }
        }
        return jars;
    }


    public static List<URL> getJarsUrlsOfPath(String path) {
        File file = new File(path);
        List<URL> jars = new ArrayList<>();
        if (file.listFiles() != null) {
            for (File f : file.listFiles()) {
                if (!f.isDirectory() && f.getName().endsWith(AppConnLoader.JAR_SUF_NAME)) {
                    try {
                        jars.add(f.toURI().toURL());
                    } catch (MalformedURLException e) {
                        logger.warn("url {} cannot be added", AppConnLoader.FILE_SCHEMA + f.getPath());
                    }
                }
            }
        }
        return jars;
    }


    /**
     * 从jar包读取所有的class文件名
     */
    private static List<String> getClassNameFrom(String jarName) {
        List<String> fileList = new ArrayList<>();
        try {
            JarFile jarFile = new JarFile(new File(jarName));
            Enumeration<JarEntry> en = jarFile.entries();
            while (en.hasMoreElements()) {
                String name1 = en.nextElement().getName();
                if (!name1.endsWith(".class")) {
                    continue;
                }
                String name2 = name1.substring(0, name1.lastIndexOf(".class"));
                String name3 = name2.replaceAll("/", ".");
                fileList.add(name3);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return fileList;
    }


    private static boolean isChildClass(String className, Class parentClazz, ClassLoader classLoader) {
        if (StringUtils.isEmpty(className)) {
            return false;
        }
        Class clazz = null;
        try {
            clazz = classLoader.loadClass(className);
            //忽略抽象类和接口
            if (Modifier.isAbstract(clazz.getModifiers())) {
                return false;
            }
            if (Modifier.isInterface(clazz.getModifiers())) {
                return false;
            }
        } catch (Throwable t) {
            logger.error("className {} can not be instanced", className, t);
            return false;
        }
        return parentClazz.isAssignableFrom(clazz);
    }

}
