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

package com.webank.wedatasphere.dss.common.utils;

import com.webank.wedatasphere.dss.common.conf.DSSCommonConf;
import com.webank.wedatasphere.dss.common.entity.IOEnv;
import com.webank.wedatasphere.dss.common.entity.IOType;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;

/**
 * @author allenlliu
 * @version 2.0.0
 * @date 2020/03/11 11:18 AM
 */
public class IoUtils {

    private static Logger logger = LoggerFactory.getLogger(IoUtils.class);
    private static final String DATE_FORMAT = "yyyyMMddHHmmssSSS";
    private static final String DEFAULT_IO_FILE_NAME = "IO.properties";

    public static String generateIOPath(String userName,String projectName,String subDir) {
        String baseUrl = DSSCommonConf.DSS_EXPORT_URL.getValue();
        String dataStr = new SimpleDateFormat(DATE_FORMAT).format(new Date());
        return addFileSeparator(baseUrl, dataStr, userName, projectName, subDir);
    }

    private static String addFileSeparator(String... str) {
        return Arrays.stream(str).reduce((a, b) -> a + File.separator + b).orElse("");
    }

    public static OutputStream generateExportOutputStream(String path) throws IOException {
        File file = new File(path);
        if (!file.getParentFile().exists()) {
            FileUtils.forceMkdir(file.getParentFile());
        }
        if (file.exists()) {
            logger.warn(String.format("%s is exist,delete it", path));
            file.delete();
        }
        file.createNewFile();
        return FileUtils.openOutputStream(file, true);
    }

    public static InputStream generateInputInputStream(String path)throws IOException{
        return new FileInputStream(path);
    }

    public static void generateIOType(IOType ioType, String basePath) throws IOException {
        generateIOProperties("type",ioType.name(),basePath);
    }

    public static void generateIOProperties(String key,String value,String basePath) throws IOException {
        Properties properties = new Properties();
        properties.setProperty(key,value);

        File file = new File(basePath + File.separator + DEFAULT_IO_FILE_NAME);
        if (!file.getParentFile().exists()) {
            FileUtils.forceMkdir(file.getParentFile());
        }
        if(!file.exists()){
            file.createNewFile();
        }
        try(FileOutputStream fileOutputStream = FileUtils.openOutputStream(file, true)) {
            properties.store(fileOutputStream,"");
        }

    }

    public static String readIOProperties(String key,String basepath) throws IOException {
        try(FileInputStream inputStream = new FileInputStream(basepath + File.separator + DEFAULT_IO_FILE_NAME)){
            Properties properties = new Properties();
            properties.load(inputStream);
            return properties.get(key).toString();
        }

    }

    public static IOType readIOType(String basepath)throws IOException{
        return IOType.valueOf(readIOProperties("type",basepath));
    }

    public static void generateIOEnv(String basePath) throws IOException {
        generateIOProperties("env",getDSSServerEnv().name(),basePath);
    }

    public static IOEnv readIOEnv(String basePath) throws IOException {
        return IOEnv.valueOf(readIOProperties("env",basePath));
    }

    public static IOEnv getDSSServerEnv(){
        //dssserverEnv 是当前dsss服务启动的env环境
        return IOEnv.valueOf(DSSCommonConf.DSS_IO_ENV.getValue());
    }

    public static String addVersion(String version){
        String num = String.valueOf(Integer.valueOf(version.substring(1)) + 1);
        int length = num.length();
        return version.substring(0,version.length() - length) + num;
    }

    public static String subVersion(String version){
        String num = String.valueOf(Integer.valueOf(version.substring(1)) - 1);
        int length = num.length();
        return version.substring(0,version.length() - length) + num;
    }

}
