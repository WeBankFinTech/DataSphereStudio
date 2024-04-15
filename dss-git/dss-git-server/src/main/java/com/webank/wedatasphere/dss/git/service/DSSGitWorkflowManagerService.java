package com.webank.wedatasphere.dss.git.service;


import com.webank.wedatasphere.dss.git.common.protocol.request.*;
import com.webank.wedatasphere.dss.git.common.protocol.response.*;

import java.util.concurrent.ExecutionException;

public interface DSSGitWorkflowManagerService {
    GItDiffResponse diff(GitDiffRequest request) throws ExecutionException, InterruptedException;

    GitCommitResponse commit(GitCommitRequest request) throws ExecutionException, InterruptedException;

    GitSearchResponse search(GitSearchRequest request);

    GitDeleteResponse delete(GitDeleteRequest request) throws ExecutionException, InterruptedException;

    GitFileContentResponse getFileContent(GitFileContentRequest request);

    GitHistoryResponse getHistory(GitHistoryRequest request);
}
