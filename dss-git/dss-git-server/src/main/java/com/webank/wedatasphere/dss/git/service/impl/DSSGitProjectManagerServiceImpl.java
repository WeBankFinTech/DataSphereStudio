package com.webank.wedatasphere.dss.git.service.impl;

import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.common.service.BMLService;
import com.webank.wedatasphere.dss.git.common.protocol.GitUserEntity;
import com.webank.wedatasphere.dss.git.common.protocol.config.GitServerConfig;
import com.webank.wedatasphere.dss.git.common.protocol.constant.GitConstant;
import com.webank.wedatasphere.dss.git.common.protocol.exception.GitErrorException;
import com.webank.wedatasphere.dss.git.common.protocol.request.*;
import com.webank.wedatasphere.dss.git.common.protocol.response.*;
import com.webank.wedatasphere.dss.git.common.protocol.util.UrlUtils;
import com.webank.wedatasphere.dss.git.constant.DSSGitConstant;
import com.webank.wedatasphere.dss.git.dto.GitProjectGitInfo;
import com.webank.wedatasphere.dss.git.manage.GitProjectManager;
import com.webank.wedatasphere.dss.git.service.DSSGitProjectManagerService;
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

import javax.crypto.Cipher;
import java.io.File;
import java.io.IOException;
import java.util.*;

