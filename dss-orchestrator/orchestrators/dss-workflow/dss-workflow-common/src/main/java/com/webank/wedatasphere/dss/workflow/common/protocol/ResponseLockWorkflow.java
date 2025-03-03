package com.webank.wedatasphere.dss.workflow.common.protocol;


public class ResponseLockWorkflow {
    public static final int LOCK_SUCCESS = 0;
    public static final int LOCK_FAILED = 1;

    private int unlockStatus;
    private String lockOwner;

    public ResponseLockWorkflow(int unlockStatus, String lockOwner) {
        this.unlockStatus = unlockStatus;
        this.lockOwner = lockOwner;
    }

    public ResponseLockWorkflow() {
    }

    public int getUnlockStatus() {
        return unlockStatus;
    }

    public void setUnlockStatus(int unlockStatus) {
        this.unlockStatus = unlockStatus;
    }

    public String getLockOwner() {
        return lockOwner;
    }

    public void setLockOwner(String lockOwner) {
        this.lockOwner = lockOwner;
    }
}
