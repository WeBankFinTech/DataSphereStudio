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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * created by cooperyang on 2019/10/9
 * Description:
 */
public class CommonAppConnNode extends AbstractAppConnNode {

    private Map<String, Object> params;

    public CommonAppConnNode(String projectName, long projectId , String flowName, long flowId, String nodeName,
                             String type, Map<String, Object> jobContent){
        super(projectName, projectId, flowName, flowId, nodeName, type, jobContent);
    }

    public CommonAppConnNode(){

    }


    public void setParams(Map<String, Object> params){
        this.params = params;
    }

    public Map<String, Object> getParams(){
        return this.params;
    }

}
