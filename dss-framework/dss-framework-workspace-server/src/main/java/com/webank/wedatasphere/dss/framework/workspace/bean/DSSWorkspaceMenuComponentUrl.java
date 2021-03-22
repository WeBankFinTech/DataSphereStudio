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

package com.webank.wedatasphere.dss.framework.workspace.bean;

import java.util.Date;

/**
 * created by cooperyang on 2020/3/17
 * Description:
 */
public class DSSWorkspaceMenuComponentUrl {
    private int id;
    private int menuId;
    private int dssApplicationId;
    private String url;
    private String manulUrl;
    private String operationUrl;
    private Date updateTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMenuId() {
        return menuId;
    }

    public void setMenuId(int menuId) {
        this.menuId = menuId;
    }

    public int getDssApplicationId() {
        return dssApplicationId;
    }

    public void setDssApplicationId(int dssApplicationId) {
        this.dssApplicationId = dssApplicationId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getManulUrl() {
        return manulUrl;
    }

    public void setManulUrl(String manulUrl) {
        this.manulUrl = manulUrl;
    }

    public String getOperationUrl() {
        return operationUrl;
    }

    public void setOperationUrl(String operationUrl) {
        this.operationUrl = operationUrl;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
