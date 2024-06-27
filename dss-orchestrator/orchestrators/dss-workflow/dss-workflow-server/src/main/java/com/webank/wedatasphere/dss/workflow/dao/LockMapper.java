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

import com.webank.wedatasphere.dss.workflow.entity.DSSFlowEditLock;
import org.apache.ibatis.annotations.Param;
import org.springframework.dao.DuplicateKeyException;

import java.util.List;


public interface LockMapper {

    Integer compareAndSwap(DSSFlowEditLock lock);

    /**
     * 获取数据库中isExpire字段为false的lock，只有一个，owner必须
     *
     * @param flowID      工作流id
     * @param owner       锁的持有者
     * @return DSSFlowEditLock
     */
    DSSFlowEditLock getPersonalFlowEditLock(@Param("flowID") Long flowID, @Param("owner") String owner);

    Boolean flowNotExistEditLock(@Param("flowID") Long flowID,
                                 @Param("flowVersion") String flowVersion,
                                 @Param("projectVersionID") Long projectVersionID,
                                 @Param("owner") String owner,
                                 @Param("timeout") long timeout);

    DSSFlowEditLock getFlowEditLockByID(@Param("flowId") Long flowId);

    DSSFlowEditLock getFlowEditLockByLockContent(String lockContent);

    void insertLock(DSSFlowEditLock newLock) throws DuplicateKeyException;

    void clearExpire(@Param("expireTime") String expireTime, @Param("flowId") long flowId);

    void deleteALL();


    void deleteExpectAfterSave(@Param("list") List<String> list);


    void insertFlowStatus(@Param("flowID") Long flowID, @Param("status") String status);

    void updateOrchestratorStatus(@Param("id") Long id, @Param("status") String status);

    List<String> selectOrchestratorByStatus( @Param("status") String status);

    String selectOrchestratorStatus(@Param("id") Long id);

    void updateOrchestratorVersionCommitId(@Param("commitId")String commitId, @Param("flowID")Long flowID);
}
