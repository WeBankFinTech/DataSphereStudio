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

package com.webank.wedatasphere.dss.workflow.entity;

import java.util.HashMap;

public class AppMapFlowNode {
    private String id;
    private String desc;
    private String businessTag;
    private String appTag;
    private String key;
    private String title;
    
    private String isRootFlow;

    public String getIsRootFlow() {
        return isRootFlow;
    }

    public void setIsRootFlow(String isRootFlow) {
        this.isRootFlow = isRootFlow;
    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getBusinessTag() {
        return businessTag;
    }

    public void setBusinessTag(String businessTag) {
        this.businessTag = businessTag;
    }

    public String getAppTag() {
        return appTag;
    }

    public void setAppTag(String appTag) {
        this.appTag = appTag;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public AppMapProjectInfo getProjectData() {
        return projectData;
    }

    public void setProjectData(AppMapProjectInfo projectData) {
        this.projectData = projectData;
    }

    private AppMapProjectInfo projectData;
    private HashMap<String,Object> layout=new HashMap<String ,Object>(){{
     put("height",120);
     put("width",200);
     put("x","");
     put("y","");}
    };
}
