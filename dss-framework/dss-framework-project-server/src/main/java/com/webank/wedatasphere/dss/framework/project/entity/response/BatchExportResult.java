package com.webank.wedatasphere.dss.framework.project.entity.response;

import com.webank.wedatasphere.dss.common.entity.BmlResource;

import java.util.List;

/**
 * Author: xlinliu
 * Date: 2022/9/9
 */
public class BatchExportResult {
    /**
     * 导出文件上传后的bml资源
     */
    private BmlResource bmlResource;

    /**
     * 导出文件的md5校验码
     */
    private String checkSum;

    public BatchExportResult(BmlResource bmlResource, String checkSum) {
        this.bmlResource = bmlResource;
        this.checkSum = checkSum;
    }

    public BmlResource getBmlResource() {
        return bmlResource;
    }

    public void setBmlResource(BmlResource bmlResource) {
        this.bmlResource = bmlResource;
    }

    public String getCheckSum() {
        return checkSum;
    }

    public void setCheckSum(String checkSum) {
        this.checkSum = checkSum;
    }
}
