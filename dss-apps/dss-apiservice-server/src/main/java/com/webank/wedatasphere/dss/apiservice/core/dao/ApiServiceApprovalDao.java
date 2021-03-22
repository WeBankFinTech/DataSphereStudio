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

package com.webank.wedatasphere.dss.apiservice.core.dao;


import com.webank.wedatasphere.dss.apiservice.core.vo.ApprovalVo;
import org.apache.ibatis.annotations.Param;

/**
 * @author: jinyangrao
 * @date: 2020-08-26 18:03:34
 */
public interface ApiServiceApprovalDao {

    /**
     * 新增一条审批单
     */
    void insert(ApprovalVo approvalVo);

    /**
     * 查询审批单
     * */
    ApprovalVo queryByApprovalNo(@Param("approvalNo") String approvalNo);

    /**
     * set status to success
     * */
    void setApprovalStatusSuccess(@Param("approvalNo") String approvalNo);

    /**
     * set status to failed
     * */
    void setApprovalStatusFailed(@Param("approvalNo") String approvalNo);

    /**
     * set status to init
     * */
    void setApprovalStatusInit(@Param("approvalNo") String approvalNo);

    /**
     * set status to applying
     * */
    void setApprovalStatusApplying(@Param("approvalNo") String approvalNo);

    /**
     * update status
     * */
    void updateApprovalStatus(@Param("approvalNo") String approvalNo, @Param("status") Integer status);


    /**
     * delete approval
     * */
    void deleteApproval(@Param("approvalNo") String approvalNo);

    /**
     * 通过版本ID查询审批单
     * */
    ApprovalVo queryByVersionId(@Param("apiVersionId") Long apiVersionId);
}
