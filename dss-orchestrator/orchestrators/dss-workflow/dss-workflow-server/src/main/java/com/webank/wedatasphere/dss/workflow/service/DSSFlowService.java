/*
 *
 *  * Copyright 2019 WeBank
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.webank.wedatasphere.dss.workflow.service;

import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.workflow.common.entity.DSSFlow;

import java.io.IOException;
import java.util.List;

/**
 * Created by v_wbjftang on 2019/5/15.
 */
public interface DSSFlowService {
    DSSFlow getFlowByID(Long id);

    DSSFlow getFlowWithJsonAndSubFlowsByID(Long rootFlowId);

    DSSFlow addFlow(DSSFlow DSSFlow, String contextIDStr) throws DSSErrorException;

    DSSFlow addSubFlow(DSSFlow DSSFlow, Long parentFlowID, String contextIDStr) throws DSSErrorException;

    /**
     * 通过flowID获取最新版本的dwsFlow，版本信息在latestVersion
     * @param flowID
     * @return
     */
    DSSFlow getLatestVersionFlow(Long flowID) throws DSSErrorException;

    /**
     * 通过flowID和某个版本号，获取一个dwsFlow，版本信息在versions数组中的第一个元素
     * @param flowID
     * @return
     */
    DSSFlow getOneVersionFlow(Long flowID);

    /**
     * 通过dwsFlow对象拿到最新的json，其实这里只要个flowID应该就可以了
     * @param DSSFlow
     * @return
     */
/*    String getLatestJsonByFlow(DWSFlow dwsFlow);

    DWSFlow getLatestVersionFlow(Long ProjectID,String flowName);*/

    void updateFlowBaseInfo(DSSFlow DSSFlow) throws DSSErrorException;


    void batchDeleteFlow(List<Long> flowIDlist);

    String saveFlow(Long flowID,
                    String jsonFlow,
                    String comment,
                    String userName,
                    String workspaceName,
                    String projectName
                   ) throws DSSErrorException;

    Integer getParentRank(Long flowID);

    DSSFlow copyRootFlow(Long rootFlowId,String userName,String workspaceName,String projectName,String version) throws DSSErrorException, IOException;

//    DWSFlowVersion getLatestVersionByFlowIDAndProjectVersionID(Long flowID, Long projectVersionID);

    Long getParentFlowID(Long id);

//    AppMap getFlowAppMap(long projectID);
//
//    DWSFlow genBusinessTagForNode(DWSFlow dwsFlow) throws DSSErrorException;
//
//    void flowVersionIncr(Long flowID, String userName, String comment, Long newProjectVersionID, Long oldProjectVersionID,
//                         DWSProject dwsProject,String workspace);
}
