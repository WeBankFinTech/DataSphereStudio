package com.webank.wedatasphere.dss.appconn.dolphinscheduler.tuning;

import java.io.IOException;
import java.util.*;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.wedatasphere.dss.appconn.dolphinscheduler.entity.DolphinSchedulerFlow;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.entity.DolphinSchedulerNode;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.entity.DolphinSchedulerTask;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.linkisjob.NodeConverter;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.utils.DolphinAppConnUtils;
import com.webank.wedatasphere.dss.appconn.schedule.core.entity.SchedulerEdge;
import com.webank.wedatasphere.dss.appconn.schedule.core.entity.SchedulerFlow;
import com.webank.wedatasphere.dss.appconn.schedule.core.entity.SchedulerNode;
import com.webank.wedatasphere.dss.appconn.schedule.core.tuning.AbstractFlowTuning;
import com.webank.wedatasphere.dss.appconn.schedule.core.tuning.NodeTuning;

/**
 * The type Linkis dolphin scheduler flow tuning.
 *
 * @author yuxin.yuan
 * @date 2021/04/29
 */
public class LinkisDolphinSchedulerFlowTuning extends AbstractFlowTuning {

    private static final Logger logger = LoggerFactory.getLogger(LinkisDolphinSchedulerFlowTuning.class);

    private static NodeConverter nodeConverter = new NodeConverter();

    @Override
    public SchedulerFlow tuningSchedulerFlow(SchedulerFlow schedulerFlow) {
        DolphinSchedulerFlow dolphinSchedulerFlow = (DolphinSchedulerFlow)schedulerFlow;
        super.tuningSchedulerFlow(dolphinSchedulerFlow);

        String contextID = dolphinSchedulerFlow.getContextID();
        String user = null;
        try {
            String value = DolphinAppConnUtils.getValueFromJsonString(contextID, "value");
            user = DolphinAppConnUtils.getValueFromJsonString(value, "user");
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        DolphinSchedulerFlow.ProcessDefinitionJson processDefinitionJson
            = new DolphinSchedulerFlow.ProcessDefinitionJson();
        Map<String, DolphinSchedulerFlow.LocationInfo> locations = new HashMap<>();

        List<LinkedHashMap<String, String>> globalParams = ((ArrayList<LinkedHashMap<String, String>>) new Gson().fromJson(schedulerFlow.getJsonFlow().getJson(), Map.class).get("props")).stream().filter(stringStringLinkedHashMap -> stringStringLinkedHashMap.containsKey("user.to.proxy")).collect(Collectors.toList());

        for (SchedulerNode node : schedulerFlow.getSchedulerNodes()) {
            DolphinSchedulerNode dolphinSchedulerSchedulerNode = (DolphinSchedulerNode)node;
            dolphinSchedulerSchedulerNode.setUser(user);
            LinkedHashMap<String, String> variableMap = (LinkedHashMap<String, String>) node.getDSSNode().getParams().get("variable");
            globalParams.forEach(variableMap::putAll);

            DolphinSchedulerTask dolphinSchedulerTask = dolphinSchedulerSchedulerNode.toDolphinSchedulerTask(
                nodeConverter);
            processDefinitionJson.addTask(dolphinSchedulerTask);

            DolphinSchedulerFlow.LocationInfo locationInfo = new DolphinSchedulerFlow.LocationInfo();
            locationInfo.setName(node.getName());
            locationInfo.setTargetarr(StringUtils.join(node.getDependencys(), ","));
            locationInfo.setX((int)dolphinSchedulerSchedulerNode.getDSSNode().getLayout().getX());
            locationInfo.setY((int)dolphinSchedulerSchedulerNode.getDSSNode().getLayout().getY());
            locations.put(node.getId(), locationInfo);
        }

        List<SchedulerEdge> schedulerEdges = dolphinSchedulerFlow.getSchedulerEdges();
        List<DolphinSchedulerFlow.Connect> connects = new LinkedList<>();
        for (SchedulerEdge edge : schedulerEdges) {
            connects.add(
                new DolphinSchedulerFlow.Connect(edge.getDSSEdge().getSource(), edge.getDSSEdge().getTarget()));
        }

        dolphinSchedulerFlow.setProcessDefinitionJson(processDefinitionJson);
        dolphinSchedulerFlow.setLocations(locations);
        dolphinSchedulerFlow.setConnects(connects);

        return dolphinSchedulerFlow;
    }

    @Override
    public NodeTuning[] getNodeTunings() {
        return new NodeTuning[0];
    }

    @Override
    public Boolean ifFlowCanTuning(SchedulerFlow schedulerFlow) {
        // TODO: 2019/9/30 应该加上node 是linkisazkabanNode的 instance判断，因为加到尾部节点的就是一个linkisazkabanNode
        return schedulerFlow instanceof DolphinSchedulerFlow;
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
