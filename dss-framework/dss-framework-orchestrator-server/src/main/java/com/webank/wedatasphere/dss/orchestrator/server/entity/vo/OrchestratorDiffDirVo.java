package com.webank.wedatasphere.dss.orchestrator.server.entity.vo;

import com.webank.wedatasphere.dss.git.common.protocol.GitTree;

import java.util.List;

public class OrchestratorDiffDirVo {
    private List<GitTree> codeTree;
    private List<GitTree> metaTree;
    private String commitId;

    public OrchestratorDiffDirVo(List<GitTree> codeTree, List<GitTree> metaTree, String commitId) {
        this.codeTree = codeTree;
        this.metaTree = metaTree;
        this.commitId = commitId;
    }

    public OrchestratorDiffDirVo() {
    }

    public List<GitTree> getCodeTree() {
        return codeTree;
    }

    public void setCodeTree(List<GitTree> codeTree) {
        this.codeTree = codeTree;
    }

    public List<GitTree> getMetaTree() {
        return metaTree;
    }

    public void setMetaTree(List<GitTree> metaTree) {
        this.metaTree = metaTree;
    }

    public String getCommitId() {
        return commitId;
    }

    public void setCommitId(String commitId) {
        this.commitId = commitId;
    }
}
