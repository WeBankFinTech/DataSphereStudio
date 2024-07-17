/*
 * Copyright 2019 WeBank
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.webank.wedatasphere.dss.workflow.dao;


import com.webank.wedatasphere.dss.workflow.common.entity.DSSFlow;
import com.webank.wedatasphere.dss.workflow.common.entity.DSSFlowRelation;
import com.webank.wedatasphere.dss.workflow.entity.vo.FlowInfoVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.dao.DuplicateKeyException;

import java.util.List;


public interface FlowMapper {
    DSSFlow selectFlowByID(Long id);

    List<DSSFlow> selectFlowListByID(List<Long> list);

    void insertFlow(DSSFlow dssFlow) throws DuplicateKeyException;

    void insertFlowRelation(@Param("flowID") Long flowID, @Param("parentFlowID") Long parentFlowID);

    void updateFlowBaseInfo(DSSFlow dssFlow) throws DuplicateKeyException;

    List<Long> selectSubFlowIDByParentFlowID(Long parentFlowID);

    List<Long> selectSavedSubFlowIDByParentFlowID(Long parentFlowID);

    void deleteFlowBaseInfo(Long flowID);

    void deleteFlowRelation(Long flowID);

    Long selectParentFlowIDByFlowID(Long flowID);

    List<DSSFlow> getSubflowInfoByParentId(@Param("parentFlowID") Long parentFlowID);

    Long getParentFlowID(Long flowID);

    List<DSSFlowRelation> listFlowRelation(@Param("flowIDs") List<Long> flowIDs);

    void updateFlowInputInfo(DSSFlow dssFlow);

    DSSFlowRelation selectFlowRelation(@Param("flowID") Long flowID, @Param("parentFlowID") Long parentFlowID);

    @Select("select creator from dss_flow where id = #{flowId}")
    String getCreatorById(@Param("flowId") Long flowId);
    List<String> getSubflowName(Long parentFlowID);
}
