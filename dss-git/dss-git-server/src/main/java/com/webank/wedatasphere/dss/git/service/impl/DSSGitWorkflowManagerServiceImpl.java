package com.webank.wedatasphere.dss.git.service.impl;

import com.webank.wedatasphere.dss.common.entity.BmlResource;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.common.service.BMLService;
import com.webank.wedatasphere.dss.git.common.protocol.GitUserEntity;
import com.webank.wedatasphere.dss.git.common.protocol.request.*;
import com.webank.wedatasphere.dss.git.common.protocol.response.*;
import com.webank.wedatasphere.dss.git.config.GitServerConfig;
import com.webank.wedatasphere.dss.git.constant.DSSGitConstant;
import com.webank.wedatasphere.dss.git.service.DSSGitWorkflowManagerService;
import com.webank.wedatasphere.dss.git.service.DSSWorkspaceGitService;
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
import org.springframework.util.CollectionUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class DSSGitWorkflowManagerServiceImpl implements DSSGitWorkflowManagerService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DSSWorkspaceGitService dssWorkspaceGitService;

    @Autowired
    @Qualifier("workflowBmlService")
    private BMLService bmlService;
    @Override
    public GitDiffResponse diff(GitDiffRequest request) {
        GitUserEntity gitUser = dssWorkspaceGitService.selectGit(request.getWorkspaceId(), DSSGitConstant.GIT_ACCESS_WRITE_TYPE);
        if (gitUser == null) {
            logger.error("the workspace : {} don't associate with git", request.getWorkspaceId());
            return null;
        }
        GitDiffResponse diff = null;
        Repository repository = null;
        try {
            // 拼接.git路径
            String gitPath = DSSGitUtils.generateGitPath(request.getProjectName());
            // 获取git仓库
            File repoDir = new File(gitPath);
            repository = getRepository(repoDir, request.getProjectName(), gitUser);
            // 本地保持最新状态
            DSSGitUtils.pull(repository, request.getProjectName(), gitUser);
            // 解压BML文件到本地 todo 对接Server时放开调试
            Map<String, BmlResource> bmlResourceMap = request.getBmlResourceMap();
            List<String> fileList = new ArrayList<>();
            for (Map.Entry<String, BmlResource> entry : bmlResourceMap.entrySet()) {
                fileList.add(entry.getKey());
                FileUtils.removeFlowNode(entry.getKey(), request.getProjectName());
                FileUtils.update(bmlService, entry.getKey(), entry.getValue(), request.getUsername());
            }
            diff = DSSGitUtils.diff(request.getProjectName(), fileList);
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
        GitUserEntity gitUser = dssWorkspaceGitService.selectGit(request.getWorkspaceId(), DSSGitConstant.GIT_ACCESS_WRITE_TYPE);
        if (gitUser == null) {
            logger.error("the workspace : {} don't associate with git", request.getWorkspaceId());
            return null;
        }
        Repository repository = null;
        GitCommitResponse commitResponse = null;
        try {
            // 拼接.git路径
            String gitPath = DSSGitUtils.generateGitPath(request.getProjectName());
            // 获取git仓库
            File repoDir = new File(gitPath);
            repository = getRepository(repoDir, request.getProjectName(), gitUser);
            // 本地保持最新状态
            DSSGitUtils.pull(repository, request.getProjectName(), gitUser);
            // 解压BML文件到本地 todo 对接Server时放开调试
            Map<String, BmlResource> bmlResourceMap = request.getBmlResourceMap();
            for (Map.Entry<String, BmlResource> entry : bmlResourceMap.entrySet()) {
                FileUtils.removeFlowNode(entry.getKey(), request.getProjectName());
                FileUtils.update(bmlService, entry.getKey(), entry.getValue(), request.getUsername());
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
    public GitSearchResponse search(GitSearchRequest request) throws DSSErrorException {
        String gitDir = "/" + FileUtils.normalizePath(GitServerConfig.GIT_SERVER_PATH.getValue()) + "/" + request.getProjectName() + "/.git";
        String workTree = "/" + FileUtils.normalizePath(GitServerConfig.GIT_SERVER_PATH.getValue()) + "/" + request.getProjectName() ;
        List<String> gitCommands = new ArrayList<>(Arrays.asList(
                "git", "--git-dir=" + gitDir, "--work-tree=" + workTree, "grep", "-l", request.getSearchContent()
        ));
        List<String> workflowNode = request.getPath();
        String fileName = request.getFileName();
        List<String> typeList = request.getType();
        List<String> path = new ArrayList<>();
        if (!CollectionUtils.isEmpty(workflowNode)) {
            if (!StringUtils.isEmpty(fileName)) {
                workflowNode = workflowNode.stream().map(s -> s + "/*" + fileName + "*").collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(typeList)) {
                    for (String workflow : workflowNode) {
                        for (String type : typeList) {
                            path.add(workflow + "." + type);
                        }
                    }
                }
            }else if (!CollectionUtils.isEmpty(typeList)) {
                for (String workflow : workflowNode) {
                    for (String type : typeList) {
                        path.add(workflow + "/**/*." + type);
                    }
                }
            }

            if (CollectionUtils.isEmpty(path)) {
                path = workflowNode;
            }
        }else if (!StringUtils.isEmpty(fileName)) {
            if (!CollectionUtils.isEmpty(typeList)) {
                for (String type : typeList) {
                    path.add("/*" + fileName + "*/." + type);
                }
            }else {
                path.add("/*" + fileName + "*");
            }
        }else if (!CollectionUtils.isEmpty(typeList)) {
            for (String type : typeList) {
                path.add("*." + type);
            }
        }

        if (!CollectionUtils.isEmpty(path)) {
            gitCommands.add("--");
            gitCommands.addAll(path);
        }
        logger.info(gitCommands.toString());
        List<String> fileList = process(gitCommands);
        Map<String, List<String>> result = new LinkedHashMap<>();

        if (CollectionUtils.isEmpty(fileList)) {
            return new GitSearchResponse(result, 0);
        }
        int start = (request.getPageNow()-1) * request.getPageSize();
        int end = Math.min((start + request.getPageSize()), fileList.size());
        if (request.getPageNow() < 0 || start >= fileList.size()) {
            throw new DSSErrorException(0101001, "当前请求页" + request.getPageNow() + "超出搜索指定范围");
        }
        final List<String> subList = fileList.subList(start, end);
        List<String> filePathList = new ArrayList<>();
        for (String file : subList) {
            filePathList.add("/" + FileUtils.normalizePath(GitServerConfig.GIT_SERVER_PATH.getValue())+ "/" + request.getProjectName() + "/" + file);
        }



        List<String> gitBaseCommand = new ArrayList<>(Arrays.asList(
                "git", "--git-dir=" + gitDir, "--work-tree=" + workTree, "grep", "-n", request.getSearchContent()
        ));


        for (String file : filePathList) {
            List<String> gitSearchCommand = new ArrayList<>(gitBaseCommand);
            gitSearchCommand.add(file);
            logger.info(gitSearchCommand.toString());

            final List<String> searchResult = process(gitSearchCommand);
            final List<String> subSearchResult = searchResult.size() > GitServerConfig.GIT_SEARCH_RESULT_LIMIT.getValue()? searchResult.subList(0, GitServerConfig.GIT_SEARCH_RESULT_LIMIT.getValue()) : searchResult;

            result.put(file, subSearchResult);
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
        GitUserEntity gitUser = dssWorkspaceGitService.selectGit(request.getWorkspaceId(), DSSGitConstant.GIT_ACCESS_WRITE_TYPE);
        if (gitUser == null) {
            logger.error("the workspace : {} don't associate with git", request.getWorkspaceId());
            return null;
        }
        Repository repository = null;
        GitDeleteResponse deleteResponse = null;
        try {
            // 拼接.git路径
            String gitPath = DSSGitUtils.generateGitPath(request.getProjectName());
            // 获取git仓库
            File repoDir = new File(gitPath);
            repository = getRepository(repoDir, request.getProjectName(), gitUser);
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
        GitUserEntity gitUser = dssWorkspaceGitService.selectGit(request.getWorkspaceId(), DSSGitConstant.GIT_ACCESS_WRITE_TYPE);
        if (gitUser == null) {
            logger.error("the workspace : {} don't associate with git", request.getWorkspaceId());
            return null;
        }
        Repository repository = null;
        GitFileContentResponse contentResponse = null;
        try {
            // 拼接.git路径
            String gitPath = DSSGitUtils.generateGitPath(request.getProjectName());
            // 获取git仓库
            File repoDir = new File(gitPath);
            repository = getRepository(repoDir, request.getProjectName(), gitUser);
            // 本地保持最新状态
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

    @Override
    public GitHistoryResponse getHistory(GitHistoryRequest request) {
        GitUserEntity gitUser = dssWorkspaceGitService.selectGit(request.getWorkspaceId(), DSSGitConstant.GIT_ACCESS_WRITE_TYPE);
        if (gitUser == null) {
            logger.error("the workspace : {} don't associate with git", request.getWorkspaceId());
            return null;
        }

        Repository repository = null;
        GitHistoryResponse response = new GitHistoryResponse();
        try {
            // 拼接.git路径
            String gitPath = DSSGitUtils.generateGitPath(request.getProjectName());
            // 获取git仓库
            File repoDir = new File(gitPath);
            repository = getRepository(repoDir, request.getProjectName(), gitUser);
            // 本地保持最新状态
            DSSGitUtils.pull(repository, request.getProjectName(), gitUser);

            response = DSSGitUtils.listCommitsBetween(repository, request.getStartCommitId(), request.getEndCommitId());

        } catch (Exception e) {
            logger.error("pull failed, the reason is ",e);
        } finally {
            repository.close();
        }
        return response;
    }

    private Repository getRepository(File repoDir, String projectName, GitUserEntity gitUser) throws IOException {
        Repository repository = null;
        // 当前机器不存在就新建
        if (repoDir.exists()) {
            repository = new FileRepositoryBuilder().setGitDir(repoDir).build();
        } else {
            repository = FileRepositoryBuilder.create(new File(String.valueOf(repoDir)));
            DSSGitUtils.remote(repository, projectName, gitUser);
        }
        return repository;
    }
}
