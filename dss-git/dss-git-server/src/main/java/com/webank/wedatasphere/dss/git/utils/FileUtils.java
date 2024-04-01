package com.webank.wedatasphere.dss.git.utils;


import com.webank.wedatasphere.dss.common.entity.BmlResource;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.common.exception.DSSRuntimeException;
import com.webank.wedatasphere.dss.common.service.BMLService;
import com.webank.wedatasphere.dss.common.utils.ZipHelper;
import com.webank.wedatasphere.dss.git.config.GitServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class FileUtils {
    private static final Logger logger = LoggerFactory.getLogger(DSSGitUtils.class);

    public static void addFiles(String projectName) {

        String filePath = GitServerConfig.GIT_SERVER_PATH.getValue() + "/" + projectName +"/file1.txt";
        List<String> lines = Arrays.asList("The first line", "The second line");

        try {
            removeFiles(filePath);

            // 确保父目录存在
            Files.createDirectories(Paths.get(filePath).getParent());

            // 向文件写入内容
            Files.write(Paths.get(filePath), lines, StandardOpenOption.CREATE);
        } catch (Exception e) {
            logger.error("add Files Failed, the reason is: ", e);
        }
    }

    public static void removeFiles (String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            logger.error("remove Files Failed, the reason is: ", e);
        }
    }

    public static void addDirectory (String directory) {
        String filePath = GitServerConfig.GIT_SERVER_PATH.getValue() + "/testGit1/file1.txt";
        List<String> lines = Arrays.asList("The first line", "The second line");

        try {
            removeFiles(filePath);

            // 确保父目录存在
            Files.createDirectories(Paths.get(filePath).getParent());

            // 向文件写入内容
            Files.write(Paths.get(filePath), lines, StandardOpenOption.CREATE);
        } catch (Exception e) {
            logger.error("add Files Failed, the reason is: ", e);
        }
    }

    public static void removeDirectory (String removeDirectoryPath) {
        try {
            Path dirToBeDeleted = Paths.get(removeDirectoryPath);
            // 使用Files.walk收集所有路径，然后按照逆序排序，确保文件/子文件夹在其父文件夹之前被删除
            Files.walk(dirToBeDeleted)
                    .sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path); // 删除每个路径，如果存在的话
                            logger.info("{} 删除成功", path);
                        } catch (IOException e) {
                            logger.info("无法删除路径: "+ path, e);
                        }
                    });
           logger.info("{} Directory and all its content deleted successfully.", removeDirectoryPath);
        } catch (IOException e) {
            logger.error("remove Directory Failed, the reason is: ", e);
        }
    }

    public static String unzipFile(String zipFile) {
        logger.info("-------=======================beginning to uznip testGit1=======================-------{}", zipFile);

        String longZipFilePath = "";
        try {
            longZipFilePath = ZipHelper.unzip(zipFile);
            File file = new File(zipFile);
            logger.info("开始删除目录 {}", zipFile);
            if (file.delete()){
                logger.info("结束删除目录 {} 成功", zipFile);
            }else{
                logger.info("删除目录 {} 失败", zipFile);
            }
        } catch (Exception e) {
            logger.error("unzip failed, the reason is ");
        }
        return longZipFilePath;
    }

    public static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());

        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }

        return destFile;
    }

    public static void removeAndUpdate (BMLService bmlService, String path,  BmlResource bmlResource, String username) {
        FileUtils.removeDirectory(GitServerConfig.GIT_SERVER_PATH.getValue() + "/" +path);
        try {
            downloadAndUnzipBMLResource(bmlService, path, bmlResource, username, GitServerConfig.GIT_SERVER_PATH.getValue());
        }catch (DSSErrorException e) {
            logger.error("unzip BML Resource Failed, the reason is : ", e);
        }
    }

    private static void downloadAndUnzipBMLResource(BMLService bmlService, String path,BmlResource bmlResource, String userName, String dirPath) throws DSSErrorException {
        //下载到本地处理
        String importFile=dirPath + File.separator + path + ".zip";
        logger.info("import zip file locate at {}",importFile);

        try{
            //下载压缩包
            bmlService.downloadToLocalPath(userName, bmlResource.getResourceId(), bmlResource.getVersion(), importFile);
        }catch (Exception e){
            logger.error("download failed, the reason is :", e);
            throw new DSSRuntimeException("upload file format error(导入包格式错误)");
        }
        //解压
        String unzipImportFile= FileUtils.unzipFile(importFile);
        logger.info("import unziped file locate at {}",unzipImportFile);
    }

    private static String readImportZipProjectName(String zipFilePath) throws IOException {
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
}
