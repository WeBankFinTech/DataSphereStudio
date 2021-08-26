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

package com.webank.wedatasphere.dss.apiservice.core.constant;




public enum SQLMetadataInfoCheckStatus {
    LEGAL(0, "该SQL查询合法"), ILLEGALSQL(1,"不支持无库表信息的SQL查询");

    int index;
    String desc;

    SQLMetadataInfoCheckStatus(int index, String desc) {
        this.index = index;
        this.desc = desc;
    }

    public String getDescByIndex(int index) {
        for (SQLMetadataInfoCheckStatus item : values()) {
            if (item.getIndex() == index) {
                return item.getDesc();
            }
        }
        return null;
    }

    public int getIndex() {
        return index;
    }

    public String getDesc() {
        return desc;
    }
}
