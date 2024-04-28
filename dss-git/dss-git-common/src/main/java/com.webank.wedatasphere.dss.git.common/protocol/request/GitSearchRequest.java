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
    private Integer pageSize;
    private Integer pageNow;

    public GitSearchRequest() {
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


    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPageNow() {
        return pageNow;
    }

    public void setPageNow(Integer pageNow) {
        this.pageNow = pageNow;
    }
}
