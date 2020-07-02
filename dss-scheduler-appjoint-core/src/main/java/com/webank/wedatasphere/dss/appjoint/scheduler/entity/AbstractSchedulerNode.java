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

import com.webank.wedatasphere.dss.common.entity.node.DSSNode;

import java.util.List;

/**
 * Created by enjoyyin on 2019/9/25.
 */
public abstract class AbstractSchedulerNode implements SchedulerNode {

    private DSSNode dssNode;

    @Override
    public DSSNode getDssNode() {
        return this.dssNode;
    }

    @Override
    public void setDssNode(DSSNode dssNode) {
        this.dssNode = dssNode;
    }

    @Override
    public String getId() {
        return dssNode.getId();
    }

    @Override
    public void setId(String id) {
        dssNode.setId(id);
    }

    @Override
    public String getNodeType() {
        return dssNode.getNodeType();
    }

    @Override
    public void setNodeType(String nodeType) {
        dssNode.setNodeType(nodeType);
    }

    @Override
    public String getName() {
        return dssNode.getName();
    }

    @Override
    public void setName(String name) {
        dssNode.setName(name);
    }

    @Override
    public void addDependency(String nodeName) {
        dssNode.addDependency(nodeName);
    }

    @Override
    public void setDependency(List<String> dependency) {
        dssNode.setDependency(dependency);
    }

    @Override
    public void removeDependency(String nodeName) {
        dssNode.removeDependency(nodeName);
    }

    @Override
    public List<String> getDependencys() {
        return dssNode.getDependencys();
    }
}
