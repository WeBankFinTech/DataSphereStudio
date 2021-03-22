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

package com.webank.wedatasphere.dss.framework.project.entity;

/**
 * created by cooperyang on 2020/12/16
 * Description:
 */
public enum ProcessType {

    /**
     * 开发流程，首先保证开发中心和生产中心
     */
    DEV_CENTER(1, "dev_center", "开发中心"),
    PROD_CENTER(2, "prod_center", "生产中心");

    private ProcessType(int index, String name, String chName){
        this.index = index;
        this.name = name;
        this.chName = chName;
    }

    private int index;
    private String name;
    private String chName;

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    public String getChName() {
        return chName;
    }
}
