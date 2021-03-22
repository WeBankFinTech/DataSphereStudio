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

import java.util.HashMap;
import java.util.Map;

/**
 * created by cooperyang on 2020/3/8
 * Description:
 */
public class DSSWorkspaceScriptisStatiticVO extends DSSWorkspaceStatisticVO{
    private String name;

    private String status;

    private Map<String, Object> statistics;

    public DSSWorkspaceScriptisStatiticVO() {
    }

    public DSSWorkspaceScriptisStatiticVO(String name, String status, Map<String, Object> statistics) {
        this.name = name;
        this.status = status;
        this.statistics = statistics;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Map<String, Object> getStatistics() {
        return statistics;
    }

    public void setStatistics(Map<String, Object> statistics) {
        this.statistics = statistics;
    }

    public static void main(String[] args){
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("脚本", 50);
        statistics.put("查询", 550);
        statistics.put("个人库", "60%");
        statistics.put("共享目录", "8G");
        DSSWorkspaceStatisticVO dssWorkspaceStatisticVO = new DSSWorkspaceScriptisStatiticVO("scriptis", "运行中", statistics);
        System.out.println(VOUtils.gson.toJson(dssWorkspaceStatisticVO));
    }

}
