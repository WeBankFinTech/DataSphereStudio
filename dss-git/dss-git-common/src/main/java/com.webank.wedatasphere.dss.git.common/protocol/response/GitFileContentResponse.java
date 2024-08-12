package com.webank.wedatasphere.dss.git.common.protocol.response;

public class GitFileContentResponse{
    // 提交前 发布前 文件内容
    private String before;
    // 提交后 发布后 文件内容
    private String after;
    // 反显注释内容 --仅发布时diff需要
    private String beforeAnnotate;
    // 反显CommitId --仅发布时diff需要
    private String beforeCommitId;
    // 反显注释内容 --仅发布时diff需要
    private String AfterAnnotate;
    // 反显CommitId --仅发布时diff需要
    private String AfterCommitId;


    public GitFileContentResponse() {
    }

    public GitFileContentResponse(String before, String after, String beforeAnnotate, String beforeCommitId, String afterAnnotate, String afterCommitId) {
        this.before = before;
        this.after = after;
        this.beforeAnnotate = beforeAnnotate;
        this.beforeCommitId = beforeCommitId;
        AfterAnnotate = afterAnnotate;
        AfterCommitId = afterCommitId;
    }


    public String getBefore() {
        return before;
    }

    public void setBefore(String before) {
        this.before = before;
    }

    public String getAfter() {
        return after;
    }

    public void setAfter(String after) {
        this.after = after;
    }

    public String getBeforeAnnotate() {
        return beforeAnnotate;
    }

    public void setBeforeAnnotate(String beforeAnnotate) {
        this.beforeAnnotate = beforeAnnotate;
    }

    public String getBeforeCommitId() {
        return beforeCommitId;
    }

    public void setBeforeCommitId(String beforeCommitId) {
        this.beforeCommitId = beforeCommitId;
    }

    public String getAfterAnnotate() {
        return AfterAnnotate;
    }

    public void setAfterAnnotate(String afterAnnotate) {
        AfterAnnotate = afterAnnotate;
    }

    public String getAfterCommitId() {
        return AfterCommitId;
    }

    public void setAfterCommitId(String afterCommitId) {
        AfterCommitId = afterCommitId;
    }
}
