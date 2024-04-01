package com.webank.wedatasphere.dss.git.common.protocol.response;


import com.webank.wedatasphere.dss.common.entity.BmlResource;

public class GitFileContentResponse{
    private BmlResource bmlResource;

    public GitFileContentResponse(BmlResource bmlResource) {
        this.bmlResource = bmlResource;
    }

    public GitFileContentResponse() {
    }

    public BmlResource getBmlResource() {
        return bmlResource;
    }

    public void setBmlResource(BmlResource bmlResource) {
        this.bmlResource = bmlResource;
    }
}
