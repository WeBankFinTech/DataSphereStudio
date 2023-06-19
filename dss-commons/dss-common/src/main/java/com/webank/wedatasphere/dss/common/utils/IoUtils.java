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

package com.webank.wedatasphere.dss.common.utils;

import com.webank.wedatasphere.dss.common.conf.DSSCommonConf;
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
 * IO通用类。提供对一次IO操作的临时目录创建、文件创建、IO流创建功能。
 * 另外，提供一份IO properties记录本次IO的上下文信息，并以IO.properties文件的形式存放到临时目录下。通过提供的方法，可以方便的写入properties。
 */
public class IoUtils {

    private static Logger logger = LoggerFactory.getLogger(IoUtils.class);
    private static final String DATE_FORMAT = "yyyyMMddHHmmssSSS";
    private static final String DEFAULT_IO_FILE_NAME = "IO.properties";

    /**
     * 生成一个基础目录，这个目录用于某次任务的临时文件目录
     * @param userName 用户名
     * @param projectName 项目名
     * @param subDir 自定义的子目录名
     * @return 基础目录的全路径
     */
    public static String generateIOPath(String userName,String projectName,String subDir) {
        String baseUrl = DSSCommonConf.DSS_EXPORT_URL.getValue();
        String dataStr = new SimpleDateFormat(DATE_FORMAT).format(new Date());
        return addFileSeparator(baseUrl, dataStr, userName, projectName, subDir);
    }

    private static String addFileSeparator(String... str) {
        return Arrays.stream(str).reduce((a, b) -> a + File.separator + b).orElse("");
    }

    /**
     * 根据指定的文件路径，创建一个输出流
     * @param path 指定的文件路径
     * @return 输出流
     * @throws IOException 如果有io异常的话，直接抛出
     */
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
    /**
     * 根据指定的文件路径，创建一个输入流
     * @param path 指定的文件路径
     * @return 输入流
     * @throws IOException 如果有io异常的话，直接抛出
     */
    public static InputStream generateInputInputStream(String path)throws IOException{
        return new FileInputStream(path);
    }

    /**
     * 标记io类型到IO properties中
     * @param ioType
     * @param basePath
     * @throws IOException
     */
    public static void generateIOType(IOType ioType, String basePath) throws IOException {
        generateIOProperties("type",ioType.name(),basePath);
    }

    /**
     * 写一个properties键值对到IO properties中
     * @param key 键
     * @param value 值
     * @param basePath 目标目录
     * @throws IOException 有io异常，直接抛出
     */
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

    /**
     * 读取一个IO properties中的值
     * @param key 要读取的properties
     * @param basepath 读取的目录
     * @return 读取的值
     */
    public static String readIOProperties(String key,String basepath) throws IOException {
        try(FileInputStream inputStream = new FileInputStream(basepath + File.separator + DEFAULT_IO_FILE_NAME)){
            Properties properties = new Properties();
            properties.load(inputStream);
            return properties.get(key).toString();
        }

    }
    /**
     * 标记当前环境label值到IO properties中
     * @param basePath
     * @throws IOException
     */
    public static void generateIOEnv(String basePath) throws IOException {
        generateIOProperties("env",getDSSServerEnv(),basePath);
    }

    /**
     * 获取当前服务器的环境
     */
    public static String getDSSServerEnv(){
        //dssserverEnv 是当前dss服务启动的env环境
        return DSSCommonConf.DSS_IO_ENV.getValue();
    }
}
