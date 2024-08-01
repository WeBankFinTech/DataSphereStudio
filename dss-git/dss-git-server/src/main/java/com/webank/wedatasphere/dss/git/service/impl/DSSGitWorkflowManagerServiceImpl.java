package com.webank.wedatasphere.dss.git.service.impl;

import com.webank.wedatasphere.dss.common.entity.BmlResource;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.common.service.BMLService;
import com.webank.wedatasphere.dss.git.common.protocol.GitSearchLine;
import com.webank.wedatasphere.dss.git.common.protocol.GitSearchResult;
import com.webank.wedatasphere.dss.git.common.protocol.GitTree;
import com.webank.wedatasphere.dss.git.common.protocol.GitUserEntity;
import com.webank.wedatasphere.dss.git.common.protocol.constant.GitConstant;
import com.webank.wedatasphere.dss.git.common.protocol.exception.GitErrorException;
import com.webank.wedatasphere.dss.git.common.protocol.request.*;
import com.webank.wedatasphere.dss.git.common.protocol.response.*;
import com.webank.wedatasphere.dss.git.common.protocol.config.GitServerConfig;
import com.webank.wedatasphere.dss.git.constant.DSSGitConstant;
import com.webank.wedatasphere.dss.git.dto.GitProjectGitInfo;
import com.webank.wedatasphere.dss.git.manage.GitProjectManager;
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
import org.springframework.util.CollectionUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class DSSGitWorkflowManagerServiceImpl implements DSSGitWorkflowManagerService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    @Qualifier("workflowBmlService")
    private BMLService bmlService;
    @Override
    public GitDiffResponse diff(GitDiffRequest request) {
        Long workspaceId = request.getWorkspaceId();
        String projectName = request.getProjectName();

        GitProjectGitInfo projectInfoByProjectName = GitProjectManager.getProjectInfoByProjectName(projectName);
        if (projectInfoByProjectName == null) {
            logger.error("the projectName : {} don't associate with git", projectName);
            return null;
        }
        String gitUser = projectInfoByProjectName.getGitUser();
        String gitToken = projectInfoByProjectName.getGitToken();
        String gitUrl = projectInfoByProjectName.getGitUrl();
        GitDiffResponse diff = null;
        // 拼接.git路径
        String gitPath = DSSGitUtils.generateGitPath(projectName, workspaceId, gitUser);
        // 获取git仓库
        File repoDir = new File(gitPath);
        try (Repository repository = getRepository(repoDir, projectName, workspaceId, gitUser, gitToken, gitUrl)){
            // 解压BML文件到本地
            Map<String, BmlResource> bmlResourceMap = request.getBmlResourceMap();
            List<String> fileList = new ArrayList<>(bmlResourceMap.keySet());
            // 本地保持最新状态
            DSSGitUtils.pull(repository, projectName, gitUser, gitToken);
            for (Map.Entry<String, BmlResource> entry : bmlResourceMap.entrySet()) {
                fileList.add(entry.getKey());
                // 解压BML文件到本地
                FileUtils.downloadBMLResource(bmlService, entry.getKey(), entry.getValue(), request.getUsername(), workspaceId, gitUser);
                FileUtils.removeFlowNode(entry.getKey(), projectName, workspaceId, gitUser);
                String metaConfPath = GitConstant.GIT_SERVER_META_PATH + File.separator + entry.getKey();
                fileList.add(metaConfPath);
                FileUtils.removeFlowNode(metaConfPath, projectName, workspaceId, gitUser);
                FileUtils.unzipBMLResource(entry.getKey(), workspaceId);

            }
            diff = DSSGitUtils.diff(projectName, fileList, workspaceId);
            // 重置本地
            DSSGitUtils.reset(repository, projectName);
        } catch (Exception e) {
            logger.error("pull failed, the reason is ",e);
        }
        return diff;

    }

    @Override
    public GitDiffResponse diffGit(GitDiffTargetCommitRequest request) {
        Long workspaceId = request.getWorkspaceId();
        String projectName = request.getProjectName();

        GitProjectGitInfo projectInfoByProjectName = GitProjectManager.getProjectInfoByProjectName(projectName);
        if (projectInfoByProjectName == null) {
            logger.error("the projectName : {} don't associate with git", projectName);
            return null;
        }
        String gitUser = projectInfoByProjectName.getGitUser();
        String gitToken = projectInfoByProjectName.getGitToken();
        String gitUrl = projectInfoByProjectName.getGitUrl();
        GitDiffResponse diff = null;
        // 拼接.git路径
        String gitPath = DSSGitUtils.generateGitPath(projectName, workspaceId, gitUser);
        String gitPrePath = DSSGitUtils.generateGitPrePath(projectName, workspaceId, gitUser);
        // 获取git仓库
        File repoDir = new File(gitPath);
        try (Repository repository = getRepository(repoDir, projectName, workspaceId, gitUser, gitToken, gitUrl)){
            // 本地保持最新状态
            DSSGitUtils.pull(repository, projectName, gitUser, gitToken);

            if (StringUtils.isEmpty(request.getCommitId())) {
                String path = gitPrePath + File.separator + request.getFilePath();
                String metaPath = gitPrePath+ File.separator + GitConstant.GIT_SERVER_META_PATH + File.separator + request.getFilePath();
                GitTree fileTree = getFileTree(path);
                GitTree metaFileTree = getFileTree(metaPath);

                List<GitTree> tree = new ArrayList<>();
                tree.add(fileTree);
                tree.add(metaFileTree);
                diff = new GitDiffResponse(tree);
            } else {
                diff = DSSGitUtils.diffGit(repository, request.getCommitId(), request.getFilePath());
            }

        } catch (Exception e) {
            logger.error("pull failed, the reason is ",e);
        }
        return diff;

    }

    private GitTree getFileTree(String path) throws GitErrorException {
        Path currentDir = Paths.get(path);

        GitTree root = new GitTree("");

        try (Stream<Path> paths = Files.walk(currentDir)) {
            paths
                    .filter(Files::isRegularFile) // 只过滤出文件
                    .forEach(file -> {
                        // 获取相对路径
                        Path relativePath = currentDir.relativize(file);
                        String fullPath = path + File.separator + relativePath.toString();
                        root.setAbsolutePath(fullPath);
                        root.addChild(fullPath);
                    });
        } catch (IOException e) {
           logger.error("get failed, the reason is ", e);
           throw new GitErrorException(800001, "get failed, the reason is " + e.getMessage());
        }

        return root;
    }

    @Override
    public GitCommitResponse commit(GitCommitRequest request) throws DSSErrorException {
        Long workspaceId = request.getWorkspaceId();
        String projectName = request.getProjectName();

        GitProjectGitInfo projectInfoByProjectName = GitProjectManager.getProjectInfoByProjectName(projectName);
        if (projectInfoByProjectName == null) {
            logger.error("the projectName : {} don't associate with git", projectName);
            return null;
        }
        String gitUser = projectInfoByProjectName.getGitUser();
        String gitToken = projectInfoByProjectName.getGitToken();
        String gitUrl = projectInfoByProjectName.getGitUrl();
        GitCommitResponse commitResponse = null;
        // 拼接.git路径
        String gitPath = DSSGitUtils.generateGitPath(projectName, workspaceId, gitUser);
        // 获取git仓库
        File repoDir = new File(gitPath);

        try (Repository repository = getRepository(repoDir, projectName, workspaceId, gitUser, gitToken, gitUrl)){
            // 解压BML文件到本地
            Map<String, BmlResource> bmlResourceMap = request.getBmlResourceMap();
            // 本地保持最新状态
            DSSGitUtils.pull(repository, projectName, gitUser, gitToken);
            List<String> paths = new ArrayList<>();
            for (Map.Entry<String, BmlResource> entry : bmlResourceMap.entrySet()) {
                paths.add(entry.getKey());
                // 解压BML文件到本地
                FileUtils.downloadBMLResource(bmlService, entry.getKey(), entry.getValue(), request.getUsername(), workspaceId, gitUser);
                FileUtils.removeFlowNode(entry.getKey(), projectName, workspaceId, gitUser);
                String metaConfPath = GitConstant.GIT_SERVER_META_PATH + File.separator + entry.getKey();
                paths.add(metaConfPath);
                FileUtils.removeFlowNode(metaConfPath, projectName, workspaceId, gitUser);
                FileUtils.unzipBMLResource(entry.getKey(), workspaceId);
            }
            // 提交
            String comment = request.getComment() + DSSGitConstant.GIT_USERNAME_FLAG + request.getUsername();
            // 提交前再次pull， 降低多节点同时提交不同工作流任务导致冲突频率
            DSSGitUtils.pull(repository, projectName, gitUser, gitToken);
            DSSGitUtils.push(repository, projectName, gitUser, gitToken, comment, paths);

            commitResponse = DSSGitUtils.getCurrentCommit(repository);
        } catch (Exception e) {
            logger.error("commit failed, the reason is ",e);
            throw new DSSErrorException(8001, "commit workflow failed, the reason is: " + e);
        }
        return commitResponse;
    }

    @Override
    public GitCommitResponse batchCommit(GitBatchCommitRequest request) throws DSSErrorException {
        Long workspaceId = request.getWorkspaceId();
        String projectName = request.getProjectName();

        GitProjectGitInfo projectInfoByProjectName = GitProjectManager.getProjectInfoByProjectName(projectName);
        if (projectInfoByProjectName == null) {
            logger.error("the projectName : {} don't associate with git", projectName);
            return null;
        }
        String gitUser = projectInfoByProjectName.getGitUser();
        String gitToken = projectInfoByProjectName.getGitToken();
        String gitUrl = projectInfoByProjectName.getGitUrl();
        GitCommitResponse commitResponse = null;
        // 拼接.git路径
        String gitPath = DSSGitUtils.generateGitPath(projectName, workspaceId, gitUser);
        // 获取git仓库
        File repoDir = new File(gitPath);

        try (Repository repository = getRepository(repoDir, projectName, workspaceId, gitUser, gitToken, gitUrl)){
            // 解压BML文件到本地
            BmlResource bmlResource = request.getBmlResource();
            List<String> paths = new ArrayList<>();
            paths.addAll(request.getFilePath());
            String fileName = projectName + bmlResource.getResourceId();
            // 本地保持最新状态
            DSSGitUtils.pull(repository, projectName, gitUser, gitToken);
            // 解压BML文件到本地
            FileUtils.downloadBMLResource(bmlService, fileName, bmlResource, request.getUsername(), workspaceId, gitUser);
            for (String path : request.getFilePath()) {
                FileUtils.removeFlowNode(path, projectName, workspaceId, gitUser);
                String metaConfPath = GitConstant.GIT_SERVER_META_PATH + File.separator + path;
                paths.add(metaConfPath);
                FileUtils.removeFlowNode(metaConfPath, projectName, workspaceId, gitUser);
            }
            FileUtils.unzipBMLResource(fileName, workspaceId);



            // 提交
            String comment = request.getComment() + DSSGitConstant.GIT_USERNAME_FLAG + request.getUsername();
            // 提交前再次pull， 降低多节点同时提交不同工作流任务导致冲突频率
            DSSGitUtils.pull(repository, projectName, gitUser, gitToken);
            DSSGitUtils.push(repository, projectName, gitUser, gitToken, comment, paths);

            commitResponse = DSSGitUtils.getCurrentCommit(repository);
        } catch (Exception e) {
            logger.error("commit failed, the reason is ",e);
            throw new DSSErrorException(8001, "commit workflow failed, the reason is: " + e);
        }
        return commitResponse;
    }

    @Override
    public GitSearchResponse search(GitSearchRequest request) {
        Long workspaceId = request.getWorkspaceId();
        String projectName = request.getProjectName();

        GitProjectGitInfo projectInfoByProjectName = GitProjectManager.getProjectInfoByProjectName(projectName);
        if (projectInfoByProjectName == null) {
            logger.error("the projectName : {} don't associate with git", projectName);
            return null;
        }
        String gitUser = projectInfoByProjectName.getGitUser();
        String gitToken = projectInfoByProjectName.getGitToken();
        String gitUrl = projectInfoByProjectName.getGitUrl();
        // 拼接.git路径
        String gitPath = DSSGitUtils.generateGitPath(projectName, workspaceId, gitUser);
        // 获取git仓库
        File repoDir = new File(gitPath);
        try (Repository repository = getRepository(repoDir, projectName, workspaceId, gitUser, gitToken, gitUrl)){
            // 本地保持最新状态
            DSSGitUtils.pull(repository, projectName, gitUser, gitToken);
        } catch (Exception e) {
            logger.error("pull failed, the reason is ",e);
            return new GitSearchResponse();
        }
        if (CollectionUtils.isEmpty(request.getTypeList())) {
            request.setTypeList(GitConstant.GIT_SERVER_SEARCH_TYPE);
        }
        String gitDir = DSSGitUtils.generateGitPath(projectName, request.getWorkspaceId(), gitUser);
        String gitPathPre = DSSGitConstant.GIT_PATH_PRE + request.getWorkspaceId() + File.separator;
        String workTree = gitPathPre + projectName;
        List<String> gitCommands = new ArrayList<>(Arrays.asList(
                "git", "--git-dir=" + gitDir, "--work-tree=" + workTree, "grep", "-F", "-l", request.getSearchContent()
        ));
        List<String> workflowNode = request.getWorkflowNameList();
        String fileName = request.getNodeName();
        List<String> typeList = request.getTypeList();
        List<String> path = new ArrayList<>();
        if (!CollectionUtils.isEmpty(workflowNode)) {
            if (!StringUtils.isEmpty(fileName)) {
                workflowNode = workflowNode.stream().map(s -> s + "*" + fileName + "*").collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(typeList)) {
                    for (String workflow : workflowNode) {
                        for (String type : typeList) {
                            path.add(workflow + "/*" + type);
                        }
                    }
                }
            }else if (!CollectionUtils.isEmpty(typeList)) {
                for (String workflow : workflowNode) {
                    for (String type : typeList) {
                        path.add(workflow + "/**/*" + type);
                    }
                }
            }

            if (CollectionUtils.isEmpty(path)) {
                path = workflowNode;
            }
        }else if (!StringUtils.isEmpty(fileName)) {
            if (!CollectionUtils.isEmpty(typeList)) {
                for (String type : typeList) {
                    path.add("*" + fileName + "*/*" + type);
                }
            }else {
                path.add("*" + fileName + "*");
            }
        }else if (!CollectionUtils.isEmpty(typeList)) {
            for (String type : typeList) {
                path.add("*" + type);
            }
        }

        if (!CollectionUtils.isEmpty(path)) {
            gitCommands.add("--");
            gitCommands.addAll(path);
        }
        logger.info(gitCommands.toString());
        List<String> fileList = process(gitCommands);
        List<GitSearchResult> result = new ArrayList<>();

        if (CollectionUtils.isEmpty(fileList)) {
            return new GitSearchResponse(result, 0);
        }

        String excludeDirectory = GitServerConfig.GIT_SEARCH_EXCLUDE_DIRECTORY.getValue();
        String excludeFile = GitServerConfig.GIT_SEARCH_EXCLUDE_FILE.getValue();

        List<String> excludeDirList = StringUtils.isEmpty(excludeDirectory)? new ArrayList<>() : Arrays.asList(excludeDirectory.split(","));
        List<String> excludeFileList = StringUtils.isEmpty(excludeFile)? new ArrayList<>() : Arrays.asList(excludeFile.split(","));

        Set<String> excludeResult = new HashSet<>();
        for (String file : fileList) {
            //排除指定文件夹下的内容 (.metaConf)
            for (String excludeDir : excludeDirList) {
                if (file.startsWith(excludeDir)) {
                    excludeResult.add(file);
                    break;
                }
            }
            // 排除指定文件下的内容(.properties)
            for (String exclude : excludeFileList) {
                if (file.endsWith(exclude)) {
                    excludeResult.add(file);
                }
            }
        }

        if (!CollectionUtils.isEmpty(excludeResult)) {
            fileList.removeAll(excludeResult);
            if (CollectionUtils.isEmpty(fileList)) {
                return new GitSearchResponse(result, 0);
            }
        }

        int start = (request.getPageNow()-1) * request.getPageSize();
        int end = Math.min((start + request.getPageSize()), fileList.size());
        if (request.getPageNow() < 0 || start >= fileList.size()) {
            logger.error("当前请求页" + request.getPageNow() + "超出搜索指定范围");
            return new GitSearchResponse(result, 0);
        }
        // subList 截断的List及子集 不允许List变更
        List<String> subList = new ArrayList<>(fileList.subList(start, end));
        List<String> filePathList = new ArrayList<>();
        for (String file : subList) {
            filePathList.add(workTree + File.separator + file);
        }



        List<String> gitBaseCommand = new ArrayList<>(Arrays.asList(
                "git", "--git-dir=" + gitDir, "--work-tree=" + workTree, "grep", "-F", "-n", request.getSearchContent()
        ));


        for (String file : filePathList) {
            List<String> gitSearchCommand = new ArrayList<>(gitBaseCommand);
            gitSearchCommand.add(file);
            logger.info(gitSearchCommand.toString());

            List<String> searchResult = process(gitSearchCommand);

            List<GitSearchLine> keyLines = new ArrayList<>();
            for (String resultLine : searchResult) {
                // 找到第一个冒号的位置
                int colonIndex = resultLine.indexOf(':');

                // 如果存在冒号，去除它之前的所有内容 /testGit/test/.sql:1:test -> 1:test
                if (colonIndex != -1 && colonIndex + 1 < resultLine.length()) {
                    resultLine = resultLine.substring(colonIndex + 1);
                    // 找到第二个冒号位置
                    int colonIndexSec = resultLine.indexOf(':');
                    if (colonIndexSec != -1 && colonIndexSec + 1 < resultLine.length()) {
                        Integer num = Integer.valueOf(resultLine.substring(0, colonIndexSec));
                        String line = resultLine.substring(colonIndexSec + 1);
                        if (!StringUtils.isEmpty(line)) {
                            // 只添加符合要求的结果
                            GitSearchLine searchLine = new GitSearchLine(num, line);
                            keyLines.add(searchLine);
                        }
                    }
                }
            }
            // 处理文件路径 /data/GitInstall/testGit/test/.sql -> testGit/test/.sql
            if (file.startsWith(workTree + File.separator)) {
                file = file.substring(workTree.length() + File.separator.length());
            }

            result.add(new GitSearchResult(file, keyLines));
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
            if (process != null) {
                process.destroy();
            }
        }
        return result;
    }

    @Override
    public GitDeleteResponse delete(GitDeleteRequest request) throws DSSErrorException {
        Long workspaceId = request.getWorkspaceId();
        String projectName = request.getProjectName();

        GitProjectGitInfo projectInfoByProjectName = GitProjectManager.getProjectInfoByProjectName(projectName);
        if (projectInfoByProjectName == null) {
            logger.error("the projectName : {} don't associate with git", projectName);
            return null;
        }
        String gitUser = projectInfoByProjectName.getGitUser();
        String gitToken = projectInfoByProjectName.getGitToken();
        String gitUrl = projectInfoByProjectName.getGitUrl();
        GitDeleteResponse deleteResponse = null;
        // 拼接.git路径
        String gitPath = DSSGitUtils.generateGitPath(projectName, request.getWorkspaceId(), gitUser);
        // 获取git仓库
        File repoDir = new File(gitPath);
        try (Repository repository = getRepository(repoDir, projectName, workspaceId, gitUser, gitToken, gitUrl)){
            // 本地保持最新状态
            DSSGitUtils.pull(repository, projectName, gitUser, gitToken);
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
            // 提交前再次pull， 降低多节点同时提交不同工作流任务导致冲突频率
            DSSGitUtils.pull(repository, projectName, gitUser, gitToken);
            // 提交
            DSSGitUtils.push(repository, projectName, gitUser, gitToken, "delete " + request.getDeleteFileList(), request.getDeleteFileList());
        } catch (Exception e) {
            logger.error("delete failed, the reason is ",e);
            throw new DSSErrorException(80001, "delete workflow failed, the reason is: " + e);
        }
        return null;
    }

    @Override
    public GitFileContentResponse getFileContent(GitFileContentRequest request) throws DSSErrorException {
        Long workspaceId = request.getWorkspaceId();
        String projectName = request.getProjectName();

        GitProjectGitInfo projectInfoByProjectName = GitProjectManager.getProjectInfoByProjectName(projectName);
        if (projectInfoByProjectName == null) {
            logger.error("the projectName : {} don't associate with git", projectName);
            return null;
        }
        String gitUser = projectInfoByProjectName.getGitUser();
        String gitToken = projectInfoByProjectName.getGitToken();
        String gitUrl = projectInfoByProjectName.getGitUrl();
        GitFileContentResponse contentResponse = new GitFileContentResponse();
        // 拼接.git路径
        String gitPath = DSSGitUtils.generateGitPath(projectName, workspaceId, gitUser);
        // 获取git仓库
        File repoDir = new File(gitPath);
        try (Repository repository = getRepository(repoDir, projectName, workspaceId, gitUser, gitToken, gitUrl)){
            // 本地保持最新状态
            DSSGitUtils.pull(repository, projectName, gitUser, gitToken);
            String before = null;
            String after = null;

            if (request.getPublish()) {
                if (StringUtils.isNotEmpty(request.getCommitId())) {
                    before = DSSGitUtils.getTargetCommitFileContent(repository, request.getCommitId(), request.getFilePath());
                }
                after = DSSGitUtils.getFileContent(request.getFilePath(), projectName, workspaceId);
            } else {
                // 获取当前提交前的文件内容
                before = DSSGitUtils.getFileContent(request.getFilePath(), projectName, workspaceId);
            }
            contentResponse.setAfter(after);
            contentResponse.setBefore(before);

            return contentResponse;
        } catch (Exception e) {
            throw new DSSErrorException(80001, "getFileContent failed, the reason is: " + e);
        }
    }

    @Override
    public GitHistoryResponse getHistory(GitHistoryRequest request) throws DSSErrorException {
        Long workspaceId = request.getWorkspaceId();
        String projectName = request.getProjectName();

        GitProjectGitInfo projectInfoByProjectName = GitProjectManager.getProjectInfoByProjectName(projectName);
        if (projectInfoByProjectName == null) {
            logger.error("the projectName : {} don't associate with git", projectName);
            return null;
        }
        String gitUser = projectInfoByProjectName.getGitUser();
        String gitToken = projectInfoByProjectName.getGitToken();
        String gitUrl = projectInfoByProjectName.getGitUrl();
        GitHistoryResponse response = new GitHistoryResponse();
        // 拼接.git路径
        String gitPath = DSSGitUtils.generateGitPath(projectName, request.getWorkspaceId(), gitUser);
        // 获取git仓库
        File repoDir = new File(gitPath);
        try (Repository repository = getRepository(repoDir, projectName, workspaceId, gitUser, gitToken, gitUrl)){

            List<String> fileList = Collections.singletonList(request.getFilePath());
            // 本地保持最新状态
            DSSGitUtils.pull(repository, projectName, gitUser, gitToken);

            List<GitCommitResponse> latestCommit = DSSGitUtils.getLatestCommit(repository, request.getFilePath(), null);
            if (CollectionUtils.isEmpty(latestCommit)) {
                logger.error("get Commit failed, the reason is null");
            }else {
                response.setResponses(latestCommit);
            }

        } catch (Exception e) {
            throw new DSSErrorException(80001, "getHistory failed, the reason is: " + e);
        }
        return response;
    }

    private Repository getRepository(File repoDir, String projectName, Long workspaceId, String gitUser, String gitToken, String gitUrl) throws DSSErrorException {
        Repository repository = null;
        try {
            // 当前机器不存在就新建
            if (repoDir.exists()) {
                repository = new FileRepositoryBuilder().setGitDir(repoDir).build();
            } else {
                // 本地创建Git项目
                DSSGitUtils.create(projectName, workspaceId, gitUser);
                // 获取git项目
                repository = new FileRepositoryBuilder().setGitDir(repoDir).build();
                DSSGitUtils.remote(repository, projectName, gitUser, gitUrl);
                DSSGitUtils.pull(repository, projectName, gitUser, gitToken);
            }
        } catch (Exception e) {
            logger.info("get repository failed, the reason is: ", e);
            throw new DSSErrorException(80001, "get repository failed, the reason is: " + e);
        }
        return repository;
    }

    @Override
    public GitCommitResponse getCurrentCommit(GitCurrentCommitRequest request) throws DSSErrorException {
        Long workspaceId = request.getWorkspaceId();
        String projectName = request.getProjectName();

        GitProjectGitInfo projectInfoByProjectName = GitProjectManager.getProjectInfoByProjectName(projectName);
        if (projectInfoByProjectName == null) {
            logger.error("the projectName : {} don't associate with git", projectName);
            return null;
        }
        String gitUser = projectInfoByProjectName.getGitUser();
        String gitToken = projectInfoByProjectName.getGitToken();
        String gitUrl = projectInfoByProjectName.getGitUrl();
        GitCommitResponse commitResponse = null;
        // 拼接.git路径
        String gitPath = DSSGitUtils.generateGitPath(projectName, request.getWorkspaceId(), gitUser);
        // 获取git仓库
        File repoDir = new File(gitPath);
        try (Repository repository = getRepository(repoDir, projectName, workspaceId, gitUser, gitToken, gitUrl)){
            // 本地保持最新状态
            DSSGitUtils.pull(repository, projectName, gitUser, gitToken);

            List<GitCommitResponse> latestCommit = DSSGitUtils.getLatestCommit(repository, request.getFilepath(), 1);
            if (CollectionUtils.isEmpty(latestCommit)) {
                logger.error("get latestCommit failed, the reason is null");
            } else {
                commitResponse = latestCommit.get(0);
                return commitResponse;
            }

        } catch (Exception e) {
            logger.error("getCurrentCommit, the reason is ",e);
            throw new DSSErrorException(80001, "getCurrentCommit failed, the reason is: " + e);
        }
        return null;
    }

    @Override
    public GitCommitResponse gitCheckOut(GitRevertRequest request) throws DSSErrorException {
        Long workspaceId = request.getWorkspaceId();
        String projectName = request.getProjectName();

        GitProjectGitInfo projectInfoByProjectName = GitProjectManager.getProjectInfoByProjectName(projectName);
        if (projectInfoByProjectName == null) {
            logger.error("the projectName : {} don't associate with git", projectName);
            return null;
        }
        String gitUser = projectInfoByProjectName.getGitUser();
        String gitToken = projectInfoByProjectName.getGitToken();
        String gitUrl = projectInfoByProjectName.getGitUrl();
        GitCommitResponse commitResponse = null;
        // 拼接.git路径
        String gitPath = DSSGitUtils.generateGitPath(projectName, request.getWorkspaceId(), gitUser);
        // 获取git仓库
        File repoDir = new File(gitPath);
        try (Repository repository = getRepository(repoDir, projectName, workspaceId, gitUser, gitToken, gitUrl)){
            // 本地保持最新状态
            DSSGitUtils.pull(repository, projectName, gitUser, gitToken);
            // 回滚
            DSSGitUtils.checkoutTargetCommit(repository, request);
            // push
            List<String> paths = Collections.singletonList(request.getPath());
            DSSGitUtils.push(repository, projectName, gitUser, gitToken,"revert "+ DSSGitConstant.GIT_USERNAME_FLAG + request.getUsername(), paths);

            List<GitCommitResponse> latestCommit = DSSGitUtils.getLatestCommit(repository, request.getPath(), 1);
            if (CollectionUtils.isEmpty(latestCommit)) {
                logger.error("get latestCommit failed, the reason is null");
            } else {
                return latestCommit.get(0);
            }

        } catch (Exception e) {
            logger.error("checkOut failed, the reason is ",e);
            throw new DSSErrorException(80001, "checkOut failed, the reason is: " + e);
        }
        return null;
    }

    @Override
    public GitCommitResponse removeFile(GitRemoveRequest request) throws DSSErrorException {
        Long workspaceId = request.getWorkspaceId();
        String projectName = request.getProjectName();

        GitProjectGitInfo projectInfoByProjectName = GitProjectManager.getProjectInfoByProjectName(projectName);
        if (projectInfoByProjectName == null) {
            logger.error("the projectName : {} don't associate with git", projectName);
            return null;
        }
        String gitUser = projectInfoByProjectName.getGitUser();
        String gitToken = projectInfoByProjectName.getGitToken();
        String gitUrl = projectInfoByProjectName.getGitUrl();
        GitCommitResponse commitResponse = null;
        // 拼接.git路径
        String gitPath = DSSGitUtils.generateGitPath(projectName, workspaceId, gitUser);
        // 获取git仓库
        File repoDir = new File(gitPath);
        try (Repository repository = getRepository(repoDir, projectName, workspaceId, gitUser, gitToken, gitUrl)){
            // 本地保持最新状态
            DSSGitUtils.pull(repository, projectName, gitUser, gitToken);
            List<String> paths = new ArrayList<>();
            // 同步删除对应节点
            for (String path : request.getPath()) {
                paths.add(path);
                FileUtils.removeFlowNode(path, projectName, workspaceId, gitUser);
                String metaConfPath = GitConstant.GIT_SERVER_META_PATH + File.separator + path;
                paths.add(metaConfPath);
                FileUtils.removeFlowNode(metaConfPath, projectName, workspaceId, gitUser);
            }
            // 提交
            String comment = "delete workflowNode " + request.getPath().toString() + DSSGitConstant.GIT_USERNAME_FLAG + request.getUsername();
            // 提交前再次pull， 降低多节点同时提交不同工作流任务导致冲突频率
            DSSGitUtils.pull(repository, projectName, gitUser, gitToken);
            DSSGitUtils.push(repository, projectName, gitUser, gitToken, comment, paths);

            commitResponse = DSSGitUtils.getCurrentCommit(repository);

        } catch (Exception e) {
            logger.error("removeFile failed, the reason is ",e);
            throw new DSSErrorException(80001, "removeFile failed, the reason is: " + e);
        }
        return commitResponse;
    }

    @Override
    public GitCommitResponse rename(GitRenameRequest request) throws DSSErrorException {
        Long workspaceId = request.getWorkspaceId();
        String projectName = request.getProjectName();

        GitProjectGitInfo projectInfoByProjectName = GitProjectManager.getProjectInfoByProjectName(projectName);
        if (projectInfoByProjectName == null) {
            logger.error("the projectName : {} don't associate with git", projectName);
            return null;
        }
        String gitUser = projectInfoByProjectName.getGitUser();
        String gitToken = projectInfoByProjectName.getGitToken();
        String gitUrl = projectInfoByProjectName.getGitUrl();
        
        GitCommitResponse commitResponse = null;
        // 拼接.git路径
        String gitPath = DSSGitUtils.generateGitPath(projectName, request.getWorkspaceId(), gitUser);
        // 获取git仓库
        File repoDir = new File(gitPath);
        try (Repository repository = getRepository(repoDir, projectName, workspaceId, gitUser, gitToken, gitUrl)){
            // 同步删除对应节点 eg: /data/GitInstall/224/testGit/flowGitOld -> /data/GitInstall/224/testGit/flowGitNew
            String projectPath = DSSGitUtils.generateGitPrePath(projectName, workspaceId, gitUser) + File.separator;
            String olfFilePath = projectPath + FileUtils.normalizePath(request.getOldName());
            String filePath = projectPath + FileUtils.normalizePath(request.getName());
            // 同步删除对应节点 eg: /data/GitInstall/224/testGit/.metaConf/flowGitOld -> /data/GitInstall/224/testGit/.metaConf/flowGitNew
            String metaPath = FileUtils.normalizePath(GitConstant.GIT_SERVER_META_PATH) + File.separator + FileUtils.normalizePath(request.getOldName());
            String oldFileMetaPath = projectPath  + metaPath;
            String oldMetaPath = FileUtils.normalizePath(GitConstant.GIT_SERVER_META_PATH) + File.separator + FileUtils.normalizePath(request.getName());
            String fileMetaPath = projectPath + oldMetaPath;
            // 本地保持最新状态
            DSSGitUtils.pull(repository, projectName, gitUser, gitToken);
            FileUtils.renameFile(olfFilePath, filePath);
            FileUtils.renameFile(oldFileMetaPath, fileMetaPath);
            // 提交
            String comment = "rename workflowNode " + request.getName() + DSSGitConstant.GIT_USERNAME_FLAG + request.getUsername();
            // 提交前再次pull， 降低多节点同时提交不同工作流任务导致冲突频率
            DSSGitUtils.pull(repository, projectName, gitUser, gitToken);
            List<String> paths = new ArrayList<>();
            paths.add(FileUtils.normalizePath(request.getOldName()));
            paths.add(FileUtils.normalizePath(request.getName()));

            paths.add(metaPath);
            paths.add(oldMetaPath);
            DSSGitUtils.push(repository, projectName, gitUser, gitToken, comment, paths);

            commitResponse = DSSGitUtils.getCurrentCommit(repository);

        } catch (Exception e) {
            logger.error("rename failed, the reason is ",e);
            throw new DSSErrorException(80001, "rename failed, the reason is: " + e);
        }
        return commitResponse;
    }

    @Override
    public GitHistoryResponse getHistory(GitCommitInfoBetweenRequest request) throws DSSErrorException {
        Long workspaceId = request.getWorkspaceId();
        String projectName = request.getProjectName();

        GitProjectGitInfo projectInfoByProjectName = GitProjectManager.getProjectInfoByProjectName(projectName);
        String gitUser = projectInfoByProjectName.getGitUser();
        String gitToken = projectInfoByProjectName.getGitToken();
        String gitUrl = projectInfoByProjectName.getGitUrl();
        if (projectInfoByProjectName == null) {
            logger.error("the projectName : {} don't associate with git", projectName);
            return null;
        }
        GitHistoryResponse response = new GitHistoryResponse();
        // 拼接.git路径
        String gitPath = DSSGitUtils.generateGitPath(projectName, request.getWorkspaceId(), gitUser);
        // 获取git仓库
        File repoDir = new File(gitPath);
        try (Repository repository = getRepository(repoDir, projectName, workspaceId, gitUser, gitToken, gitUrl)){
            // 本地保持最新状态
            DSSGitUtils.pull(repository, projectName, gitUser, gitToken);
            if (StringUtils.isEmpty(request.getOldCommitId())) {
                // 去掉上线
                List<GitCommitResponse> latestCommit = DSSGitUtils.getLatestCommit(repository, request.getDirName(), null);
                if (CollectionUtils.isEmpty(latestCommit)) {
                    logger.error("get Commit failed, the reason is null");
                }else {
                    response.setResponses(latestCommit);
                }
            } else {
                response = DSSGitUtils.listCommitsBetween(repository, request.getOldCommitId(), request.getNewCommitId(), request.getDirName());
            }
        } catch (Exception e) {
            logger.error("getHistory failed, the reason is ",e);
            throw new DSSErrorException(80001, "getHistory failed, the reason is: " + e);
        }
        return response;
    }
}
