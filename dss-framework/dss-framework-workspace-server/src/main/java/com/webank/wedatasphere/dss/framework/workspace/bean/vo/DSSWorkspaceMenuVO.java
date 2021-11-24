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

package com.webank.wedatasphere.dss.framework.workspace.bean.vo;

import java.util.ArrayList;
import java.util.List;


public class DSSWorkspaceMenuVO {
    private int id;
    private String name;
    private String icon;
    private List<SecondaryWorkspaceMenuVO> subMenus;

    public DSSWorkspaceMenuVO(){

    }

    public DSSWorkspaceMenuVO(int id, String name, List<SecondaryWorkspaceMenuVO> subMenus) {
        this.id = id;
        this.name = name;
        this.subMenus = subMenus;
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

    public List<SecondaryWorkspaceMenuVO> getSubMenus() {
        return subMenus;
    }

    public void setSubMenus(List<SecondaryWorkspaceMenuVO> subMenus) {
        this.subMenus = subMenus;
    }


    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }




}
