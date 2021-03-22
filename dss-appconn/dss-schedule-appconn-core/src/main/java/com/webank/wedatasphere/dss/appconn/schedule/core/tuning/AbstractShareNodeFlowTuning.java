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

package com.webank.wedatasphere.dss.appconn.schedule.core.tuning;

import com.webank.wedatasphere.dss.appconn.schedule.core.entity.ReadNode;
import com.webank.wedatasphere.dss.appconn.schedule.core.entity.SchedulerFlow;
import com.webank.wedatasphere.dss.appconn.schedule.core.entity.SchedulerNode;
import com.webank.wedatasphere.dss.appconn.schedule.core.entity.ShareNode;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by v_wbjftang on 2019/10/31.
 */
public abstract class AbstractShareNodeFlowTuning extends AbstractFlowTuning implements ContextShareableFlowTuning {

    @Override
    public SchedulerFlow tuningSchedulerFlow(SchedulerFlow schedulerFlow) {
        List<SchedulerNode> schedulerNodes = schedulerFlow.getSchedulerNodes();
        List<SchedulerNode> tuningSchedulerNode = new ArrayList<>();
        List<SchedulerNode> readNodeList =  getReadNodes(schedulerFlow);
        ReadNode[] schedulerNodes1 = new ReadNode[readNodeList.size()];
        Map<ShareNode, Integer> shareNodeMap = getShareNodes(schedulerFlow,readNodeList.toArray(schedulerNodes1));
        schedulerNodes.stream().forEach(x ->{
            Map.Entry<ShareNode, Integer> findShareNode = shareNodeMap.entrySet().stream().filter(y ->
                    (y.getKey().getName()).equals(x.getName())
            ).findFirst().orElse(null);
            if(findShareNode != null ){
                tuningSchedulerNode.add(findShareNode.getKey());

            }else {
                tuningSchedulerNode.add(x);
            }
        });
        schedulerFlow.setSchedulerNodes(tuningSchedulerNode);
        return super.tuningSchedulerFlow(schedulerFlow);
    }

    @Override
    public List<SchedulerNode> getReadNodes(SchedulerFlow flow) {
        List<SchedulerNode> res =  flow.getSchedulerNodes().stream().filter(x -> x instanceof ReadNode).collect(Collectors.toList());
        return res;
    }

    @Override
    public Map<ShareNode, Integer> getShareNodes(SchedulerFlow flow, ReadNode[] readNodes) {
        List<String> list = new ArrayList<>();
        Map<ShareNode, Integer> res = new HashMap<>();
        //遍历readNodes，将NodeIds转为name的集合,过滤掉删除了节点但是还滞留在content里面的id
        Arrays.stream(readNodes).filter(rn ->rn.getShareNodeIds() != null).forEach(rn ->{
            List<String> names = Arrays.stream(rn.getShareNodeIds()).filter(id->flow.getSchedulerNodes().stream().filter(sn -> id.equals(sn.getId())).findFirst().isPresent()).
                    map(id -> flow.getSchedulerNodes().stream().filter(sn -> id.equals(sn.getId())).findFirst().get().getName()).collect(Collectors.toList());
            rn.setShareNodeIds(names.toArray(new String[0]));
        });
        Stream.of(readNodes).forEach(x ->
                {
                    if(x.getShareNodeIds() != null) {
                        list.addAll(Arrays.asList(x.getShareNodeIds()));
                    }
                }
        );
        Map<String,Long> nameAndNumMap = list.stream().collect(Collectors.groupingBy(Function.identity(),Collectors.counting()));
        nameAndNumMap.keySet().forEach(key ->{

            SchedulerNode schedulerNode = flow.getSchedulerNodes().stream().filter(x->x.getName().equals(key.toString())).findFirst().orElse(null);
            if(schedulerNode != null) {
                int shareTimes = (nameAndNumMap.get(key)).intValue();
                ShareNode shareNode = createShareNode();
                shareNode.setDSSNode(schedulerNode.getDSSNode());
                shareNode.setSchedulerNode(schedulerNode);
                shareNode.setShareTimes(shareTimes);
                res.put(shareNode, shareTimes);
            }
        });
        return res;
    }

    protected abstract ShareNode createShareNode();
}
