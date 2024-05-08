package com.webank.wedatasphere.dss.git.service.impl;

import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.common.service.BMLService;
import com.webank.wedatasphere.dss.git.common.protocol.GitUserEntity;
import com.webank.wedatasphere.dss.git.common.protocol.constant.GitConstant;
import com.webank.wedatasphere.dss.git.common.protocol.request.*;
import com.webank.wedatasphere.dss.git.common.protocol.response.*;
import com.webank.wedatasphere.dss.git.common.protocol.config.GitServerConfig;
import com.webank.wedatasphere.dss.git.service.DSSGitProjectManagerService;
import com.webank.wedatasphere.dss.git.service.DSSWorkspaceGitService;
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

@Service
public class DSSGitProjectManagerServiceImpl  implements DSSGitProjectManagerService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DSSWorkspaceGitService dssWorkspaceGitService;

    @Autowired
    @Qualifier("projectBmlService")
    private BMLService bmlService;

    @Override
    public GitCreateProjectResponse create(GitCreateProjectRequest request) {
        logger.info("-------=======================beginning to create testGit1=======================-------");
        GitUserEntity gitUser = dssWorkspaceGitService.selectGit(request.getWorkspaceId(), GitConstant.GIT_ACCESS_WRITE_TYPE);
        if (gitUser == null) {
            logger.error("the workspace : {} don't associate with git", request.getWorkspaceId());
            return null;
        }
        Repository repository;
        try {
            // Http请求Git，创建project
            DSSGitUtils.init(request.getProjectName(), gitUser);
            // 解压BML文件到本地
            FileUtils.removeProject(request.getProjectName());
            FileUtils.update(bmlService, request.getProjectName(), request.getBmlResource(), request.getUsername());
            // 本地创建Git项目
            DSSGitUtils.create(request.getProjectName(), gitUser);
            // 获取git项目
            File repoDir = new File(File.separator + FileUtils.normalizePath(GitServerConfig.GIT_SERVER_PATH.getValue()) + File.separator + request.getProjectName() + File.separator +".git");
            repository = new FileRepositoryBuilder().setGitDir(repoDir).build();
            // 关联远端Git
            DSSGitUtils.remote(repository, request.getProjectName(), gitUser);
            // 提交
            DSSGitUtils.push(repository, request.getProjectName(), gitUser, "init project: " + request.getProjectName() + "by " + request.getUsername());
            return new GitCreateProjectResponse();
        } catch (Exception e) {
            logger.error("create project failed, the reason is: ", e);
//            throw new DSSErrorException(010001, "create project failed");
        }
        return null;
    }

    @Override
    public GitArchivePorjectResponse archive(GitArchiveProjectRequest request) {
        GitUserEntity gitUser = dssWorkspaceGitService.selectGit(request.getWorkspaceId(), GitConstant.GIT_ACCESS_WRITE_TYPE);
        if (gitUser == null) {
            logger.error("the workspace : {} don't associate with git", request.getWorkspaceId());
            return null;
        }
        // 远程归档
        DSSGitUtils.archive(request.getProjectName(), gitUser);
        // 删除本地项目
        DSSGitUtils.archiveLocal(request.getProjectName());
        return null;
    }

    @Override
    public GitCheckProjectResponse checkProject(GitCheckProjectRequest request) throws DSSErrorException {
        GitUserEntity gitUser = dssWorkspaceGitService.selectGit(request.getWorkspaceId(), GitConstant.GIT_ACCESS_WRITE_TYPE);
        if (gitUser == null) {
            logger.error("the workspace : {} don't associate with git", request.getWorkspaceId());
            return null;
        }
        boolean b = DSSGitUtils.checkProjectName(request.getProjectName(), gitUser);
        logger.info("checkProjectName result is : {}", b);
        return new GitCheckProjectResponse(request.getProjectName(), b);
    }


}
