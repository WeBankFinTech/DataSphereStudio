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

package com.webank.wedatasphere.dss.framework.workspace.util;


public enum CommonRoleEnum {
    /**
     * 通用角色的枚举
     */
    ADMIN(1, "admin", "管理员"),
    MAINTENANCE(2, "maintaince","运维人员"),
    DEVELOPER(3, "developer", "开发人员"),
    ANALYSER(4, "analyser", "分析用户"),
    OPERATOR(5, "operator", "运营用户"),
    BOSS(6, "boss", "领导");

    private int id;
    private String name;
    private String frontName;

    private CommonRoleEnum(int id, String name, String frontName){
        this.id = id;
        this.name = name;
        this.frontName = frontName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFrontName() {
        return frontName;
    }

    public void setFrontName(String frontName) {
        this.frontName = frontName;
    }
}
