package com.webank.wedatasphere.dss.workflow.dao;

import com.webank.wedatasphere.dss.workflow.dto.NodeMetaDO;
import feign.Param;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface NodeMetaMapper {
    void insertNodeMeta(NodeMetaDO nodeMetaDO);

    void updateNodeMeta(NodeMetaDO nodeMetaDO);

    NodeMetaDO getNodeMetaByOrchestratorId(@Param ("orchestratorId") Long orchestratorId);

    void deleteNodeMetaByOrchestratorId(@Param ("orchestratorId") Long orchestratorId);
}
