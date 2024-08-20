package com.webank.wedatasphere.dss.workflow.dao;

import com.webank.wedatasphere.dss.workflow.dto.NodeContentDO;
import feign.Param;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface NodeContentMapper {
    void insert(NodeContentDO nodeContentDO);

    void batchInsert(List<NodeContentDO> list);

    void update(NodeContentDO nodeContentDO);

    void updateByKey(NodeContentDO nodeContentDO);

    NodeContentDO getNodeContentByKey(@Param("nodeKey") String nodeKey);

    List<NodeContentDO> getNodeContentByKeyList(@Param("list") List<String> list, @Param("orchestratorId") Long orchestratorId);

    List<NodeContentDO> getNodeContentListByOrchestratorId(@Param("orchestratorId") Long orchestratorId);

    List<Long> getContentIdListByOrchestratorId(@Param("orchestratorId") Long orchestratorId);

    void deleteNodeContentByKey(@Param("nodeKey") String nodeKey, @Param("orchestratorId") Long orchestratorId);

    void batchDelete(@Param("list") List<NodeContentDO> list, @Param("orchestratorId") Long orchestratorId);

    void deleteNodeContentByOrchestratorId(@Param("orchestratorId") Long orchestratorId);
}
