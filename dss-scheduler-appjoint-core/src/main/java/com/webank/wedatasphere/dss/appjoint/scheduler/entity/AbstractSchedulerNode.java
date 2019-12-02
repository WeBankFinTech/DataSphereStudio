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

package com.webank.wedatasphere.dss.appjoint.scheduler.entity;

import com.webank.wedatasphere.dss.common.entity.node.DWSNode;

import java.util.List;

/**
 * Created by enjoyyin on 2019/9/25.
 */
public abstract class AbstractSchedulerNode implements SchedulerNode {

    private DWSNode dwsNode;

    @Override
    public DWSNode getDWSNode() {
        return this.dwsNode;
    }

    @Override
    public void setDWSNode(DWSNode dwsNode) {
        this.dwsNode = dwsNode;
    }

    @Override
    public String getId() {
        return dwsNode.getId();
    }

    @Override
    public void setId(String id) {
        dwsNode.setId(id);
    }

    @Override
    public String getNodeType() {
        return dwsNode.getNodeType();
    }

    @Override
    public void setNodeType(String nodeType) {
        dwsNode.setNodeType(nodeType);
    }

    @Override
    public String getName() {
        return dwsNode.getName();
    }

    @Override
    public void setName(String name) {
        dwsNode.setName(name);
    }

    @Override
    public void addDependency(String nodeName) {
        dwsNode.addDependency(nodeName);
    }

    @Override
    public void setDependency(List<String> dependency) {
        dwsNode.setDependency(dependency);
    }

    @Override
    public void removeDependency(String nodeName) {
        dwsNode.removeDependency(nodeName);
    }

    @Override
    public List<String> getDependencys() {
        return dwsNode.getDependencys();
    }
}
