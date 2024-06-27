package com.webank.wedatasphere.dss.git.common.protocol;

import java.util.HashMap;
import java.util.Map;

public class GitTree {
    private String name;
    private Map<String, GitTree> children = new HashMap<>();

    public GitTree(String name) {
        this.name = name;
    }

    // 添加子节点
    public void addChild(String path) {
        String[] parts = path.split("/", 2);
        String currentPart = parts[0];
        String restPart = parts.length > 1 ? parts[1] : null;

        children.putIfAbsent(currentPart, new GitTree(currentPart));
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
}
