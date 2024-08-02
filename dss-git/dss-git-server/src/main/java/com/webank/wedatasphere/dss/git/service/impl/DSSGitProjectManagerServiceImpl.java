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
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
            FileUtils.unzipBMLResource(projectName, workspaceId);
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
            isExist = true;
            if (!projectGitInfo.getGitUser().equals(gitUser)) {
                throw new DSSErrorException(80001, "Git用户名不允许更换");
            }
        }
        Boolean tokenTest;
        String gitUrl = UrlUtils.normalizeIp(GitServerConfig.GIT_URL_PRE.getValue());
        if (isExist && projectGitInfo.getGitToken().equals(gitToken)) {
            tokenTest = true;
        } else {
            // 检测token合法性 数据库中已存在的配置无需再次校验
            tokenTest = GitProjectManager.gitTokenTest(gitToken, gitUser);
        }
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


}
