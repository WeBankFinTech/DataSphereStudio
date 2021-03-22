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

package com.webank.wedatasphere.dss.standard.common.desc;

/**
 * created by cooperyang on 2020/10/16
 * Description: 环境标签的枚举
 */
public enum EnvironmentLabelEnum {

    /**
     * 通用DSS的label环境标签
     * 一般有开发 测试 和 生产
     */
    DEV(1,"dev"),
    TEST(2, "test"),
    PROD(2, "prod");

    private int index;
    private String name;

    private EnvironmentLabelEnum(int index, String name){
        this.name = name;
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }
}
