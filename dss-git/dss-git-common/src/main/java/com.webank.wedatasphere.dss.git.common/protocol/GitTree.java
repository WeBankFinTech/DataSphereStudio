package com.webank.wedatasphere.dss.git.common.protocol;

import java.util.HashMap;
import java.util.Map;

public class GitTree {
    private String name;
    private Boolean meta;
    // 仅子节点拥有该属性 文件相对路径
    private String absolutePath;
    private Map<String, GitTree> children = new HashMap<>();

    public GitTree(String name) {
        this.name = name;
    }

    public GitTree(String name, Boolean meta) {
        this.name = name;
        this.meta = meta;
    }

    // 添加子节点
    public void addChild(String path) {
        String[] parts = path.split("/", 2);
        String currentPart = parts[0];
        String restPart = parts.length > 1 ? parts[1] : null;

        GitTree gitTree = new GitTree(currentPart);
        // 标识当前文件相对路径
        if (restPart == null) {
            gitTree.setAbsolutePath(this.absolutePath);
        }
        children.putIfAbsent(currentPart, gitTree);
        GitTree child = children.get(currentPart);

        if (restPart != null) {
            child.addChild(restPart);
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

    public Boolean getMeta() {
        return meta;
    }

    public void setMeta(Boolean meta) {
        this.meta = meta;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }
}
