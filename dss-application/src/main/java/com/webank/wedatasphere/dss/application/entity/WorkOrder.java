package com.webank.wedatasphere.dss.application.entity;

/**
 * Created by schumiyi on 2020/8/31
 */
public class WorkOrder {
    /**
     * Id
     */
    private Long id;
    /**
     * 施工单Id
     */
    private String workOrderId;
    /**
     * 施工单类型
     */
    private String workOrderType;
    /**
     * 关联ctyun_user表用户Id
     */
    private String ctyunUserId;
    /**
     * 施工单matser Order的配置信息
     */
    private String workOrderItemConfig;
    /**
     * 施工单进度
     */
    private Integer progress = 0;
    /**
     * 施工单状态
     */
    private Boolean isSuccess = false;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWorkOrderId() {
        return workOrderId;
    }

    public void setWorkOrderId(String workOrderId) {
        this.workOrderId = workOrderId;
    }

    public String getWorkOrderType() {
        return workOrderType;
    }

    public void setWorkOrderType(String workOrderType) {
        this.workOrderType = workOrderType;
    }

    public String getCtyunUserId() {
        return ctyunUserId;
    }

    public void setCtyunUserId(String ctyunUserId) {
        this.ctyunUserId = ctyunUserId;
    }

    public Integer getProgress() {
        return progress;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
    }

    public Boolean getSuccess() {
        return isSuccess;
    }

    public void setSuccess(Boolean success) {
        isSuccess = success;
    }

    public String getWorkOrderItemConfig() {
        return workOrderItemConfig;
    }

    public void setWorkOrderItemConfig(String workOrderItemConfig) {
        this.workOrderItemConfig = workOrderItemConfig;
    }
}
