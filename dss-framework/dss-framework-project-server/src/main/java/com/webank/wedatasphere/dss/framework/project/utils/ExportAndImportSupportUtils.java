package com.webank.wedatasphere.dss.framework.project.utils;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.common.utils.IoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 支持导入导出的工具类
 * Author: xlinliu
 * Date: 2024/4/19
 */
public class ExportAndImportSupportUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExportAndImportSupportUtils.class);
    private static final String EXTERNAL_RESOURCES = ".external-resources";
    private static final String META_CONF = ".metaConf";

    /**
     * 分离一个工作流导出包，得到各个工作流的导出包
     * @param sourceDir 原始导出包的的路劲，路径下存在至少一个项目名，项目下有两个以上的工作流
     * @param projectName 项目名
     * @param targetDir 指定分离后的工作流导出包位置路径。导出后，这个路径下会出现以各工作流名命名的文件夹，这些文件夹下都只有一个工作流导出包。
     * @return 分离后的导出包路径，key是工作流名，value是一个项目文件夹。此时项目文件夹里只有一个工作流。
     */
    public static Map<String,Path> separateFlows(String sourceDir, String projectName, String targetDir) {

        // 检测工作流目录
        List<String> workflowNames = detectWorkflowNames(new File(sourceDir, projectName));
        Map<String, Path> workFlowProjectPaths = new HashMap<>(workflowNames.size());
        for (String workflow : workflowNames) {
            try {
                // 创建新的工作流项目目录
                String newWorkflowDir = IoUtils.addFileSeparator(targetDir,workflow,projectName) ;
                new File(newWorkflowDir).mkdirs();

                // 复制项目中的所有文件到新的工作流项目目录
                copyProjectFiles(new File(sourceDir, projectName), newWorkflowDir);

                // 复制 EXTERNAL_RESOURCES
                copyDirectory(new File(sourceDir, projectName + File.separator + EXTERNAL_RESOURCES).getPath(), newWorkflowDir + File.separator + EXTERNAL_RESOURCES);

                // 复制 META_CONF，仅包括指定工作流的子目录
                copyWorkflowMetaConf(new File(sourceDir, projectName + File.separator + META_CONF).getPath(), newWorkflowDir + File.separator + META_CONF, workflow);

                // 复制工作流的代码目录
                String workflowDir = new File(sourceDir, projectName + File.separator + workflow).getPath();
                copyDirectory(workflowDir, newWorkflowDir + File.separator + workflow);
                workFlowProjectPaths.put(workflow, Paths.get(newWorkflowDir));
            } catch (IOException e) {
                LOGGER.error("Error while copying workflow " + workflow + ": " + e);
                throw new DSSErrorException(70001, "复制工作流失败，原因为:" + e);
            }


        }

        return workFlowProjectPaths;
    }

    // 复制整个目录
    private static void copyDirectory(String sourceDirectory, String destinationDirectory) throws IOException {
        File file = new File(sourceDirectory);
        if (!file.exists()) {
            return ;
        }
        Files.walk(Paths.get(sourceDirectory))
                .forEach(source -> {
                    Path destination = Paths.get(destinationDirectory, source.toString().substring(sourceDirectory.length()));
                    try {
                        if (Files.isDirectory(source)) {
                            Files.createDirectories(destination);
                        } else {
                            Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }

    // 检测工作流目录
    private static List<String> detectWorkflowNames(File projectDir) {
        List<String> workflowNames = new ArrayList<>();
        File[] projectSubFolders = projectDir.listFiles();
        if (projectSubFolders != null) {
            for (File file : projectSubFolders) {
                if (file.isDirectory() && !file.getName().equals(EXTERNAL_RESOURCES) && !file.getName().equals(META_CONF)) {
                    workflowNames.add(file.getName());
                }
            }
        }
        return workflowNames;
    }

    // 复制项目目录中的所有文件
    private static void copyProjectFiles(File sourceDir, String destinationDir) throws IOException {
        File[] files = sourceDir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) { // 确保只复制文件
                    Path destination = Paths.get(destinationDir, file.getName());
                    Files.copy(file.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
    }

    // 专门为 META_CONF 文件夹的复制，只包括特定工作流的子目录
    private static void copyWorkflowMetaConf(String sourceDir, String destinationDir, String workflow) throws IOException {
        File sourceMetaConfDir = new File(sourceDir);
        File[] filesAndDirs = sourceMetaConfDir.listFiles();
        if (filesAndDirs != null) {
            for (File item : filesAndDirs) {
                if (item.isDirectory() && item.getName().equals(workflow)) {
                    copyDirectory(item.getPath(), Paths.get(destinationDir, item.getName()).toString());
                } else if (item.isFile()) {
                    Files.copy(item.toPath(), Paths.get(destinationDir, item.getName()), StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
    }

}
