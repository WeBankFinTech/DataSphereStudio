package com.webank.wedatasphere.dss.workflow.dao;

import com.webank.wedatasphere.dss.workflow.dto.NodeContentDO;

import com.webank.wedatasphere.dss.workflow.entity.DSSFlowNodeInfo;
import com.webank.wedatasphere.dss.workflow.entity.DSSFlowNodeTemplate;
import org.apache.ibatis.annotations.Param;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface NodeContentMapper {
    void insert(NodeContentDO nodeContentDO);

    void batchInsert(List<NodeContentDO> list);

    void update(NodeContentDO nodeContentDO);

    void updateByKey(NodeContentDO nodeContentDO);

    NodeContentDO getNodeContentByKey(@Param("nodeKey") String nodeKey);

    List<NodeContentDO> getNodeContentByKeyList(@Param("list") List<String> list, @Param("orchestratorId") Long orchestratorId, @Param("flowId") Long flowId);

    List<NodeContentDO> getNodeContentListByOrchestratorId(@Param("orchestratorId") Long orchestratorId, @Param("flowId") Long flowId);

    List<NodeContentDO> getContentListByOrchestratorId(@Param("orchestratorId") Long orchestratorId, @Param("flowId") Long flowId);

    List<NodeContentDO> getContentListByFlowId(@Param("flowId") Long flowId);

    void deleteNodeContentByKey(@Param("nodeKey") String nodeKey, @Param("orchestratorId") Long orchestratorId, @Param("flowId") Long flowId);

    void batchDelete(@Param("list") List<NodeContentDO> list, @Param("orchestratorId") Long orchestratorId, @Param("flowId") Long flowId);

    void deleteNodeContentByOrchestratorId(@Param("orchestratorId") Long orchestratorId, @Param("flowId") Long flowId);

    void updateFlowId(@Param("flowId") Long flowId, @Param("oldFlowId") Long oldFlowId);

    void deleteNodeContentByFlowId(@Param("flowId") Long flowId);

    List<DSSFlowNodeInfo> queryFlowNodeInfo(@Param("projectIdList") List<Long> projectIdList,@Param("nodeTypeList") List<String> nodeTypeList);

    List<DSSFlowNodeTemplate> queryFlowNodeTemplate(@Param("orchestratorIdList") List<Long> orchestratorIdList);

    NodeContentDO getNodeContentById(@Param("contentId") Long contentId,@Param("nodeId") String nodeId);
}
