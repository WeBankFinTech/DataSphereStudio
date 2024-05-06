package com.webank.wedatasphere.dss.git.common.protocol;

import java.util.List;

public class GitSearchResult {
    private String path;
    private List<String> keyLines;

    public GitSearchResult() {
    }

    public GitSearchResult(String path, List<String> keyLines) {
        this.path = path;
        this.keyLines = keyLines;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<String> getKeyLines() {
        return keyLines;
    }

    public void setKeyLines(List<String> keyLines) {
        this.keyLines = keyLines;
    }
}
