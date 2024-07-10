package com.webank.wedatasphere.dss.git.common.protocol.response;


import com.webank.wedatasphere.dss.git.common.protocol.GitTree;

import java.util.List;

public class GitDiffResponse {
    private List<GitTree> tree;

    public GitDiffResponse(List<GitTree> tree) {
        this.tree = tree;
    }

    public List<GitTree> getTree() {
        return tree;
    }

    public void setTree(List<GitTree> tree) {
        this.tree = tree;
    }
}
