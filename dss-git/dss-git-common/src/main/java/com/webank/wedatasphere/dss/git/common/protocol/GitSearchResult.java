package com.webank.wedatasphere.dss.git.common.protocol;

import java.util.List;

public class GitSearchResult {
    private String path;
    private List<GitSearchLine> keyLines;

    public GitSearchResult() {
    }

    public GitSearchResult(String path, List<GitSearchLine> keyLines) {
        this.path = path;
        this.keyLines = keyLines;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<GitSearchLine> getKeyLines() {
        return keyLines;
    }

    public void setKeyLines(List<GitSearchLine> keyLines) {
        this.keyLines = keyLines;
    }
}
