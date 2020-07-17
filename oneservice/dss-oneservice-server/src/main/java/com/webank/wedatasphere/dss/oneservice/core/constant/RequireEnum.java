/*
 * Copyright 2019 WeBank
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
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
package com.webank.wedatasphere.dss.oneservice.core.constant;

/**
 * @author chongchuanbing
 */
public enum RequireEnum {

    YES(1, "是"),

    NO(0, "否");

    private Integer index;
    private String name;

    RequireEnum(Integer index, String name) {
        this.index = index;
        this.name = name;
    }

    public static RequireEnum getEnum(Integer index) {
        if (index == null) {
            return NO;
        }
        for (RequireEnum statusEnum : values()) {
            if (statusEnum.getIndex().equals(index)) {
                return statusEnum;
            }
        }
        return NO;
    }

    public Integer getIndex() {
        return this.index;
    }
    
    public String getName() {
        return this.name;
    }
}
