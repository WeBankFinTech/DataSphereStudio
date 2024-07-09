package com.webank.wedatasphere.dss.workflow.dao;


import com.webank.wedatasphere.dss.workflow.dto.NodeContentUIDO;
import feign.Param;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface NodeContentUIMapper {
    void insertNodeContentUI(NodeContentUIDO contentUIDO);

    void batchInsertNodeContentUI(List<NodeContentUIDO> list);

    void updateNodeContentUI(NodeContentUIDO contentUIDO);

    NodeContentUIDO getNodeContentUI(@Param("orchestratorId") Long orchestratorId);

    void batchUpdateNodeContentUI(NodeContentUIDO contentUIDO);

    void deleteNodeContentUIByContentList(@Param("contentList") List<Long> contentList);
}
