package com.webank.wedatasphere.dss.workflow.dao;

import com.webank.wedatasphere.dss.workflow.dto.NodeContentDO;

import com.webank.wedatasphere.dss.workflow.entity.DSSFlowNodeInfo;
import com.webank.wedatasphere.dss.workflow.entity.DSSFlowNodeInfoOfFlow;
import com.webank.wedatasphere.dss.workflow.entity.DSSFlowNodeTemplate;
import com.webank.wedatasphere.dss.workflow.entity.StarRocksNodeInfo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface NodeContentMapper {
    void insert(NodeContentDO nodeContentDO);

    void batchInsert(List<NodeContentDO> list);

    void update(NodeContentDO nodeContentDO);

    void updateByKey(NodeContentDO nodeContentDO);

    NodeContentDO getNodeContentByKey(@Param("nodeKey") String nodeKey);

    NodeContentDO getNodeContentByContentId(@Param("id") Long id);

    List<NodeContentDO> getNodeContentByKeyList(@Param("list") List<String> list, @Param("orchestratorId") Long orchestratorId, @Param("flowId") Long flowId);

    List<NodeContentDO> getNodeContentListByOrchestratorId(@Param("orchestratorId") Long orchestratorId);

    List<NodeContentDO> getContentListByOrchestratorIdAndFlowId(@Param("orchestratorId") Long orchestratorId, @Param("flowId") Long flowId);

    List<NodeContentDO> getContentListByFlowId(List<Long> list);

    void deleteNodeContentByKey(@Param("nodeKey") String nodeKey, @Param("orchestratorId") Long orchestratorId, @Param("flowId") Long flowId);

    void batchDelete(@Param("list") List<NodeContentDO> list, @Param("orchestratorId") Long orchestratorId, @Param("flowId") Long flowId);

    void deleteNodeContentByOrchestratorIdAndFlowId(@Param("orchestratorId") Long orchestratorId, @Param("flowId") Long flowId);

    void deleteNodeContentByOrchestratorId(@Param("orchestratorId") Long orchestratorId);

    void updateFlowId(@Param("flowId") Long flowId, @Param("oldFlowId") Long oldFlowId);

    void deleteNodeContentByFlowId(List<Long> list);

    List<DSSFlowNodeInfo> queryFlowNodeInfo(@Param("projectIdList") List<Long> projectIdList,@Param("nodeTypeList") List<String> nodeTypeList);

    List<DSSFlowNodeTemplate> queryFlowNodeTemplate(@Param("orchestratorIdList") List<Long> orchestratorIdList);

    NodeContentDO getNodeContentById(@Param("contentId") Long contentId,@Param("nodeId") String nodeId);
    @Select(
            "SELECT t2.flow_id AS appId,t2.orchestrator_id AS orchestratorId " +
                    "FROM dss_orchestrator_info t1 " +
                    "INNER JOIN dss_workflow_node_content t2 ON t1.id = t2.orchestrator_id " +
                    "INNER JOIN dss_workflow_node_content_to_ui t3 ON t2.id = t3.content_id " +
                    "WHERE t1.project_id = #{projectId} " +
                    "AND t1.name = #{orchestratorName} " +
                    "AND t3.node_ui_key = 'title' " +
                    "AND t3.node_ui_value = #{nodeName} LIMIT 1"
    )
    DSSFlowNodeInfoOfFlow getNodeInfoOfFLow(@Param("projectId") Long projectId,
                                                            @Param("orchestratorName")String orchestratorName,
                                                            @Param("nodeName")String nodeName);

    @Select(
        "SELECT t3.workspace_id, t3.project_id, t3.id AS orchestrator_id, t2.id AS node_content_id, t2.node_key, t1.node_ui_key, t1.node_ui_value, t1.node_type FROM dss_workflow_node_content_to_ui t1\n" +
                "LEFT JOIN dss_workflow_node_content t2 ON t1.content_id = t2.id\n" +
                "LEFT JOIN dss_orchestrator_info t3 ON t2.orchestrator_id = t3.id\n" +
                "WHERE t1.node_type = 'linkis.jdbc.starrocks' AND t3.workspace_id = #{workspaceId} AND t1.node_ui_key in ('executeCluster','ReuseEngine', 'auto.disabled') "
    )
    List<StarRocksNodeInfo> queryStarRocksNodeInfo(@Param("workspaceId") Long workspaceId);
}
