package com.webank.wedatasphere.dss.git.service;


import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.git.common.protocol.request.*;
import com.webank.wedatasphere.dss.git.common.protocol.response.*;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public interface DSSGitWorkflowManagerService {
    GitDiffResponse diff(GitDiffRequest request) throws DSSErrorException;

    GitCommitResponse commit(GitCommitRequest request) throws DSSErrorException;

    GitSearchResponse search(GitSearchRequest request) throws DSSErrorException;

    GitDeleteResponse delete(GitDeleteRequest request) throws DSSErrorException;

    GitFileContentResponse getFileContent(GitFileContentRequest request) throws DSSErrorException;

    GitHistoryResponse getHistory(GitHistoryRequest request) throws DSSErrorException;

    GitCommitResponse getCurrentCommit(GitCurrentCommitRequest request) throws DSSErrorException;

    GitCommitResponse gitCheckOut(GitRevertRequest request) throws DSSErrorException;

    GitCommitResponse removeFile(GitRemoveRequest request) throws DSSErrorException;

    GitCommitResponse rename(GitRenameRequest request) throws DSSErrorException;

    GitHistoryResponse getHistory(GitCommitInfoBetweenRequest request) throws DSSErrorException;

    GitDiffResponse diffGit(GitDiffTargetCommitRequest request) throws DSSErrorException;

    GitCommitResponse batchCommit(GitBatchCommitRequest request) throws DSSErrorException;
}
