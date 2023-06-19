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

package com.webank.wedatasphere.dss.appconn.loader.utils;

import com.webank.wedatasphere.dss.appconn.core.AppConn;
import com.webank.wedatasphere.dss.appconn.loader.exception.NoSuchAppConnException;
import com.webank.wedatasphere.dss.common.utils.DSSCommonUtils;
import org.apache.linkis.common.conf.CommonVars;
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
import org.apache.commons.lang.StringUtils;

public class AppConnUtils {

    public static final String JAR_SUF_NAME = ".jar";

    public static final String APPCONN_DIR_NAME = "dss-appconns";

    public static final CommonVars<String> APPCONN_HOME_PATH = CommonVars.apply("wds.dss.appconn.home.path",
        new File(DSSCommonUtils.DSS_HOME.getValue(), APPCONN_DIR_NAME).getPath());

    public static String getAppConnHomePath() {
       return APPCONN_HOME_PATH.acquireNew();
    }

    /**
     * Obtain the fully qualified name of the appconn to be instantiated.
     * */
    public static String getAppConnClassName(String appConnName, String libPath,
                                             ClassLoader classLoader) throws NoSuchAppConnException, IOException {
        //1.Get all the jar packages under the directory
        List<String> jars = getJarsOfPath(libPath);
        //2.Get the subclass of appconn from all jars
        for (String jar : jars) {
            for (String clazzName : getClassNameFrom(jar)) {
                //3.Then find the subclass of appconn in the corresponding jar package
                if (isChildClass(clazzName, AppConn.class, classLoader)) {
                    return clazzName;
                }
            }
        }
        throw new NoSuchAppConnException("Cannot find a appConn instance for AppConn " + appConnName + " in lib path " + libPath);
    }

    public static List<String> getJarsOfPath(String path) {
        File file = new File(path);
        List<String> jars = new ArrayList<>();
        if (file.listFiles() != null) {
            for (File f : file.listFiles()) {
                // only search from dss-xxxxx.jar.
                if (!f.isDirectory() && f.getName().endsWith(JAR_SUF_NAME) && f.getName().startsWith("dss-")) {
                    jars.add(f.getPath());
                }
            }
        }
        return jars;
    }


    public static List<URL> getJarsUrlsOfPath(String path) throws MalformedURLException {
        File file = new File(path);
        List<URL> jars = new ArrayList<>();
        if (file.listFiles() != null) {
            for (File f : file.listFiles()) {
                if (!f.isDirectory() && f.getName().endsWith(JAR_SUF_NAME)) {
                    jars.add(f.toURI().toURL());
                }
            }
        }
        return jars;
    }


    /**
     * Then look for the subclass of appconn in the corresponding jar package,
     * and read all the class file names from the jar package.
     */
    private static List<String> getClassNameFrom(String jarName) throws IOException {
        List<String> fileList = new ArrayList<>();
        try (JarFile jarFile = new JarFile(new File(jarName))) {
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
            return false;
        }
        return parentClazz.isAssignableFrom(clazz);
    }

}
