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

package com.webank.wedatasphere.dss.apiservice.core.token;

import com.webank.wedatasphere.dss.apiservice.core.constant.ApiCommonConstant;
import com.webank.wedatasphere.dss.apiservice.core.constant.SaveTokenEnum;
import com.webank.wedatasphere.dss.apiservice.core.dao.ApiServiceTokenManagerDao;
import com.webank.wedatasphere.dss.apiservice.core.exception.ApiServiceTokenException;
import com.webank.wedatasphere.dss.apiservice.core.bo.ApiServiceToken;
import com.webank.wedatasphere.dss.apiservice.core.vo.ApprovalVo;
import com.webank.wedatasphere.dss.apiservice.core.vo.TokenManagerVo;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author allenlliu
 * @version 2.0.0
 * @date 2020/08/14 11:52 AM
 */

@Component
public class DataMapTokenImpl implements TokenAuth {

    private static final Logger logger = LoggerFactory.getLogger(DataMapTokenImpl.class);


    @Autowired
    ApiServiceTokenManagerDao apiServiceTokenManagerDao;

    @Override
    public SaveTokenEnum saveTokensToDb(List<TokenManagerVo> tokenManagerVos, String approvalNo) throws ApiServiceTokenException {
        boolean isEmptyToken = tokenManagerVos.stream().filter(tokenManagerVo -> StringUtils.isEmpty(tokenManagerVo.getToken())).count()>0;
        if(checkDuplicateAuth(approvalNo) && approvalNo != ApiCommonConstant.DEFAULT_APPROVAL_NO){
//            throw new ApiServiceTokenException(800001,"ApprovalNo has been repeatedly authorized ");
            return SaveTokenEnum.DUPLICATE;
        }else if(isEmptyToken){
            throw new ApiServiceTokenException(800001,"Failed to save to db for Some token is empty");
        }
        else {

            try {
                apiServiceTokenManagerDao.insertList(tokenManagerVos);
            } catch (Exception e) {
                logger.error("Batch save token to db failed", e);
                return SaveTokenEnum.FAILED;
            }
        }

        return SaveTokenEnum.SUCCESS;
    }

    private boolean  checkDuplicateAuth(String approvalNo){
        if(apiServiceTokenManagerDao.queryApprovalNo(approvalNo) > 0){
            return  true;
        }else {
            return  false;
        }
    }

    @Override
    public List<TokenManagerVo>  genTokenRecord(ApprovalVo approvalVo){

        List<TokenManagerVo>  tokenManagerVoList = new ArrayList<>();
        Arrays.stream(approvalVo.getApplyUser().split(",")).forEach(tempUser ->{

            TokenManagerVo tmpToken = new TokenManagerVo();
            tmpToken.setApiId(approvalVo.getApiId());
            tmpToken.setApplyTime(new Date());
            tmpToken.setDuration(365L);
            tmpToken.setReason("approval token auth");
            tmpToken.setStatus(1);
            tmpToken.setIpWhitelist("");
            tmpToken.setCaller("scripts");
            tmpToken.setUser(tempUser);
            tmpToken.setPublisher(approvalVo.getCreator());
            tmpToken.setApiVersionId(approvalVo.getApiVersionId());

            ApiServiceToken apiServiceToken = new ApiServiceToken();
            apiServiceToken.setApplyUser(tempUser);
            apiServiceToken.setPublisher(approvalVo.getCreator()); //todo creator
            apiServiceToken.setApplyTime(tmpToken.getApplyTime());
            apiServiceToken.setApiServiceId(approvalVo.getApiId());

            tmpToken.setToken(JwtManager.createToken(tempUser,apiServiceToken,tmpToken.getDuration()));
            //审批单号
            tmpToken.setApplySource(approvalVo.getApprovalNo());
            tokenManagerVoList.add(tmpToken);


        });

        return tokenManagerVoList;
    }
}
