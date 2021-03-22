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

import java.util.Arrays;

/**
 * created by cooperyang on 2020/3/8
 * Description:
 */
public class DSSDatasourceVO {
    private int id;
    private String name;
    private String type;
    private String createTime;
    private String env;
    private String creator;
    private String responser;
    private String lastUpdater;
    private String lastUpdateTime;

    public DSSDatasourceVO() {
    }

    public DSSDatasourceVO(int id, String name, String type, String createTime, String env, String creator, String responser, String lastUpdater, String lastUpdateTime) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.createTime = createTime;
        this.env = env;
        this.creator = creator;
        this.responser = responser;
        this.lastUpdater = lastUpdater;
        this.lastUpdateTime = lastUpdateTime;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getResponser() {
        return responser;
    }

    public void setResponser(String responser) {
        this.responser = responser;
    }

    public String getLastUpdater() {
        return lastUpdater;
    }

    public void setLastUpdater(String lastUpdater) {
        this.lastUpdater = lastUpdater;
    }

    public String getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public static void main(String[] args){
        DSSDatasourceVO vo1 =
                new DSSDatasourceVO(1, "default.aaa", "HIVE", "2020-03-08 20:15:00", "BDP测试", "enjoyyin", "enjoyyin", "cooperyang", "2020-03-08 20:17:45");
        DSSDatasourceVO vo2 =
                new DSSDatasourceVO(1, "default.bbb", "TDSQL", "2020-03-08 20:15:00", "BDP测试", "enjoyyin", "enjoyyin", "cooperyang", "2020-03-08 20:17:45");

        System.out.println(VOUtils.gson.toJson(Arrays.asList(vo1, vo2)));

    }


}
