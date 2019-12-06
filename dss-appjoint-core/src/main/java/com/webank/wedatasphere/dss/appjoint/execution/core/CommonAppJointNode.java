/*
 * Copyright 2019 WeBank
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.webank.wedatasphere.dss.appjoint.execution.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * created by enjoyyin on 2019/10/9
 * Description:
 */
public class CommonAppJointNode extends AbstractAppJointNode{

    private Map<String, Object> params;


    private List<String> dependencys = new ArrayList<>();

    public CommonAppJointNode(String projectName, long projectId , String flowName, long flowId, String nodeName,
                              String type, Map<String, Object> jobContent){
        super(projectName, projectId, flowName, flowId, nodeName, type, jobContent);
    }

    public CommonAppJointNode(){

    }


    public void setParams(Map<String, Object> params){
        this.params = params;
    }




    @Override
    public void addDependency(String nodeName) {
        this.dependencys.add(nodeName);
    }

    @Override
    public void setDependency(List<String> dependencys) {
        this.dependencys = dependencys;
    }

    @Override
    public void removeDependency(String nodeName) {
        this.dependencys.remove(nodeName);
    }

    @Override
    public List<String> getDependencys() {
        return this.dependencys;
    }
}
