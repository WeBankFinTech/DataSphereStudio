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

    NodeContentDO getNodeContentByKey(@Param("key") String key);

    List<NodeContentDO> getNodeContentListByOrchestratorId(@Param("orchestratorId") Long orchestratorId);

    List<Long> getContentIdListByOrchestratorId(@Param("orchestratorId") Long orchestratorId);

    void deleteNodeContentByKey(@Param("key") String key);

    void deleteNodeContentByOrchestratorId(@Param("orchestratorId") Long orchestratorId);
}
