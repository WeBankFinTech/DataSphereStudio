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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * created by cooperyang on 2020/3/8
 * Description:
 */
public class VOUtils {
    public static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void addWorkspaceUser(){
        Map<String, Object> params = new HashMap<>();
        params.put("workspaceId", 12);
        params.put("username", "cooperyang");
        params.put("roles",new String[]{"分析用户","开发用户"});
        System.out.println(gson.toJson(params));
    }

    public static void deleteWorkspaceUser(){
        Map<String, Object> params = new HashMap<>();
        params.put("workspaceId", 12);
        params.put("username", "cooperyang");
        System.out.println(gson.toJson(params));
    }

    public static void addWorkspaceRole(){
        Map<String, Object> params = new HashMap<>();
        params.put("workspaceId", 123);
        params.put("roleName", "测试人员");
        params.put("menuPrivs", new String[]{"菜单栏-生产中心", "菜单栏-生产中心-调度中心"});
        params.put("componentPrivs", new String[]{"数据分析组件-Scriptis", "数据分析组件-Visualis"});
        System.out.println(gson.toJson(params));
    }

    public static void updateRoleMenuPriv(){
        Map<String, Object> params = new HashMap<>();
        params.put("workspaceId", 123);
        params.put("menuName", "菜单栏-生产中心");
        Map<String, Boolean> data = new HashMap<>();
        data.put("管理员", true);
        data.put("领导", true);
        data.put("开发人员", false);
        data.put("运维人员", true);
        data.put("分析人员", false);
        data.put("运营人员", false);
        params.put("menuPrivs", data);
        System.out.println(gson.toJson(params));
    }

    public static void rollbackWorkFlow(){
        Map<String, Object> params = new HashMap<>();
        params.put("workspaceId", 123);
        params.put("workflowId", 789);
        params.put("rollbackVersion", "v0002");
        System.out.println(gson.toJson(params));
    }


    public static void createCooperateProject(){
        Map<String, Object> params = new HashMap<>();
        params.put("workspaceId", 123);
        params.put("projectName", "工程名字");
        params.put("applicationDomain", "运营优化");
        params.put("business", "微粒贷");
        params.put("description", "工程的描述");
        params.put("editUsers", Arrays.asList("cooperyang", "chaogefeng"));
        params.put("accessUsers", Arrays.asList("cooperyang", "chaogefeng", "enjoyyin"));
        System.out.println(gson.toJson(params));
    }

    public static void addPublicTable(){
        Map<String, Object> params = new HashMap<>();
        params.put("workspaceId", 123);
        params.put("dbName", "default");
        params.put("tableName", "aaa");
        params.put("privilege", "700");
        System.out.println(VOUtils.gson.toJson(params));
    }


    public static void releaseWorkflow(){
        Map<String, Object> params = new HashMap<>();
        params.put("workspaceId", 123);
        params.put("projectId", 15);
        params.put("projectVersion", "v00001");
        params.put("workflowId", 456);
        params.put("workflowVersion", "v0012");
        System.out.println(gson.toJson(params));
    }


    public static void main(String args[]){
        //addWorkspaceUser();
        //deleteWorkspaceUser();
        //addWorkspaceRole();
        //updateRoleMenuPriv();
        //rollbackWorkFlow();
        //createCooperateProject();
        //addPublicTable();
        releaseWorkflow();
    }


}
