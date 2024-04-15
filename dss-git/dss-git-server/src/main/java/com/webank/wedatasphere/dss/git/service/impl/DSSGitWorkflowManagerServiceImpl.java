package com.webank.wedatasphere.dss.git.service.impl;

import com.webank.wedatasphere.dss.common.entity.BmlResource;
import com.webank.wedatasphere.dss.common.service.BMLService;
import com.webank.wedatasphere.dss.framework.workspace.bean.GitUserEntity;
import com.webank.wedatasphere.dss.framework.workspace.service.DSSWorkspaceGitService;
import com.webank.wedatasphere.dss.git.common.protocol.request.*;
import com.webank.wedatasphere.dss.git.common.protocol.response.*;
import com.webank.wedatasphere.dss.git.config.GitServerConfig;
import com.webank.wedatasphere.dss.git.service.DSSGitWorkflowManagerService;
import com.webank.wedatasphere.dss.git.utils.DSSGitUtils;
import com.webank.wedatasphere.dss.git.utils.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class DSSGitWorkflowManagerServiceImpl implements DSSGitWorkflowManagerService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DSSWorkspaceGitService dssWorkspaceGitService;

    @Autowired
    @Qualifier("workflowBmlService")
    private BMLService bmlService;
    @Override
    public GItDiffResponse diff(GitDiffRequest request) {
        GitUserEntity gitUser = dssWorkspaceGitService.selectGit(request.getWorkspaceId());
        if (gitUser == null) {
            logger.error("the workspace : {} don't associate with git", request.getWorkspaceId());
            return null;
        }
        GItDiffResponse diff = null;
        Repository repository = null;
        try {
            // Path to the Git repository (.git directory or its parent)
            File repoDir = new File("/" + FileUtils.normalizePath(GitServerConfig.GIT_SERVER_PATH.getValue()) + "/" + request.getProjectName() + "/.git");
            // 当前机器不存在就新建
            if (repoDir.exists()) {
                repository = new FileRepositoryBuilder().setGitDir(repoDir).build();
            } else {
                repository = FileRepositoryBuilder.create(new File(String.valueOf(repoDir)));
                DSSGitUtils.remote(repository, request.getProjectName(), gitUser);
            }
            // 本地保持最新状态
            DSSGitUtils.pull(repository, request.getProjectName(), gitUser);
            // 解压BML文件到本地 todo 对接Server时放开调试
            Map<String, BmlResource> bmlResourceMap = request.getBmlResourceMap();
            for (Map.Entry<String, BmlResource> entry : bmlResourceMap.entrySet()) {
                FileUtils.removeAndUpdate(bmlService, entry.getKey(), entry.getValue(), request.getUsername());
            }
            diff = DSSGitUtils.diff(request.getProjectName());
            // 重置本地
            DSSGitUtils.reset(request.getProjectName());
        } catch (Exception e) {
            logger.error("pull failed, the reason is ",e);
        }finally {
            repository.close();
        }
        return diff;

    }

    @Override
    public GitCommitResponse commit(GitCommitRequest request) {
        GitUserEntity gitUser = dssWorkspaceGitService.selectGit(request.getWorkspaceId());
        if (gitUser == null) {
            logger.error("the workspace : {} don't associate with git", request.getWorkspaceId());
            return null;
        }
        Repository repository = null;
        GitCommitResponse commitResponse = null;
        try {
            // Path to the Git repository (.git directory or its parent)
            File repoDir = new File("/" + FileUtils.normalizePath(GitServerConfig.GIT_SERVER_PATH.getValue()) + "/" + request.getProjectName() + "/.git");
            // 当前机器不存在就新建
            if (repoDir.exists()) {
                repository = new FileRepositoryBuilder().setGitDir(repoDir).build();
            } else {
                repository = FileRepositoryBuilder.create(new File(String.valueOf(repoDir)));
                DSSGitUtils.remote(repository, request.getProjectName(), gitUser);
            }
            // 本地保持最新状态
            DSSGitUtils.pull(repository, request.getProjectName(), gitUser);
            // 解压BML文件到本地 todo 对接Server时放开调试
            Map<String, BmlResource> bmlResourceMap = request.getBmlResourceMap();
            for (Map.Entry<String, BmlResource> entry : bmlResourceMap.entrySet()) {
                FileUtils.removeAndUpdate(bmlService, entry.getKey(), entry.getValue(), request.getUsername());
            }
            // 提交
            DSSGitUtils.push(repository, request.getProjectName(), gitUser, request.getComment());

            commitResponse = DSSGitUtils.getCurrentCommit(repository);

        } catch (Exception e) {
            logger.error("pull failed, the reason is ",e);
        } finally {
            repository.close();
        }
        return commitResponse;
    }

    @Override
    public GitSearchResponse search(GitSearchRequest request) {
        String gitDir = "/" + FileUtils.normalizePath(GitServerConfig.GIT_SERVER_PATH.getValue()) + "/" + request.getProjectName() + "/.git";
        String workTree = "/" + FileUtils.normalizePath(GitServerConfig.GIT_SERVER_PATH.getValue()) + "/" + request.getProjectName() ;
        List<String> gitCommands = new ArrayList<>(Arrays.asList(
                "git", "--git-dir=" + gitDir, "--work-tree=" + workTree, "grep", "-l", request.getSearchContent()
        ));
        logger.info(gitCommands.toString());
        List<String> fileList = process(gitCommands);
        List<String> filePathList = new ArrayList<>();
        for (String file : fileList) {
            filePathList.add("/" + FileUtils.normalizePath(GitServerConfig.GIT_SERVER_PATH.getValue())+ "/" + request.getProjectName() + "/" + file);
        }

        List<String> fileCommands = new ArrayList<>(Arrays.asList(
                "wc", "-l"
        ));

        fileCommands.addAll(filePathList);

        List<String> process = process(fileCommands);
        Map<String, Integer> map =new LinkedHashMap<>();
        for (String content: process) {
            String[] split = content.trim().split("\\s+");
            logger.info("content : {}", content);
            logger.info("split 0 : {}", split[0]);
            logger.info("split 1 : {}", split[1]);
            if (split.length == 2) {
                int line = Integer.parseInt(split[0]);
                if (line == 0) {
                    line ++;
                }
                String fileName = split[1];
                if(fileName.equals("total")) {
                    continue;
                }
                map.put(fileName, line);
            }
        }
        List<String> currentFiles = new ArrayList<>();
         {
        int total = 0;
        boolean flag = StringUtils.isEmpty(request.getCurrentPageLastFile());
        logger.info("flag before: {}", flag);
        for(Map.Entry<String, Integer> entry: map.entrySet()) {
            logger.info("entry key : {}", entry.getKey());
            logger.info("entry value : {}", entry.getValue());
            logger.info("flag 1111111: {}", flag);
            if (total + entry.getValue() >= 1000) {
                break;
            }
            if (!StringUtils.isEmpty(request.getCurrentPageLastFile()) && request.getCurrentPageLastFile().equals(entry.getKey())) {
                flag = true;
                continue;
            }
            if (flag) {
                total += entry.getValue();
                currentFiles.add(entry.getKey());
            }
        }
        }


        List<String> gitCurrentCommands = new ArrayList<>(Arrays.asList(
                "git", "--git-dir=" + gitDir, "--work-tree=" + workTree, "grep", "-n", request.getSearchContent()
        ));
        gitCurrentCommands.addAll(currentFiles);
        logger.info(gitCurrentCommands.toString());
        List<String> fileCurrentList = process(gitCurrentCommands);
        Map<String, List<String>> result = new LinkedHashMap<>();
        for (String fileContent : fileCurrentList) {
            String[] split = fileContent.split(":", 2);

            String fileName = split[0];
            String fileSearchContent = split[1];
            if (result.containsKey(fileName)) {
                result.get(fileName).add(fileSearchContent);
            }else {
                List<String> content = new ArrayList<>();
                content.add(fileSearchContent);
                result.put(fileName, content);
            }
        }
        return new GitSearchResponse(result, fileList.size());
    }

    private List<String> process(List<String> commands) {
        List<String> result = new ArrayList<>();
        ProcessBuilder processBuilder = new ProcessBuilder(commands);
        processBuilder.redirectErrorStream(true); // Merge error stream with the standard output stream
        Process process = null;
        try {
            process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                logger.info(line);
                result.add(line);
            }
            boolean b = process.waitFor(5, TimeUnit.SECONDS);
            if (b) {
                int exitCode = process.waitFor();
                logger.info("Exit code: " + exitCode);

            }else {
                logger.info("search timeout");
                process.destroy();
            }
        } catch (IOException | InterruptedException e) {
            logger.error("grep failed ,the reason is :", e);
        } finally {
            process.destroy();
        }
        return result;
    }

    @Override
    public GitDeleteResponse delete(GitDeleteRequest request) {
        GitUserEntity gitUser = dssWorkspaceGitService.selectGit(request.getWorkspaceId());
        if (gitUser == null) {
            logger.error("the workspace : {} don't associate with git", request.getWorkspaceId());
            return null;
        }
        Repository repository = null;
        GitDeleteResponse deleteResponse = null;
        try {
            // Path to the Git repository (.git directory or its parent)
            File repoDir = new File("/" + FileUtils.normalizePath(GitServerConfig.GIT_SERVER_PATH.getValue()) + "/" + request.getProjectName() + "/.git");
            // 当前机器不存在就新建
            if (repoDir.exists()) {
                repository = new FileRepositoryBuilder().setGitDir(repoDir).build();
            } else {
                repository = FileRepositoryBuilder.create(new File(String.valueOf(repoDir)));
                DSSGitUtils.remote(repository, request.getProjectName(), gitUser);
            }
            // 本地保持最新状态
            DSSGitUtils.pull(repository, request.getProjectName(), gitUser);
            List<String> deleteFileList = request.getDeleteFileList();
            for (String path : deleteFileList) {
                File file = new File(path);
                if (file.exists()) {
                    if (file.isDirectory()) {
                        FileUtils.removeDirectory(path);
                    } else {
                        FileUtils.removeFiles(path);
                    }
                }
            }
            // 提交
            DSSGitUtils.push(repository, request.getProjectName(), gitUser,"delete " + request.getDeleteFileList());
        } catch (Exception e) {
            logger.error("pull failed, the reason is ",e);
        } finally {
            repository.close();
        }
        return null;
    }

    @Override
    public GitFileContentResponse getFileContent(GitFileContentRequest request) {
        GitUserEntity gitUser = dssWorkspaceGitService.selectGit(request.getWorkspaceId());
        if (gitUser == null) {
            logger.error("the workspace : {} don't associate with git", request.getWorkspaceId());
            return null;
        }
        Repository repository = null;
        GitFileContentResponse contentResponse = null;
        try {
            // Path to the Git repository (.git directory or its parent)
            File repoDir = new File("/" + FileUtils.normalizePath(GitServerConfig.GIT_SERVER_PATH.getValue()) + "/" + request.getProjectName() + "/.git");
            // 当前机器不存在就新建
            if (repoDir.exists()) {
                repository = new FileRepositoryBuilder().setGitDir(repoDir).build();
            } else {
                repository = FileRepositoryBuilder.create(new File(String.valueOf(repoDir)));
                DSSGitUtils.remote(repository, request.getProjectName(), gitUser);
            }
            DSSGitUtils.pull(repository, request.getProjectName(), gitUser);

            String content = DSSGitUtils.getTargetCommitFileContent(request.getProjectName(), request.getCommitId(), request.getFilePath());
            String fullpath = File.separator + FileUtils.normalizePath(GitServerConfig.GIT_SERVER_PATH.getValue()) + File.separator + FileUtils.normalizePath(request.getFilePath());
            File file = new File(fullpath);
            String fileName = file.getName();
            BmlResource bmlResource = FileUtils.uploadResourceToBML(bmlService, gitUser.getGitUser(), content, fileName, request.getProjectName());
            logger.info("upload success, the fileName is : {}", request.getFilePath());
            contentResponse.setBmlResource(bmlResource);
            return contentResponse;
        } catch (Exception e) {
            logger.error("pull failed, the reason is ",e);
        } finally {
            repository.close();
        }
       return null;
    }
}
