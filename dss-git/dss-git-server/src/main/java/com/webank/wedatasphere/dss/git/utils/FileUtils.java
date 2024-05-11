package com.webank.wedatasphere.dss.git.utils;


import com.webank.wedatasphere.dss.common.entity.BmlResource;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.common.exception.DSSRuntimeException;
import com.webank.wedatasphere.dss.common.service.BMLService;
import com.webank.wedatasphere.dss.git.common.protocol.config.GitServerConfig;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.util.IOUtils;
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

        String filePath = "/" + FileUtils.normalizePath(GitServerConfig.GIT_SERVER_PATH.getValue()) + "/" + projectName +"/file1.txt";
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
        String filePath = "/" + FileUtils.normalizePath(GitServerConfig.GIT_SERVER_PATH.getValue()) + "/testGit1/file1.txt";
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

    public static BmlResource uploadResourceToBML(BMLService bmlService, String userName, String content, String fileName, String projectName) {
        Map<String, Object> bmlReturnMap = bmlService.upload(userName, content, fileName, projectName);

        BmlResource bmlResource = new BmlResource();
        bmlResource.setResourceId(bmlReturnMap.get("resourceId").toString());
        bmlResource.setVersion(bmlReturnMap.get("version").toString());

        return bmlResource;
    }

    public static String unzipFile(String zipFile) {
        logger.info("-------=======================beginning to uznip testGit1=======================-------{}", zipFile);

        String longZipFilePath = "";
        try {
            longZipFilePath = unzip(zipFile, true);
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

    public static void removeFlowNode(String path, String projectName) {
        // 删除node节点
        String flowNodePathPre =  FileUtils.normalizePath(GitServerConfig.GIT_SERVER_PATH.getValue()) + File.separator + projectName ;
        String flowNodePath = File.separator + FileUtils.normalizePath(flowNodePathPre) + File.separator + path;
        String flowNodeMetaPath = File.separator + FileUtils.normalizePath(flowNodePathPre) + File.separator + FileUtils.normalizePath(GitServerConfig.GIT_SERVER_META_PATH.getValue()) + File.separator + path;
        // 1.删除工作流节点代码
        removeDirectory(flowNodePath);
        // 2. 删除工作流节点对应.metaConf文件
        removeDirectory(flowNodeMetaPath);
    }

    public static void removeProject(String path) {
        // 删除node节点
        String projectPath =  File.separator + FileUtils.normalizePath(GitServerConfig.GIT_SERVER_PATH.getValue()) + File.separator + path ;
        // 1.删除项目
        removeDirectory(projectPath);
    }

    public static void update(BMLService bmlService, String path,  BmlResource bmlResource, String username) {
        try {
            downloadAndUnzipBMLResource(bmlService, path, bmlResource, username, "/" + FileUtils.normalizePath(GitServerConfig.GIT_SERVER_PATH.getValue()));
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
}
