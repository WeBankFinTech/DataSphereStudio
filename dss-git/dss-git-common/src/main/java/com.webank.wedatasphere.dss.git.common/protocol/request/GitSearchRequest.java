package com.webank.wedatasphere.dss.git.common.protocol.request;

import java.util.List;

public class GitSearchRequest extends GitBaseRequest{
    /**
     * 搜索条件：工作流（相对路径）
     */
    private List<String> workflowNameList;
    /**
     * 搜索条件：关键字
     */
    private String searchContent;
    /**
     * 搜索条件：文件名
     */
    private String nodeName;
    /**
     * 搜索条件：节点类型（后缀）
     */
    private List<String> typeList;
    private Integer pageSize;
    private Integer pageNow;

    public GitSearchRequest() {
    }


    public List<String> getWorkflowNameList() {
        return workflowNameList;
    }

    public void setWorkflowNameList(List<String> workflowNameList) {
        this.workflowNameList = workflowNameList;
    }

    public String getSearchContent() {
        return searchContent;
    }

    public void setSearchContent(String searchContent) {
        this.searchContent = searchContent;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public List<String> getTypeList() {
        return typeList;
    }

    public void setTypeList(List<String> typeList) {
        this.typeList = typeList;
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
