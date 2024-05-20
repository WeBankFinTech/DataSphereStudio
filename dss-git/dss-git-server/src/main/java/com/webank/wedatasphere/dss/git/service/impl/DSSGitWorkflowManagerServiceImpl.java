package com.webank.wedatasphere.dss.git.service.impl;

import com.webank.wedatasphere.dss.common.entity.BmlResource;
import com.webank.wedatasphere.dss.common.service.BMLService;
import com.webank.wedatasphere.dss.git.common.protocol.GitSearchLine;
import com.webank.wedatasphere.dss.git.common.protocol.GitSearchResult;
import com.webank.wedatasphere.dss.git.common.protocol.GitUserEntity;
import com.webank.wedatasphere.dss.git.common.protocol.constant.GitConstant;
import com.webank.wedatasphere.dss.git.common.protocol.request.*;
import com.webank.wedatasphere.dss.git.common.protocol.response.*;
import com.webank.wedatasphere.dss.git.common.protocol.config.GitServerConfig;
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
        GitUserEntity gitUser = dssWorkspaceGitService.selectGit(request.getWorkspaceId(), GitConstant.GIT_ACCESS_WRITE_TYPE);
        if (gitUser == null) {
            logger.error("the workspace : {} don't associate with git", request.getWorkspaceId());
            return null;
        }
        GitDiffResponse diff = null;
        // 拼接.git路径
        String gitPath = DSSGitUtils.generateGitPath(request.getProjectName());
        // 获取git仓库
        File repoDir = new File(gitPath);
        try (Repository repository = getRepository(repoDir, request.getProjectName(), gitUser)){
            // 解压BML文件到本地
            Map<String, BmlResource> bmlResourceMap = request.getBmlResourceMap();
            List<String> fileList = new ArrayList<>(bmlResourceMap.keySet());
            // 本地保持最新状态
            DSSGitUtils.pull(repository, request.getProjectName(), gitUser);
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
        }
        return diff;

    }

    @Override
    public GitCommitResponse commit(GitCommitRequest request) {
        GitUserEntity gitUser = dssWorkspaceGitService.selectGit(request.getWorkspaceId(), GitConstant.GIT_ACCESS_WRITE_TYPE);
        if (gitUser == null) {
            logger.error("the workspace : {} don't associate with git", request.getWorkspaceId());
            return null;
        }
        GitCommitResponse commitResponse = null;
        // 拼接.git路径
        String gitPath = DSSGitUtils.generateGitPath(request.getProjectName());
        // 获取git仓库
        File repoDir = new File(gitPath);

        try (Repository repository = getRepository(repoDir, request.getProjectName(), gitUser)){
            // 解压BML文件到本地
            Map<String, BmlResource> bmlResourceMap = request.getBmlResourceMap();
            List<String> fileList = new ArrayList<>(bmlResourceMap.keySet());
            // 本地保持最新状态
            DSSGitUtils.pull(repository, request.getProjectName(), gitUser);
            for (Map.Entry<String, BmlResource> entry : bmlResourceMap.entrySet()) {
                FileUtils.removeFlowNode(entry.getKey(), request.getProjectName());
                FileUtils.update(bmlService, entry.getKey(), entry.getValue(), request.getUsername());
            }
            // 提交
            String comment = request.getComment() + DSSGitConstant.GIT_USERNAME_FLAG + request.getUsername();
            DSSGitUtils.push(repository, request.getProjectName(), gitUser, comment);

            commitResponse = DSSGitUtils.getCurrentCommit(repository);
        } catch (Exception e) {
            logger.error("pull failed, the reason is ",e);
        }
        return commitResponse;
    }

    @Override
    public GitSearchResponse search(GitSearchRequest request) {
        String gitDir = DSSGitUtils.generateGitPath(request.getProjectName());
        String workTree = DSSGitConstant.GIT_PATH_PRE + request.getProjectName() ;
        List<String> gitCommands = new ArrayList<>(Arrays.asList(
                "git", "--git-dir=" + gitDir, "--work-tree=" + workTree, "grep", "-l", request.getSearchContent()
        ));
        List<String> workflowNode = request.getWorkflowNameList();
        String fileName = request.getNodeName();
        List<String> typeList = request.getTypeList();
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
                if (file.endsWith(File.separator + exclude)) {
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
            filePathList.add(DSSGitConstant.GIT_PATH_PRE + request.getProjectName() + File.separator + file);
        }



        List<String> gitBaseCommand = new ArrayList<>(Arrays.asList(
                "git", "--git-dir=" + gitDir, "--work-tree=" + workTree, "grep", "-n", request.getSearchContent()
        ));


        for (String file : filePathList) {
            List<String> gitSearchCommand = new ArrayList<>(gitBaseCommand);
            gitSearchCommand.add(file);
            logger.info(gitSearchCommand.toString());

            List<String> searchResult = process(gitSearchCommand);
            List<String> subSearchResult = null;
            if (searchResult.size() > GitServerConfig.GIT_SEARCH_RESULT_LIMIT.getValue()) {
                subSearchResult = new ArrayList<>(searchResult.subList(0, GitServerConfig.GIT_SEARCH_RESULT_LIMIT.getValue()));
            } else {
                subSearchResult = searchResult;
            }
            List<GitSearchLine> keyLines = new ArrayList<>();
            for (String resultLine : subSearchResult) {
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
                        // 只添加符合要求的结果
                        GitSearchLine searchLine = new GitSearchLine(num, line);
                        keyLines.add(searchLine);
                    }
                }
            }
            // 处理文件路径 /data/GitInstall/testGit/test/.sql -> testGit/test/.sql
            if (file.startsWith(DSSGitConstant.GIT_PATH_PRE)) {
                file = file.substring(DSSGitConstant.GIT_PATH_PRE.length());
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
            process.destroy();
        }
        return result;
    }

    @Override
    public GitDeleteResponse delete(GitDeleteRequest request) {
        GitUserEntity gitUser = dssWorkspaceGitService.selectGit(request.getWorkspaceId(), GitConstant.GIT_ACCESS_WRITE_TYPE);
        if (gitUser == null) {
            logger.error("the workspace : {} don't associate with git", request.getWorkspaceId());
            return null;
        }
        GitDeleteResponse deleteResponse = null;
        // 拼接.git路径
        String gitPath = DSSGitUtils.generateGitPath(request.getProjectName());
        // 获取git仓库
        File repoDir = new File(gitPath);
        try (Repository repository = getRepository(repoDir, request.getProjectName(), gitUser)){
            List<String> fileList = request.getDeleteFileList();
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
        }
        return null;
    }

    @Override
    public GitFileContentResponse getFileContent(GitFileContentRequest request) {
        GitUserEntity gitUser = dssWorkspaceGitService.selectGit(request.getWorkspaceId(), GitConstant.GIT_ACCESS_WRITE_TYPE);
        if (gitUser == null) {
            logger.error("the workspace : {} don't associate with git", request.getWorkspaceId());
            return null;
        }
        GitFileContentResponse contentResponse = null;
        // 拼接.git路径
        String gitPath = DSSGitUtils.generateGitPath(request.getProjectName());
        // 获取git仓库
        File repoDir = new File(gitPath);
        try (Repository repository = getRepository(repoDir, request.getProjectName(), gitUser)){
            List<String> fileList = Collections.singletonList(request.getFilePath());
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
        }
       return null;
    }

    @Override
    public GitHistoryResponse getHistory(GitHistoryRequest request) {
        GitUserEntity gitUser = dssWorkspaceGitService.selectGit(request.getWorkspaceId(), GitConstant.GIT_ACCESS_WRITE_TYPE);
        if (gitUser == null) {
            logger.error("the workspace : {} don't associate with git", request.getWorkspaceId());
            return null;
        }
        GitHistoryResponse response = new GitHistoryResponse();
        // 拼接.git路径
        String gitPath = DSSGitUtils.generateGitPath(request.getProjectName());
        // 获取git仓库
        File repoDir = new File(gitPath);
        try (Repository repository = getRepository(repoDir, request.getProjectName(), gitUser)){

            List<String> fileList = Collections.singletonList(request.getFilePath());
            // 本地保持最新状态
            DSSGitUtils.pull(repository, request.getProjectName(), gitUser);

            List<GitCommitResponse> latestCommit = DSSGitUtils.getLatestCommit(repository, request.getFilePath(), 5);
            if (CollectionUtils.isEmpty(latestCommit)) {
                logger.error("get Commit failed, the reason is null");
            }else {
                response.setResponses(latestCommit);
            }

        } catch (Exception e) {
            logger.error("pull failed, the reason is ",e);
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

    @Override
    public GitCommitResponse getCurrentCommit(GitCurrentCommitRequest request) {
        GitUserEntity gitUser = dssWorkspaceGitService.selectGit(request.getWorkspaceId(), GitConstant.GIT_ACCESS_WRITE_TYPE);
        if (gitUser == null) {
            logger.error("the workspace : {} don't associate with git", request.getWorkspaceId());
            return null;
        }
        GitCommitResponse commitResponse = null;
        // 拼接.git路径
        String gitPath = DSSGitUtils.generateGitPath(request.getProjectName());
        // 获取git仓库
        File repoDir = new File(gitPath);
        try (Repository repository = getRepository(repoDir, request.getProjectName(), gitUser);){
            List<String> fileList = Collections.singletonList(request.getFilepath());
            // 本地保持最新状态
            DSSGitUtils.pull(repository, request.getProjectName(), gitUser);

            List<GitCommitResponse> latestCommit = DSSGitUtils.getLatestCommit(repository, request.getFilepath(), 1);
            if (CollectionUtils.isEmpty(latestCommit)) {
                logger.error("get latestCommit failed, the reason is null");
            } else {
                commitResponse = latestCommit.get(0);
                return commitResponse;
            }

        } catch (Exception e) {
            logger.error("pull failed, the reason is ",e);
        }
        return null;
    }

    @Override
    public GitCommitResponse gitCheckOut(GitRevertRequest request) throws IOException {
        GitUserEntity gitUser = dssWorkspaceGitService.selectGit(request.getWorkspaceId(), GitConstant.GIT_ACCESS_WRITE_TYPE);
        if (gitUser == null) {
            logger.error("the workspace : {} don't associate with git", request.getWorkspaceId());
            return null;
        }
        GitCommitResponse commitResponse = null;
        // 拼接.git路径
        String gitPath = DSSGitUtils.generateGitPath(request.getProjectName());
        // 获取git仓库
        File repoDir = new File(gitPath);
        try (Repository repository = getRepository(repoDir, request.getProjectName(), gitUser)){
            List<String> fileList = Collections.singletonList(request.getPath());
            // 本地保持最新状态
            DSSGitUtils.pull(repository, request.getProjectName(), gitUser);
            // 回滚
            DSSGitUtils.checkoutTargetCommit(request);
            // push
            DSSGitUtils.push(repository, request.getProjectName(), gitUser, "revert by : " + request.getUsername());

            List<GitCommitResponse> latestCommit = DSSGitUtils.getLatestCommit(repository, request.getPath(), 1);
            if (CollectionUtils.isEmpty(latestCommit)) {
                logger.error("get latestCommit failed, the reason is null");
            } else {
                return commitResponse;
            }

        } catch (Exception e) {
            logger.error("checkOut failed, the reason is ",e);
        }
        return null;
    }

    @Override
    public GitCommitResponse removeFile(GitRemoveRequest request) {
        GitUserEntity gitUser = dssWorkspaceGitService.selectGit(request.getWorkspaceId(), GitConstant.GIT_ACCESS_WRITE_TYPE);
        if (gitUser == null) {
            logger.error("the workspace : {} don't associate with git", request.getWorkspaceId());
            return null;
        }
        GitCommitResponse commitResponse = null;
        // 拼接.git路径
        String gitPath = DSSGitUtils.generateGitPath(request.getProjectName());
        // 获取git仓库
        File repoDir = new File(gitPath);
        try (Repository repository = getRepository(repoDir, request.getProjectName(), gitUser)){
            List<String> fileList = request.getPath();
            // 本地保持最新状态
            DSSGitUtils.pull(repository, request.getProjectName(), gitUser);
            // 同步删除对应节点
            for (String path : request.getPath()) {
                FileUtils.removeFlowNode(path, request.getProjectName());
            }
            // 提交
            String comment = "delete workflowNode " + request.getPath().toString() + DSSGitConstant.GIT_USERNAME_FLAG + request.getUsername();
            DSSGitUtils.push(repository, request.getProjectName(), gitUser, comment);

            commitResponse = DSSGitUtils.getCurrentCommit(repository);

        } catch (Exception e) {
            logger.error("pull failed, the reason is ",e);
        }
        return commitResponse;
    }

    @Override
    public GitCommitResponse rename(GitRenameRequest request) {
        GitUserEntity gitUser = dssWorkspaceGitService.selectGit(request.getWorkspaceId(), GitConstant.GIT_ACCESS_WRITE_TYPE);
        if (gitUser == null) {
            logger.error("the workspace : {} don't associate with git", request.getWorkspaceId());
            return null;
        }
        GitCommitResponse commitResponse = null;
        // 拼接.git路径
        String gitPath = DSSGitUtils.generateGitPath(request.getProjectName());
        // 获取git仓库
        File repoDir = new File(gitPath);
        try (Repository repository = getRepository(repoDir, request.getProjectName(), gitUser)){
            // 同步删除对应节点
            String olfFilePath = File.separator + FileUtils.normalizePath(GitServerConfig.GIT_SERVER_PATH.getValue()) + File.separator + request.getProjectName() + File.separator + FileUtils.normalizePath(request.getOldName());
            String filePath = File.separator + FileUtils.normalizePath(GitServerConfig.GIT_SERVER_PATH.getValue()) + File.separator + request.getProjectName() + File.separator + FileUtils.normalizePath(request.getName());

            String oldFileMetaPath = File.separator + FileUtils.normalizePath(GitServerConfig.GIT_SERVER_PATH.getValue()) + File.separator + request.getProjectName() + File.separator + FileUtils.normalizePath(GitServerConfig.GIT_SERVER_META_PATH.getValue()) + File.separator + FileUtils.normalizePath(request.getOldName());
            String fileMetaPath = File.separator + FileUtils.normalizePath(GitServerConfig.GIT_SERVER_PATH.getValue()) + File.separator + request.getProjectName() + File.separator + FileUtils.normalizePath(GitServerConfig.GIT_SERVER_META_PATH.getValue()) + File.separator + FileUtils.normalizePath(request.getName());
            List<String> fileList = new ArrayList<>();
            fileList.add(oldFileMetaPath);
            fileList.add(olfFilePath);
            // 本地保持最新状态
            DSSGitUtils.pull(repository, request.getProjectName(), gitUser);
            FileUtils.renameFile(olfFilePath, filePath);
            FileUtils.renameFile(oldFileMetaPath, fileMetaPath);
            // 提交
            String comment = "rename workflowNode " + request.getName() + DSSGitConstant.GIT_USERNAME_FLAG + request.getUsername();
            DSSGitUtils.push(repository, request.getProjectName(), gitUser, comment);

            commitResponse = DSSGitUtils.getCurrentCommit(repository);

        } catch (Exception e) {
            logger.error("pull failed, the reason is ",e);
        }
        return commitResponse;
    }
}
