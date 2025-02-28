package com.webank.wedatasphere.dss.git.common.protocol.response;


public class GitBaseResponse {
    private String status;
    private int index;
    private String message;

    public GitBaseResponse(String status, int index, String message) {
        this.status = status;
        this.index = index;
        this.message = message;
    }

    public GitBaseResponse() {
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
