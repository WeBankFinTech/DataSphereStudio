package com.webank.wedatasphere.dss.git.common.protocol;

import java.util.HashMap;
import java.util.Map;

public class GitTree {
    private String name;
    // 标识当前变动路径状态 modified-修改 missing-删除 untracked-新增
    private String status;
    private Boolean meta;
    private Map<String, GitTree> children = new HashMap<>();

    public GitTree(String name) {
        this.name = name;
    }

    public GitTree(String name, Boolean meta) {
        this.name = name;
        this.meta = meta;
    }

    // 添加子节点
    public void addChild(String path, String status) {
        String[] parts = path.split("/", 2);
        String currentPart = parts[0];
        String restPart = parts.length > 1 ? parts[1] : null;

        GitTree gitTree = new GitTree(currentPart);
        // 标识当前文件状态
        if (restPart == null) {
            gitTree.setStatus(status);
        }
        children.putIfAbsent(currentPart, gitTree);
        GitTree child = children.get(currentPart);

        if (restPart != null) {
            child.addChild(restPart, status);
        }
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, GitTree> getChildren() {
        return children;
    }

    public void setChildren(Map<String, GitTree> children) {
        this.children = children;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getMeta() {
        return meta;
    }

    public void setMeta(Boolean meta) {
        this.meta = meta;
    }
}
