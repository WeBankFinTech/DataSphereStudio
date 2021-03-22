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

package com.webank.wedatasphere.dss.apiservice.core.datamap;

import com.webank.wedatasphere.dss.apiservice.core.constant.ParamTypeEnum;

/**
 * created by cooperyang on 2020/8/27
 * Description:
 */
public enum DataMapStatus {
    /**
     * datamap审批单的状态
     */
    INITED(1, "inited", "提单成功"),
    APPROVING(2, "approving", "审批中"),
    SUCCESS(3, "success", "审批通过"),
    FAILED(4, "failed", "审批不通过"),
    REJECT(5, "reject", "审批驳回");

    private DataMapStatus(int index, String value, String desc) {
        this.index = index;
        this.value = value;
        this.desc = desc;
    }

    private int index;
    private String value;
    private String desc;


    public static String getDescByIndex(int index) {
        for (DataMapStatus dataMapStatus : values()) {
            if (dataMapStatus.getIndex() == index) {
                return dataMapStatus.desc;
            }
        }
        return INITED.desc;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
