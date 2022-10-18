/*
 * Copyright 2019 WeBank
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.webank.wedatasphere.dss.workflow.common.protocol;


public class ResponseUnlockWorkflow {
    public static final int UNLOCK_SUCCESS = 0;
    public static final int NONEED_UNLOCK = 1;
    public static final int NEED_SECOND_CONFIRM = 2;

    private int unlockStatus;
    private String lockOwner;

    public ResponseUnlockWorkflow(int unlockStatus, String lockOwner) {
        this.unlockStatus = unlockStatus;
        this.lockOwner = lockOwner;
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
