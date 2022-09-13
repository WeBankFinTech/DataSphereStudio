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



import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ZipHelper {

    private static final Logger logger = LoggerFactory.getLogger(ZipHelper.class);

    private static final String ZIP_CMD = "zip";
    private static final String UN_ZIP_CMD = "unzip";
    private static final String RECURSIVE = "-r";
    private static final String ZIP_TYPE = ".zip";

    /**
     *  ZipHelper可以将传入的path进行打包，打包之后删除path目录
     * @param dirPath 需要打包的project路径,绝对路径
     * @return 打包之后的zip包全路径
     */
    public static String zip(String dirPath) throws DSSErrorException {
        return zip(dirPath, true);
    }

    /**
     *  ZipHelper可以将传入的path进行打包
     * @param dirPath 需要打包的project路径,绝对路径
     * @param deleteOriginDir 打包之后，是否要删除path目录
     * @return 打包之后的zip包全路径
     */
    public static String zip(String dirPath, boolean deleteOriginDir)throws DSSErrorException {
        if(dirPath.endsWith(File.separator)) {
            dirPath = dirPath.substring(0, dirPath.lastIndexOf(File.separator));
        }
        if(!FileHelper.checkDirExists(dirPath)){
            logger.error("{} 不存在, 不能创建zip文件", dirPath);
            throw new DSSErrorException(90001,dirPath + " does not exist, can not zip");
        }
        //先用简单的方法，调用新进程进行压缩
        String[] strArr = dirPath.split(File.separator);
        String shortPath = strArr[strArr.length - 1];
        String workPath = dirPath.substring(0, dirPath.length() - shortPath.length() - 1);
        List<String> list = new ArrayList<>();
        list.add(ZIP_CMD);
        list.add(RECURSIVE);
        String zipFilePath = shortPath + ZIP_TYPE;
        String longZipFilePath = dirPath + ZIP_TYPE;
        list.add(zipFilePath);
        list.add(shortPath);
        ProcessBuilder processBuilder = new ProcessBuilder(list);
        processBuilder.redirectErrorStream(true);
        processBuilder.directory(new File(workPath));
        BufferedReader infoReader = null;
        BufferedReader errorReader = null;
        try{
            Process process = processBuilder.start();
            infoReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String infoLine = null;
            while((infoLine = infoReader.readLine()) != null){
                logger.info("process output: {} ", infoLine);
            }
            String errorLine = null;
            StringBuilder errMsg = new StringBuilder();
            while((errorLine = errorReader.readLine()) != null){
                if (StringUtils.isNotEmpty(errorLine)){
                    errMsg.append(errorLine).append("\n");
                }
                logger.error("process error: {} ", errorLine);
            }
            int exitCode = process.waitFor();
            if (exitCode != 0){
                throw new DSSErrorException(90002,errMsg.toString());
            }
        }catch(final Exception e){
            logger.error("{} 压缩成 zip 文件失败, reason: ", e);
            DSSErrorException exception = new DSSErrorException(90003,dirPath + " to zip file failed");
            exception.initCause(e);
            throw exception;
        } finally {
            //删掉整个目录
            IOUtils.closeQuietly(infoReader);
            IOUtils.closeQuietly(errorReader);
            if(deleteOriginDir) {
                File file = new File(dirPath);
                logger.info("生成zip文件{}",longZipFilePath);
                logger.info("开始删除目录 {}", dirPath);
                if (deleteDir(file)){
                    logger.info("结束删除目录 {} 成功", dirPath);
                }else{
                    logger.info("删除目录 {} 失败", dirPath);
                }
            }
        }
        return longZipFilePath;
    }

    /**
     * 解压一个zip文件
     * @param dirPath zip文件的全路径名
     * @return 解压后的文件夹名
     * @throws DSSErrorException 解压出现异常
     */

    public static String unzip(String dirPath)throws DSSErrorException {
        File file = new File(dirPath);
        if(!file.exists()){
            logger.error("{} 不存在, 不能解压zip文件", dirPath);
            throw new DSSErrorException(90001,dirPath + " does not exist, can not unzip");
        }
        //先用简单的方法，调用新进程进行压缩
        String[] strArr = dirPath.split(File.separator);
        String shortPath = strArr[strArr.length - 1];
        String workPath = dirPath.substring(0, dirPath.length() - shortPath.length() - 1);
        List<String> list = new ArrayList<>();
        list.add(UN_ZIP_CMD);
        String longZipFilePath = dirPath.replace(ZIP_TYPE,"");
        list.add(shortPath);
        ProcessBuilder processBuilder = new ProcessBuilder(list);
        processBuilder.redirectErrorStream(true);
        processBuilder.directory(new File(workPath));
        BufferedReader infoReader = null;
        BufferedReader errorReader = null;
        try{
            Process process = processBuilder.start();
            infoReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String infoLine = null;
            while((infoLine = infoReader.readLine()) != null){
                logger.info("process output: {} ", infoLine);
            }
            String errorLine = null;
            StringBuilder errMsg = new StringBuilder();
            while((errorLine = errorReader.readLine()) != null){
                if (StringUtils.isNotEmpty(errorLine)){
                    errMsg.append(errorLine).append("\n");
                }
                logger.error("process error: {} ", errorLine);
            }
            int exitCode = process.waitFor();
            if (exitCode != 0){
                throw new DSSErrorException(90007,errMsg.toString());
            }
        }catch(final Exception e){
            logger.error("{} 解压缩 zip 文件失败, reason: ", e);
            DSSErrorException exception = new DSSErrorException(90009,dirPath + " to zip file failed");
            exception.initCause(e);
            throw exception;
        } finally {

            logger.info("生成解压目录{}", longZipFilePath);
            IOUtils.closeQuietly(infoReader);
            IOUtils.closeQuietly(errorReader);
        }
        return longZipFilePath;
    }

    private static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            if (children != null && children.length > 0){
                for (String s : children) {
                    boolean success = deleteDir(new File(dir, s));
                    if (!success) {
                        return false;
                    }
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }
}
