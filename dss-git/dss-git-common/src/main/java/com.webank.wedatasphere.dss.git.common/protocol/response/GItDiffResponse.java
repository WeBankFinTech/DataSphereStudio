package com.webank.wedatasphere.dss.git.common.protocol.response;


import com.webank.wedatasphere.dss.git.common.protocol.GitTree;

public class GItDiffResponse{
    private GitTree tree;

    public GItDiffResponse(GitTree tree) {
        this.tree = tree;
    }

    public GItDiffResponse() {
    }

    public GitTree getTree() {
        return tree;
    }

    public void setTree(GitTree tree) {
        this.tree = tree;
    }
}
