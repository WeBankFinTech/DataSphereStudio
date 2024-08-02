package com.webank.wedatasphere.dss.git.service;

import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.git.common.protocol.exception.GitErrorException;
import com.webank.wedatasphere.dss.git.common.protocol.request.*;
import com.webank.wedatasphere.dss.git.common.protocol.response.*;

import java.util.concurrent.ExecutionException;

public interface DSSGitProjectManagerService {
    GitCreateProjectResponse create(GitCreateProjectRequest request) throws DSSErrorException;

    GitArchivePorjectResponse archive(GitArchiveProjectRequest request) throws GitErrorException, DSSErrorException;

    GitCheckProjectResponse checkProject(GitCheckProjectRequest request) throws DSSErrorException;

}
