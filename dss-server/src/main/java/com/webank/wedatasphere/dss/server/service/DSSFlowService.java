/*
 * Copyright 2019 WeBank
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
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

package com.webank.wedatasphere.dss.server.service;

import com.webank.wedatasphere.dss.appjoint.exception.AppJointErrorException;
import com.webank.wedatasphere.dss.common.entity.flow.DSSFlow;
import com.webank.wedatasphere.dss.common.entity.flow.DSSFlowVersion;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.server.operate.Op;

import java.util.List;


public interface DSSFlowService {
    DSSFlow getFlowByID(Long id);

    List<DSSFlowVersion> listAllFlowVersions(Long flowID, Long projectVersionID);

    DSSFlow addRootFlow(DSSFlow dssFlow, Long taxonomyID, Long projectVersionID) throws DSSErrorException;

    DSSFlow addSubFlow(DSSFlow dssFlow, Long parentFlowID, Long projectVersionID) throws DSSErrorException;

    /**
     * 通过flowID获取最新版本的DSSFlow，版本信息在latestVersion
     * @param flowID
     * @return
     */
    DSSFlow getLatestVersionFlow(Long flowID, Long projectVersionID) throws DSSErrorException;

    /**
     * 通过flowID和某个版本号，获取一个DSSFlow，版本信息在versions数组中的第一个元素
     * @param flowID
     * @return
     */
    DSSFlow getOneVersionFlow(Long flowID, String version, Long projectVersionID);

    /**
     * 通过DSSFlow对象拿到最新的json，其实这里只要个flowID应该就可以了
     * @param dssFlow
     * @return
     */
/*    String getLatestJsonByFlow(dssFlow dssFlow);

    dssFlow getLatestVersionFlow(Long ProjectID,String flowName);*/

    void updateFlowBaseInfo(DSSFlow dssFlow, Long projectVersionID, Long taxonomyID) throws DSSErrorException;

    void updateFlowTaxonomyRelation(Long flowID, Long taxonomyID) throws DSSErrorException;

    void batchDeleteFlow(List<Long> flowIDlist, Long projectVersionID);

    String saveFlow(Long flowID, String jsonFlow, String comment, String userName, Long projectVersionID, List<Op> ops) throws DSSErrorException, AppJointErrorException;

    Integer getParentRank(Long flowID);

    DSSFlowVersion getLatestVersionByFlowIDAndProjectVersionID(Long flowID, Long projectVersionID);

    Long getParentFlowID(Long id);
}
