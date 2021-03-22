/*
 *
 *  * Copyright 2019 WeBank
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.webank.wedatasphere.dss.framework.release.job;

/**
 * created by cooperyang on 2020/12/9
 * Description:
 */
public enum ReleaseStatus {

    /**
     * 发布任务的执行的状态
     */
    INIT(1, "init"),

    RUNNING(2, "running"),

    SUCCESS(3, "success"),

    FAILED(-1, "failed");

    private int index;

    private String status;

    ReleaseStatus(int index, String status){
        this.index = index;
        this.status = status;
    }

    public int getIndex() {
        return index;
    }

    public String getStatus() {
        return status;
    }
}
