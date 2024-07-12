package com.webank.wedatasphere.dss.git.common.protocol.response;


import com.webank.wedatasphere.dss.common.entity.BmlResource;

public class GitFileContentResponse{
    // 提交前 发布前 文件内容
    private String before;
    // 提交后 发布后 文件内容
    private String after;

    public GitFileContentResponse(String before, String after) {
        this.before = before;
        this.after = after;
    }

    public GitFileContentResponse() {
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
}
