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
import java.util.List;

/**
 * created by cooperyang on 2020/3/5
 * Description:
 */
public class DSSPersonalWorkflowVO extends AbstractDSSVO{
    private int workflowId;
    private String name;
    private String json;
    private String description;
    private List<String> tags;

    public DSSPersonalWorkflowVO() {
    }

    public DSSPersonalWorkflowVO(int workflowId, String name, String json, String description, List<String> tags) {
        this.workflowId = workflowId;
        this.name = name;
        this.json = json;
        this.description = description;
        this.tags = tags;
    }

    public int getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(int workflowId) {
        this.workflowId = workflowId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }


    public static void main(String[] args){
        DSSPersonalWorkflowVO vo1 =
                new DSSPersonalWorkflowVO(789, "工作流1", "工作流json1", "这是一个demo工作流1", Arrays.asList("微粒贷", "日报"));
        DSSPersonalWorkflowVO vo2 =
                new DSSPersonalWorkflowVO(456, "工作流2", "工作流json2", "这是一个demo工作流2", Arrays.asList("微车贷", "利润"));
        System.out.println(VOUtils.gson.toJson(Arrays.asList(vo1, vo2)));
    }
}