@Service
public class DSSGitProjectManagerServiceImpl  implements DSSGitProjectManagerService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    @Qualifier("projectBmlService")
    private BMLService bmlService;

    @Override
    public GitCreateProjectResponse create(GitCreateProjectRequest request) throws DSSErrorException{
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
        Repository repository = null;
        try {
            // Http请求Git，创建project
            DSSGitUtils.init(projectName, gitUser, gitToken, gitUrl);
            // 解压BML文件到本地
            FileUtils.downloadBMLResource(bmlService, projectName, request.getBmlResource(), request.getUsername(), workspaceId, gitUser);
            FileUtils.removeProject(projectName, workspaceId, gitUser);
            FileUtils.unzipBMLResource(projectName, workspaceId, gitUser);
            // 本地创建Git项目
            DSSGitUtils.create(projectName, workspaceId, gitUser);
            // 获取git项目
            String gitPath = DSSGitUtils.generateGitPath(projectName, workspaceId, gitUser);
            File repoDir = new File(gitPath);
            repository = new FileRepositoryBuilder().setGitDir(repoDir).build();
            // 关联远端Git
            DSSGitUtils.remote(repository, projectName, gitUser, gitUrl);
            // 提交
            String comment = "init project: " + projectName + DSSGitConstant.GIT_USERNAME_FLAG + request.getUsername();
            // 首次创建提交项目整体
            List<String> paths = Collections.singletonList(".");
            DSSGitUtils.push(repository, projectName, gitUser, gitToken, comment, paths);

            // 获取项目ProjectId
            String projectIdByName = DSSGitUtils.getProjectIdByName(projectName, gitUser, gitToken, gitUrl);
//            DSSGitUtils.addProjectMember(gitUser, readGitUser.getGitUserId(), projectIdByName, 20);
            // 存储projectId
            GitProjectManager.updateGitProjectId(projectName, projectIdByName);
            return new GitCreateProjectResponse();
        } catch (Exception e) {
            logger.error("create project failed, the reason is: ", e);
            throw new DSSErrorException(80001, "create project failed, the reason is: " + e);
        } finally {
            if (repository != null) {
                repository.close();
            }
        }
    }

    @Override
    public GitArchivePorjectResponse archive(GitArchiveProjectRequest request) throws GitErrorException, DSSErrorException {
        String projectName = request.getProjectName();

        GitProjectGitInfo projectInfoByProjectName = GitProjectManager.getProjectInfoByProjectName(projectName);
        if (projectInfoByProjectName == null) {
            logger.error("the projectName : {} don't associate with git", projectName);
            return null;
        }
        String gitUser = projectInfoByProjectName.getGitUser();
        String gitToken = projectInfoByProjectName.getGitToken();
        String gitUrl = projectInfoByProjectName.getGitUrl();
        // 远程归档
        DSSGitUtils.archive(projectName, gitUser, gitUrl, gitToken);
        // 删除本地项目
        DSSGitUtils.archiveLocal(projectName, request.getWorkspaceId());
        return new GitArchivePorjectResponse();
    }

    @Override
    public GitCheckProjectResponse checkProject(GitCheckProjectRequest request) throws DSSErrorException {
        String gitUser = request.getGitUser();
        String gitToken = request.getGitToken();
        String projectName = request.getProjectName();
        Long workspaceId = request.getWorkspaceId();
        Boolean isExist = false;
        GitProjectGitInfo projectGitInfo = GitProjectManager.getProjectInfoByProjectName(projectName);
        if (projectGitInfo != null ) {
            if (!projectGitInfo.getGitUser().equals(gitUser)) {
                throw new DSSErrorException(80001, "Git用户名不允许更换");
            }
            if (gitToken.equals(projectGitInfo.getGitToken())) {
                isExist = true;
            } else {
                String decryptToken = GitProjectManager.generateKeys(projectGitInfo.getGitToken(), Cipher.DECRYPT_MODE);
                gitToken = decryptToken;
            }
        }
        // 检测token合法性 数据库中已存在的配置无需再次校验
        Boolean tokenTest = isExist || GitProjectManager.gitTokenTest(gitToken, gitUser);
        String gitUrl = UrlUtils.normalizeIp(GitServerConfig.GIT_URL_PRE.getValue());
        if (tokenTest) {
            String projectPath = gitUser + "/" + projectName;
            // 检测项目名称是否重复 数据库中已存在的配置无需再次校验
            if (isExist || !DSSGitUtils.checkIfProjectExists(gitToken, projectPath)) {
                GitProjectGitInfo gitProjectGitInfo = new GitProjectGitInfo(workspaceId, projectName, gitUser, gitToken, gitUrl);
                GitProjectManager.insert(gitProjectGitInfo, isExist);
            }
        } else {
            throw new GitErrorException(80101, "git init failed, the reason is: projectName " + projectName +" already exists");
        }

        return new GitCheckProjectResponse(projectName, false);
    }

    @Override
    public GitUserByWorkspaceResponse getProjectGitUserInfo(GitUserByWorkspaceIdRequest request) {
        String username = request.getUsername();
        Long workspaceId = request.getWorkspaceId();

        List<GitProjectGitInfo> projectInfoByWorkspaceId = GitProjectManager.getProjectInfo(workspaceId);

        Map<String, GitUserEntity> map = new HashMap<>();
        for (GitProjectGitInfo gitInfo : projectInfoByWorkspaceId) {
            GitUserEntity gitUserEntity = new GitUserEntity(gitInfo.getGitUser(), gitInfo.getGitToken());
            map.put(gitInfo.getProjectName(), gitUserEntity);
        }

        return new GitUserByWorkspaceResponse(map);
    }

    @Override
    public GitAddMemberResponse addMember(GitAddMemberRequest request) throws IOException {
        String projectName = request.getProjectName();
        String username = request.getUsername();
        String flowNodeName = request.getFlowNodeName();
        GitProjectGitInfo projectInfoByProjectName = GitProjectManager.getProjectInfoByProjectName(projectName);
        String gitProjectId = projectInfoByProjectName.getGitProjectId();
        String gitUser = projectInfoByProjectName.getGitUser();
        String gitToken = projectInfoByProjectName.getGitToken();
        String gitUrl = projectInfoByProjectName.getGitUrl();
        String userIdByUsername = DSSGitUtils.getUserIdByUsername(gitUrl, gitToken, username);
        DSSGitUtils.addProjectMember(gitUrl, gitToken, userIdByUsername, gitProjectId, 20);
        String jumpUrl = gitUrl + "/" + gitUser + "/" + projectName;

        if (StringUtils.isNotEmpty(flowNodeName)) {
            jumpUrl += "/tree/master/" + flowNodeName;
        }
        return new GitAddMemberResponse(jumpUrl);
    }


}
