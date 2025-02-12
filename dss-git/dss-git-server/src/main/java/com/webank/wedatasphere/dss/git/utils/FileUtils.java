package com.webank.wedatasphere.dss.git.utils;


import com.webank.wedatasphere.dss.common.entity.BmlResource;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.common.exception.DSSRuntimeException;
import com.webank.wedatasphere.dss.common.service.BMLService;
import com.webank.wedatasphere.dss.common.utils.ZipHelper;
import com.webank.wedatasphere.dss.git.common.protocol.config.GitServerConfig;
import com.webank.wedatasphere.dss.git.common.protocol.constant.GitConstant;
import com.webank.wedatasphere.dss.git.constant.DSSGitConstant;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class FileUtils {
    private static final Logger logger = LoggerFactory.getLogger(DSSGitUtils.class);

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

    public static void removeDirectory (String removeDirectoryPath) {
        try {
            File file = new File(removeDirectoryPath);
            if (!file.exists()) {
                logger.info("file {} not exist", removeDirectoryPath);
                return;
            }
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


    public static void removeFlowNode(String path, String projectName, Long workspaceId, String gitUser) {
        // 删除node节点
        String flowNodePath = DSSGitUtils.generateGitPrePath(projectName, workspaceId, gitUser) + File.separator + path;
        // 1.删除对应文件代码
        removeDirectory(flowNodePath);
    }

    public static void removeProject(String path, Long workspaceId, String gitUser) {
        // 删除node节点
        String projectPath =  DSSGitConstant.GIT_PATH_PRE + workspaceId + File.separator + gitUser + File.separator +  path ;
        // 1.删除项目
        removeDirectory(projectPath);
    }

    public static void downloadBMLResource(BMLService bmlService, String path,  BmlResource bmlResource, String username, Long workspaceId, String gitUser) {
        //下载到本地处理
        String importFile = DSSGitConstant.GIT_PATH_PRE + workspaceId + File.separator + gitUser + File.separator + path + ".zip";
        logger.info("import zip file locate at {}",importFile);

        try{
            // 确保父目录存在
            Files.createDirectories(Paths.get(importFile).getParent());
            //下载压缩包
            bmlService.downloadToLocalPath(username, bmlResource.getResourceId(), bmlResource.getVersion(), importFile);
        }catch (Exception e){
            logger.error("download failed, the reason is :", e);
            throw new DSSRuntimeException("upload file format error(导入包格式错误)");
        }
    }

    public static void unzipBMLResource(String path, Long workspaceId, String gitUser) throws DSSErrorException {
        //下载到本地处理
        String dirPath = DSSGitConstant.GIT_PATH_PRE + workspaceId + File.separator + gitUser ;
        String importFile= dirPath + File.separator +  path + ".zip";
        //解压
        ZipHelper.unzipFile(importFile, dirPath, true);
        logger.info("import unzip file locate at {}",importFile);
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

    public static String normalizePath(String path) {
        path = path.trim();
        while(path.startsWith(File.separator)) {
            path = path.substring(File.separator.length());
        }
        while(path.endsWith(File.separator)) {
            path = path.substring(0, path.length()-File.separator.length());
        }
        
        return path;
    }

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
        list.add("unzip");
        list.add("-o");
        String longZipFilePath = dirPath.replace(".zip","");
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
                logger.info("开始删除目录 {}", file);
                if (file.delete()){
                    logger.info("结束删除目录 {} 成功", file);
                }else{
                    logger.info("删除目录 {} 失败", file);
                }
            }
        }catch(final Exception e){
            logger.error( file + " 解压缩 zip 文件失败, reason: ", e);
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

    public static void renameFile(String oldFileName, String fileName) {
        File oldFile = new File(oldFileName);
        File file = new File(fileName);
        // 检查原文件夹是否存在
        if (oldFile.exists()) {
            // 尝试重命名文件夹
            if (oldFile.renameTo(file)) {
                logger.info("Folder renamed successfully.");
            } else {
                logger.error("Failed to rename folder.");
            }
        } else {
            logger.error("Folder does not exist.");
        }
    }

    public static List<String> getLocalProjectName(Long workspaceId, String gitUser) throws IOException {
        String path = DSSGitConstant.GIT_PATH_PRE + workspaceId + File.separator + gitUser + File.separator;
        Path dir = Paths.get(path);
        List<String> localProjectList = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for (Path entry : stream) {
                // 获取基本文件属性
                BasicFileAttributes attrs = Files.readAttributes(entry, BasicFileAttributes.class);
                if (attrs.isDirectory()) {
                    localProjectList.add(entry.getFileName().toString());
                }
            }
        } catch (IOException | DirectoryIteratorException e) {
            logger.error("get Local Project Failed", e);
            throw e;
        }
        return localProjectList;
    }
}
