package com.webank.wedatasphere.dss.git.common.protocol.response;


import com.webank.wedatasphere.dss.git.common.protocol.GitTree;

public class GitDiffResponse {
    private GitTree tree;

    public GitDiffResponse(GitTree tree) {
        this.tree = tree;
    }

    public GitDiffResponse() {
    }

    public GitTree getTree() {
        return tree;
    }

    public void setTree(GitTree tree) {
        this.tree = tree;
    }
}
