package com.webank.wedatasphere.dss.git.common.protocol.response;

public class GitConnectResponse {
    /**
     * token-用户名是否匹配 true-匹配 false-不匹配
     */
    private Boolean connect;

    public GitConnectResponse(Boolean connect) {
        this.connect = connect;
    }

    public GitConnectResponse() {
    }

    public Boolean getConnect() {
        return connect;
    }

    public void setConnect(Boolean connect) {
        this.connect = connect;
    }
}
