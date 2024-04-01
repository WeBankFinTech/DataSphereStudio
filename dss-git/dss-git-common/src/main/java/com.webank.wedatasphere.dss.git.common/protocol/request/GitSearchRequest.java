package com.webank.wedatasphere.dss.git.common.protocol.request;

import java.util.List;

public class GitSearchRequest extends GitBaseRequest{
    /**
     * 搜索条件：工作流（相对路径）
     */
    private List<String> path;
    /**
     * 搜索条件：关键字
     */
    private String searchContent;
    /**
     * 搜索条件：文件名
     */
    private String fileName;
    /**
     * 搜索条件：节点类型（后缀）
     */
    private List<String> type;
    /**
     * 分页条件：第一页无需关心，下一页需携带上一页最后一个文件名
     */
    private String currentPageLastFile;

    public GitSearchRequest() {
    }

    public GitSearchRequest(List<String> path, String searchContent, String fileName, List<String> type, String currentPageLastFile) {
        this.path = path;
        this.searchContent = searchContent;
        this.fileName = fileName;
        this.type = type;
        this.currentPageLastFile = currentPageLastFile;
    }

    public GitSearchRequest(Long workspaceId, String projectName, List<String> path, String searchContent, String fileName, List<String> type, String currentPageLastFile) {
        super(workspaceId, projectName);
        this.path = path;
        this.searchContent = searchContent;
        this.fileName = fileName;
        this.type = type;
        this.currentPageLastFile = currentPageLastFile;
    }


    public List<String> getPath() {
        return path;
    }

    public void setPath(List<String> path) {
        this.path = path;
    }

    public String getSearchContent() {
        return searchContent;
    }

    public void setSearchContent(String searchContent) {
        this.searchContent = searchContent;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public List<String> getType() {
        return type;
    }

    public void setType(List<String> type) {
        this.type = type;
    }

    public String getCurrentPageLastFile() {
        return currentPageLastFile;
    }

    public void setCurrentPageLastFile(String currentPageLastFile) {
        this.currentPageLastFile = currentPageLastFile;
    }
}
