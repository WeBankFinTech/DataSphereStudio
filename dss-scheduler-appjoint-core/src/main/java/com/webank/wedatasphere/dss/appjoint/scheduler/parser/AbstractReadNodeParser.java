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

package com.webank.wedatasphere.dss.appjoint.scheduler.parser;

import com.webank.wedatasphere.dss.appjoint.scheduler.entity.SchedulerNode;
import com.webank.wedatasphere.dss.common.entity.node.DSSNode;
import com.webank.wedatasphere.dss.appjoint.scheduler.entity.ReadNode;

import java.util.Arrays;
import java.util.Map;

/**
 * Created by enjoyyin on 2019/10/31.
 */
public abstract class AbstractReadNodeParser implements ContextNodeParser {

    @Override
    public String[] getShareNodeIds(SchedulerNode node) {
        //需根据节点的参数进行解析生成
        Map<String, Object> jobParams =  node.getDssNode().getParams();
        if(jobParams == null) return null;
        Map<String, Object> configuration =(Map<String, Object>) jobParams.get("configuration");
        Map<String, Object> runtime = (Map<String, Object>) configuration.get("runtime");
        if(runtime.get("category").toString().equals("node")) {
            String content = runtime.get("content").toString().replace("[","").replace("]","");
            if (content != null) {
                String[] res = Arrays.stream(content.split(",")).map(String::trim).toArray(String[]::new);
                return res;
            } else {
                return null;
            }
        }else {
            return null;
        }
    }

    @Override
    public void setReadNodeContext(SchedulerNode node) {
        // TODO: 2019/10/31
    }

    @Override
    public ReadNode parseNode(SchedulerNode node) {
        ReadNode readNode = createReadNode();
        readNode.setDssNode(node.getDssNode());
        readNode.setSchedulerNode(node);
        readNode.setShareNodeIds(getShareNodeIds(node));
        return readNode;
    }

    @Override
    public SchedulerNode parseNode(DSSNode dssNode) {
        SchedulerNode schedulerNode = createSchedulerNode();
        schedulerNode.setDssNode(dssNode);
        return parseNode(schedulerNode);
    }


    protected abstract ReadNode createReadNode();

    protected abstract SchedulerNode createSchedulerNode();
}
