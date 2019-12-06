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

import com.webank.wedatasphere.dss.appjoint.AppJoint;
import com.webank.wedatasphere.dss.appjoint.clazzloader.AppJointClassLoader;
import com.webank.wedatasphere.dss.appjoint.utils.AppJointUtils;
import com.webank.wedatasphere.dss.appjoint.utils.ExceptionHelper;
import com.webank.wedatasphere.linkis.common.exception.ErrorException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * created by cooperyang on 2019/11/8
 * Description:
 */
public class CommonAppJointLoader implements AppJointLoader{


    private static final Logger logger = LoggerFactory.getLogger(CommonAppJointLoader.class);

    private final Map<String, AppJoint> appJoints = new HashMap<>();


    /**
     * 用来存放每一个appjoint所用到的classloader，这样的话，每一个appjoint的classloader都是不一样的
     */
    private final Map<String, ClassLoader> classLoaders = new HashMap<>();



    /**
     *
     * @param baseUrl appjoint代理的外部系统的url
     * @param appJointName appJoint的名字
     * @param params 参数用来进行init
     * @return
     * 命名规范必须是  ${DSS_HOME}/appjoints/${appjointName}/
     */
    @Override
    public AppJoint getAppJoint(String baseUrl, String appJointName, Map<String, Object> params) throws Exception{
        synchronized (appJoints){
            if (appJoints.containsKey(appJointName)){
                return appJoints.get(appJointName);
            }
        }
        if (params == null) {
            params = new HashMap<String, Object>();
        }
        ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
        ClassLoader newClassLoader = null;
        synchronized (classLoaders){
            if (classLoaders.containsKey(appJointName)){
                newClassLoader = classLoaders.get(appJointName);
            }
        }
        URL classpathUrl = oldClassLoader.getResource("");
        logger.info("classpathUrl is {}", classpathUrl.getPath());
        if(null == classpathUrl){
            throw new ErrorException(70059, "classPathUrl is null");
        }



        String basePathUrlStr = classpathUrl.getPath() + ".." + File.separator + ".." + File.separator + AppJointLoader.APPJOINT_DIR_NAME;

        String libPathUrlStr =  basePathUrlStr + File.separator + appJointName +
                File.separator + AppJointLoader.LIB_NAME;
        String propertiesUrlStr = basePathUrlStr + File.separator + appJointName +
                File.separator + AppJointLoader.PROPERTIES_NAME;
        try{
            params.putAll(readFromProperties(propertiesUrlStr));
        }catch(IOException e){
            logger.warn("cannot get properties from {}", propertiesUrlStr, e);
        }
        URL finalURL = null;
        try {
            finalURL = new URL(AppJointLoader.FILE_SCHEMA + libPathUrlStr + "/*");
        } catch (MalformedURLException e) {
            ExceptionHelper.dealErrorException(70061, libPathUrlStr + " url is wrong", e);
        }
        List<URL> jars = AppJointUtils.getJarsUrlsOfPath(libPathUrlStr);
        if (newClassLoader == null){
            newClassLoader = new AppJointClassLoader(jars.toArray(new URL[100]) ,oldClassLoader);
        }
        synchronized (classLoaders){
            classLoaders.put(appJointName, newClassLoader);
        }
        Thread.currentThread().setContextClassLoader(newClassLoader);
        String fullClassName = AppJointUtils.getAppJointClassName(appJointName, libPathUrlStr, newClassLoader);
        Class clazz = null;
        try {
            clazz = newClassLoader.loadClass(fullClassName);
        } catch (ClassNotFoundException e) {
            ExceptionHelper.dealErrorException(70062, fullClassName + " class not found ", e);
        }
        if (clazz == null){
            Thread.currentThread().setContextClassLoader(oldClassLoader);
            return null;
        }else{
            AppJoint retAppjoint = (AppJoint) clazz.newInstance();
            if (StringUtils.isEmpty(baseUrl) && params.get("baseUrl") != null){
                baseUrl = params.get("baseUrl").toString();
            }
            retAppjoint.init(baseUrl, params);
            Thread.currentThread().setContextClassLoader(oldClassLoader);
            synchronized (appJoints){
                appJoints.put(appJointName, retAppjoint);
            }
            logger.info("appJointName is {},  retAppJoint is {}", appJointName, retAppjoint.getClass().toString());
            return retAppjoint;
        }
    }

    private Map<String, String> readFromProperties(String propertiesFile) throws IOException {
        Properties properties = new Properties();
        BufferedReader reader = new BufferedReader(new FileReader(propertiesFile));
        properties.load(reader);
        Map<String, String> map = new HashMap<String, String>((Map)properties);
        return map;
    }




}
