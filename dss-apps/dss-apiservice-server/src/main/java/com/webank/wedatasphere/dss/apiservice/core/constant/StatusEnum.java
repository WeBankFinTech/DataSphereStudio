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
package com.webank.wedatasphere.dss.apiservice.core.constant;

/**
 * @author chongchuanbing
 */
public enum StatusEnum {

    DISABLE(0, "停止"),

    ENABLE(1, "运行中");

    private Integer index;
    private String name;

    StatusEnum(Integer index, String name) {
        this.index = index;
        this.name = name;
    }

    public static StatusEnum getEnum(Integer index) {
        if (index == null) {
            return null;
        }
        for (StatusEnum statusEnum : values()) {
            if (statusEnum.getIndex().equals(index)) {
                return statusEnum;
            }
        }
        return null;
    }

    public Integer getIndex() {
        return this.index;
    }
    
    public String getName() {
        return this.name;
    }
}
