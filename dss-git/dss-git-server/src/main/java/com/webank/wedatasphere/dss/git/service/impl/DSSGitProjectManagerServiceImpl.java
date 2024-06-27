package com.webank.wedatasphere.dss.git.service.impl;

import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.common.service.BMLService;
import com.webank.wedatasphere.dss.git.common.protocol.GitUserEntity;
import com.webank.wedatasphere.dss.git.common.protocol.constant.GitConstant;
import com.webank.wedatasphere.dss.git.common.protocol.exception.GitErrorException;
import com.webank.wedatasphere.dss.git.common.protocol.request.*;
import com.webank.wedatasphere.dss.git.common.protocol.response.*;
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
        GitUserEntity gitUser = GitProjectManager.selectGit(workspaceId, GitConstant.GIT_ACCESS_WRITE_TYPE, true);
        if (gitUser == null) {
            logger.error("the workspace : {} don't associate with git", workspaceId);
            return null;
        }
        Repository repository = null;
        try {
            // Http请求Git，创建project
            DSSGitUtils.init(request.getProjectName(), gitUser);
            // 解压BML文件到本地
            FileUtils.downloadBMLResource(bmlService, request.getProjectName(), request.getBmlResource(), request.getUsername(), workspaceId);
            FileUtils.removeProject(request.getProjectName(), workspaceId);
            FileUtils.unzipBMLResource(request.getProjectName(), workspaceId);
            // 本地创建Git项目
            DSSGitUtils.create(request.getProjectName(), gitUser, workspaceId);
            // 获取git项目
            String gitPath = DSSGitUtils.generateGitPath(request.getProjectName(), workspaceId);
            File repoDir = new File(gitPath);
            repository = new FileRepositoryBuilder().setGitDir(repoDir).build();
            // 关联远端Git
            DSSGitUtils.remote(repository, request.getProjectName(), gitUser);
            // 提交
            String comment = "init project: " + request.getProjectName() + DSSGitConstant.GIT_USERNAME_FLAG + request.getUsername();
            // 首次创建提交项目整体
            List<String> paths = Collections.singletonList(".");
            DSSGitUtils.push(repository, request.getProjectName(), gitUser, comment, paths);
            // 获取工作空间只读用户
            GitUserEntity readGitUser = GitProjectManager.selectGit(workspaceId, GitConstant.GIT_ACCESS_READ_TYPE, true);
            if (readGitUser == null) {
                throw new DSSErrorException(80001, "只读用户不能为空，需完成工作空间制度用户设置");
            }
            // 获取项目ProjectId
            String projectIdByName = DSSGitUtils.getProjectIdByName(gitUser, request.getProjectName());
            DSSGitUtils.addProjectMember(gitUser, readGitUser.getGitUserId(), projectIdByName, 20);
            // 存储projectId
            GitProjectGitInfo projectGitInfo = new GitProjectGitInfo();
            projectGitInfo.setGitProjectId(projectIdByName);
            projectGitInfo.setProjectName(request.getProjectName());
            projectGitInfo.setWorkspaceId(workspaceId);
            GitProjectManager.insert(projectGitInfo);
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
    public GitArchivePorjectResponse archive(GitArchiveProjectRequest request) throws GitErrorException {
        GitUserEntity gitUser = GitProjectManager.selectGit(request.getWorkspaceId(), GitConstant.GIT_ACCESS_WRITE_TYPE, true);
        if (gitUser == null) {
            logger.error("the workspace : {} don't associate with git", request.getWorkspaceId());
            return null;
        }
        // 远程归档
        DSSGitUtils.archive(request.getProjectName(), gitUser);
        // 删除本地项目
        DSSGitUtils.archiveLocal(request.getProjectName(), request.getWorkspaceId());
        return new GitArchivePorjectResponse();
    }

    @Override
    public GitCheckProjectResponse checkProject(GitCheckProjectRequest request) throws DSSErrorException {
        GitUserEntity gitUser = GitProjectManager.selectGit(request.getWorkspaceId(), GitConstant.GIT_ACCESS_WRITE_TYPE, true);
        if (gitUser == null) {
            logger.error("the workspace : {} don't associate with git", request.getWorkspaceId());
            return null;
        }
        String projectPath = gitUser.getGitUser() + "/" + request.getProjectName();
        boolean b = DSSGitUtils.checkIfProjectExists(gitUser, projectPath);
        logger.info("checkProjectName result is : {}", b);
        return new GitCheckProjectResponse(request.getProjectName(), b);
    }


}
