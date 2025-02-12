package com.webank.wedatasphere.dss.workflow.dao;


import com.webank.wedatasphere.dss.workflow.dto.NodeContentUIDO;

import org.apache.ibatis.annotations.Param;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.Set;

@Mapper
public interface NodeContentUIMapper {
    void insertNodeContentUI(NodeContentUIDO contentUIDO);

    void batchInsertNodeContentUI(List<NodeContentUIDO> list);

    void updateNodeContentUI(NodeContentUIDO contentUIDO);

    NodeContentUIDO getNodeContentUI(@Param("contentId") Long contentId);

    void batchUpdateNodeContentUI(NodeContentUIDO contentUIDO);

    void deleteNodeContentUIByContentList(@Param("list") List<Long> list);

    List<NodeContentUIDO> queryNodeContentUIList(@Param("contentIdList") List<Long> contentIdList);

    List<NodeContentUIDO> getNodeContentUIByContentId(@Param("contentId") Long contentId);

    List<NodeContentUIDO> getNodeContentUIByNodeUIKey(@Param("contentIdList") List<Long> contentIdList,@Param("nodeUIKey")String nodeKey);
}
