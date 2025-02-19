package com.webank.wedatasphere.dss.git.common.protocol.request;


import java.util.List;

public class GitDeleteRequest extends GitBaseRequest{
    /**
     * 提交更新时的comment
     */
    private String comment;
    /**
     * 需删除的文件列表
     */
    private List<String> deleteFileList;

    public GitDeleteRequest() {
    }

    public GitDeleteRequest(Long workspaceId, String projectName) {
        super(workspaceId, projectName);
    }

    public GitDeleteRequest(String comment, List<String> deleteFileList) {
        this.comment = comment;
        this.deleteFileList = deleteFileList;
    }

    public GitDeleteRequest(Long workspaceId, String projectName, String comment, List<String> deleteFileList) {
        super(workspaceId, projectName);
        this.comment = comment;
        this.deleteFileList = deleteFileList;
    }

    public List<String> getDeleteFileList() {
        return deleteFileList;
    }

    public void setDeleteFileList(List<String> deleteFileList) {
        this.deleteFileList = deleteFileList;
    }

}
