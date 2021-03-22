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

package com.webank.wedatasphere.dss.apiservice.core.service.impl;

import com.webank.wedatasphere.dss.apiservice.core.dao.ApiServiceApprovalDao;
import com.webank.wedatasphere.dss.apiservice.core.dao.ApiServiceVersionDao;
import com.webank.wedatasphere.dss.apiservice.core.datamap.DataMapStatus;
import com.webank.wedatasphere.dss.apiservice.core.service.ApprovalService;
import com.webank.wedatasphere.dss.apiservice.core.vo.ApprovalVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author: jinyangrao
 */
@Service
public class ApprovalServiceImpl implements ApprovalService {

    private static final Logger LOG = LoggerFactory.getLogger(ApprovalServiceImpl.class);

    @Autowired
    private ApiServiceApprovalDao apiServiceApprovalDao;

    @Autowired
    private ApiServiceVersionDao apiServiceVersionDao;

    @Override
    public ApprovalVo refreshStatus(String  approvalNo) throws Exception {
        ApprovalVo approvalVo = apiServiceApprovalDao.queryByApprovalNo(approvalNo);
        if (null != approvalVo) {
            if (DataMapStatus.APPROVING.getIndex() == approvalVo.getStatus() ||
                    DataMapStatus.INITED.getIndex() == approvalVo.getStatus() ||
                    DataMapStatus.REJECT.getIndex() == approvalVo.getStatus()) {

                // use datamap interface
                // 更新审批单的状态，驳回还会有后续状态，所以当为驳回状态时，还需要查询datamap
//                DataMapStatus dataMapStatus = DataMapUtils.requestDataMapStatus(approvalNo, null);
                DataMapStatus dataMapStatus= DataMapStatus.SUCCESS;
                approvalVo.setStatus(dataMapStatus.getIndex());
                apiServiceApprovalDao.updateApprovalStatus(approvalNo, dataMapStatus.getIndex());
                LOG.info("更新审批单状态： {}" ,approvalNo, dataMapStatus.getValue());
            }
            return approvalVo;
        } else {
            return null;
        }
    }
    @Override
    public ApprovalVo query(String approvalNo) {
        ApprovalVo approvalVo = apiServiceApprovalDao.queryByApprovalNo(approvalNo);

        if(null != approvalVo) {
            return approvalVo;
        } else {
            LOG.warn("无此审批单号！" + approvalNo);
            return null;
        }
    }
}