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

package com.webank.wedatasphere.dss.framework.workspace.bean.vo;

import java.util.ArrayList;
import java.util.List;

/**
 * created by cooperyang on 2020/3/8
 * Description:
 */
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

    public static void main(String[] args){
        List<DSSWorkspaceMenuVO> ret = new ArrayList<>();
        List<SecondaryWorkspaceMenuVO> tempList = new ArrayList<>();
        DSSWorkspaceMenuVO vo1 = new DSSWorkspaceMenuVO();
        vo1.setName("生产中心");
        vo1.setId(1);
        tempList = new ArrayList<>();
        SecondaryWorkspaceMenuVO v1 = new SecondaryWorkspaceMenuVO(1, "调度中心", "/api/xx/getScheduleWorkflows");
        tempList.add(v1);
        vo1.setSubMenus(tempList);
        ret.add(vo1);
        DSSWorkspaceMenuVO vo2 = new DSSWorkspaceMenuVO();
        vo2.setName("开发中心");
        vo2.setId(2);
        tempList = new ArrayList<>();
        SecondaryWorkspaceMenuVO v2 = new SecondaryWorkspaceMenuVO(1, "工作流开发", "/api/xx/getxxx");
        SecondaryWorkspaceMenuVO v3 = new SecondaryWorkspaceMenuVO(2, "实时应用", "/api/xx/xxx");
        tempList.add(v2);
        tempList.add(v3);
        vo2.setSubMenus(tempList);
        ret.add(vo2);
        System.out.println(VOUtils.gson.toJson(ret));
    }


}
