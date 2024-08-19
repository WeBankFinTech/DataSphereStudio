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

    void batchUpdate(List<NodeContentDO> list);

    NodeContentDO getNodeContentByKey(@Param("nodeKey") String nodeKey);

    List<NodeContentDO> getNodeContentByKeyList(List<String> list);

    List<NodeContentDO> getNodeContentListByOrchestratorId(@Param("orchestratorId") Long orchestratorId);

    List<Long> getContentIdListByOrchestratorId(@Param("orchestratorId") Long orchestratorId);

    void deleteNodeContentByKey(@Param("nodeKey") String nodeKey);

    void batchDelete(List<NodeContentDO> list);

    void deleteNodeContentByOrchestratorId(@Param("orchestratorId") Long orchestratorId);
}
