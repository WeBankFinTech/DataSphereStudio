package com.webank.wedatasphere.dss.orchestrator.server.entity.vo;

public class OrchestratorUnlockVo extends CommonOrchestratorVo {
    /**
     * 已锁定用户
     */
    private String lockOwner;
    /**
     * 提示信息
     */
    private String confirmMessage;
    /**
     * 0：解锁成功，1：需用户二次确认解锁
     */
    private int status;

    public OrchestratorUnlockVo(String lockOwner, String confirmMessage, int status) {
        this.lockOwner = lockOwner;
        this.confirmMessage = confirmMessage;
        this.status = status;
    }

    public String getLockOwner() {
        return lockOwner;
    }

    public void setLockOwner(String lockOwner) {
        this.lockOwner = lockOwner;
    }

    public String getConfirmMessage() {
        return confirmMessage;
    }

    public void setConfirmMessage(String confirmMessage) {
        this.confirmMessage = confirmMessage;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
