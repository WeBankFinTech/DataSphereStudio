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

/**
 * created by cooperyang on 2020/3/8
 * Description:
 */
public class DSSWorkspaceHomePageVO {
    private int workspaceId;
    private String roleName;
    private String homePageUrl;

    public DSSWorkspaceHomePageVO() {
    }

    public DSSWorkspaceHomePageVO(int workspaceId, String roleName, String username, String homePageUrl) {
        this.workspaceId = workspaceId;
        this.roleName = roleName;
        this.homePageUrl = homePageUrl;
    }


    public int getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(int workspaceId) {
        this.workspaceId = workspaceId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }



    public String getHomePageUrl() {
        return homePageUrl;
    }

    public void setHomePageUrl(String homePageUrl) {
        this.homePageUrl = homePageUrl;
    }

    public static void main(String[] args){
        DSSWorkspaceHomePageVO vo = new DSSWorkspaceHomePageVO(123, "分析用户", "cooperyang", "http://sss");
        System.out.println(VOUtils.gson.toJson(vo));
    }

}
