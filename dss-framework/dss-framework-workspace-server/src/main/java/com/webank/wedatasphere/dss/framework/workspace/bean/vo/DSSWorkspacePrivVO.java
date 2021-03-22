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

import java.io.Serializable;
import java.util.List;

/**
 * created by cooperyang on 2020/3/8
 * Description:
 */
public class DSSWorkspacePrivVO implements Serializable {
    private static final long serialVersionUID=1L;
    private int workspaceId;
    private List<DSSWorkspaceMenuPrivVO> menuPrivVOS;
    private List<DSSWorkspaceComponentPrivVO> componentPrivVOS;
    private List<DSSWorkspaceRoleVO> roleVOS;

    public List<DSSWorkspaceRoleVO> getRoleVOS() {
        return roleVOS;
    }

    public void setRoleVOS(List<DSSWorkspaceRoleVO> roleVOS) {
        this.roleVOS = roleVOS;
    }




    public DSSWorkspacePrivVO() {
    }

    public DSSWorkspacePrivVO(int workspaceId, List<DSSWorkspaceMenuPrivVO> menuPrivVOS, List<DSSWorkspaceComponentPrivVO> componentPrivVOS,List<DSSWorkspaceRoleVO> roleVOS) {
        this.workspaceId = workspaceId;
        this.menuPrivVOS = menuPrivVOS;
        this.componentPrivVOS = componentPrivVOS;
        this.roleVOS=roleVOS;
    }

    public int getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(int workspaceId) {
        this.workspaceId = workspaceId;
    }

    public List<DSSWorkspaceMenuPrivVO> getMenuPrivVOS() {
        return menuPrivVOS;
    }

    public void setMenuPrivVOS(List<DSSWorkspaceMenuPrivVO> menuPrivVOS) {
        this.menuPrivVOS = menuPrivVOS;
    }

    public List<DSSWorkspaceComponentPrivVO> getComponentPrivVOS() {
        return componentPrivVOS;
    }

    public void setComponentPrivVOS(List<DSSWorkspaceComponentPrivVO> componentPrivVOS) {
        this.componentPrivVOS = componentPrivVOS;
    }

    @Override
    public String toString() {
        return "DSSWorkspacePrivVO{" +
                "workspaceId=" + workspaceId +
                ", menuPrivVOS=" + menuPrivVOS +
                ", componentPrivVOS=" + componentPrivVOS +
                ", roleVOS=" + roleVOS +
                '}';
    }

    public static void main(String args[]){
//        DSSWorkspaceMenuPrivVO dssWorkspaceMenuPrivVO1 = new DSSWorkspaceMenuPrivVO(1,"菜单栏-生产中心",null);
//        Map<String, Boolean> map = new HashMap<>();
//        map.put("管理员", true);
//        map.put("领导", false);
//        map.put("开发用户", false);
//        map.put("运维用户", true);
//        map.put("分析用户", false);
//        map.put("运营用户", false);
//        dssWorkspaceMenuPrivVO1.setMenuPrivs(map);
//        DSSWorkspaceMenuPrivVO dssWorkspaceMenuPrivVO2 = new DSSWorkspaceMenuPrivVO(2,"菜单栏-开发中心",null);
//        Map<String, Boolean> map1 = new HashMap<>();
//        map1.put("管理员", true);
//        map1.put("领导", false);
//        map1.put("开发用户", true);
//        map1.put("运维用户", false);
//        map1.put("分析用户", false);
//        map1.put("运营用户", false);
//        dssWorkspaceMenuPrivVO2.setMenuPrivs(map1);
//
//        DSSWorkspaceComponentPrivVO dssWorkspaceComponentPrivVO1 = new DSSWorkspaceComponentPrivVO(1,"数据开发组件-Scriptis", null);
//        Map<String, Boolean> map2 = new HashMap<>();
//        map2.put("管理员", true);
//        map2.put("领导", false);
//        map2.put("开发用户", false);
//        map2.put("运维用户", false);
//        map2.put("分析用户", true);
//        map2.put("运营用户", false);
//        dssWorkspaceComponentPrivVO1.setComponentPrivs(map2);
//
//        DSSWorkspaceComponentPrivVO dssWorkspaceComponentPrivVO2 = new DSSWorkspaceComponentPrivVO(2,"应用开发组件-工作流开发", null);
//        Map<String, Boolean> map3 = new HashMap<>();
//        map3.put("管理员", true);
//        map3.put("领导", false);
//        map3.put("开发用户", true);
//        map3.put("运维用户", false);
//        map3.put("分析用户", false);
//        map3.put("运营用户", false);
//        dssWorkspaceComponentPrivVO2.setComponentPrivs(map3);
//        List<DSSWorkspaceMenuPrivVO> list1 = Arrays.asList(dssWorkspaceMenuPrivVO1, dssWorkspaceMenuPrivVO2);
//        List<DSSWorkspaceComponentPrivVO> list2 =  Arrays.asList(dssWorkspaceComponentPrivVO1, dssWorkspaceComponentPrivVO2);
//        //DSSWorkspacePrivVO dssWorkspacePrivVO = new DSSWorkspacePrivVO(12, list1, list2);
//        //System.out.println(VOUtils.gson.toJson(dssWorkspacePrivVO));
    }


}
