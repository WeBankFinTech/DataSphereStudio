package com.webank.wedatasphere.dss.git.common.protocol.response;

public class GitFileContentResponse{
    // 提交前 发布前 文件内容
    private String before;
    // 提交后 发布后 文件内容
    private String after;
    // 反显注释内容 --仅发布时diff需要
    private String annotate;
    // 反显CommitId --仅发布时diff需要
    private String commitId;


    public GitFileContentResponse() {
    }

    public GitFileContentResponse(String before, String after, String annotate, String commitId) {
        this.before = before;
        this.after = after;
        this.annotate = annotate;
        this.commitId = commitId;
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

    public String getAnnotate() {
        return annotate;
    }

    public void setAnnotate(String annotate) {
        this.annotate = annotate;
    }

    public String getCommitId() {
        return commitId;
    }

    public void setCommitId(String commitId) {
        this.commitId = commitId;
    }
}
