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

import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

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
        String shortPath =new File(dirPath).getName();
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
    @Deprecated
    public static String unzip(String dirPath)throws DSSErrorException{
        return unzip(dirPath, false);
    }
    /**
     * 解压一个zip文件
     * @param zipFilePath zip文件的全路径名
     * @destDirectory 解压到的目的地
     * @return 解压后的文件夹名
     * @throws DSSErrorException 解压出现异常
     */
    public static void unzipFile(String zipFilePath, String destDirectory, boolean deleteOriginZip) throws DSSErrorException {
        File destDir = new File(destDirectory);
        // 使用 try-with-resources 确保资源被自动关闭
        File zipFile = new File(zipFilePath);
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFilePath))) {
            byte[] buffer = new byte[1024];
            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                File newFile = new File(destDir, zipEntry.getName());
                if (zipEntry.isDirectory()) {
                    newFile.mkdirs();
                } else {
                    // 为解压后的文件创建文件输出流
                    try (FileOutputStream fos = new FileOutputStream(newFile)) {
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                }
                zipEntry = zis.getNextEntry();
            }
            zis.closeEntry();
        }catch(final Exception e){
            logger.error(" 解压缩 zip 文件失败, reason: ", e);
            DSSErrorException exception = new DSSErrorException(90009, "unzip" + zipFilePath + " to " + destDirectory + "zip file failed");
            exception.initCause(e);
            throw exception;
        }
        // 如果指定删除原zip文件
        if (deleteOriginZip) {
            new File(zipFilePath).delete();
        }
    }
    @Deprecated
    public static String unzip(String dirPath,boolean deleteOriginZip)throws DSSErrorException {
        File file = new File(dirPath);
        if(!file.exists()){
            logger.error("{} 不存在, 不能解压zip文件", dirPath);
            throw new DSSErrorException(90001,dirPath + " does not exist, can not unzip");
        }
        //先用简单的方法，调用新进程进行压缩
        String[] strArr = dirPath.split(File.separator);
        String shortPath = new File(dirPath).getName();
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
            if(deleteOriginZip){
                file.delete();
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
    public static String readImportZipProjectName(String zipFilePath) throws IOException {
        try(ZipFile zipFile =new ZipFile(zipFilePath)){
            Enumeration<? extends ZipEntry> entries =zipFile.entries();
            if(entries.hasMoreElements()){
                String projectName=entries.nextElement().getName();
                while (projectName.endsWith(File.separator)){
                    projectName = projectName.substring(0, projectName.length() - 1);
                }
                return projectName;
            }
        }
        throw new IOException();
    }

    public static boolean deleteDir(File dir) {
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
