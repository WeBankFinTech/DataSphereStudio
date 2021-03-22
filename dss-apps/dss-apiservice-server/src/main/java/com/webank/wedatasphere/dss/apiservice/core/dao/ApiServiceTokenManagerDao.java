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

import com.webank.wedatasphere.dss.apiservice.core.vo.TokenManagerVo;
import com.webank.wedatasphere.dss.apiservice.core.bo.TokenQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author jinyangrao
 */
public interface ApiServiceTokenManagerDao {

    /**
     * insert a token
     * */
    void insert(TokenManagerVo tokenManagerVo);

    /**
     * insert batch token
     * */
    void insertList(List<TokenManagerVo> tokenManagerVos);


    /**
     * 多条记录需要去重返回最新授权的
     * @param userName
     * @return
     */
    List<TokenManagerVo> queryByApplyUser(String userName);


    List<TokenManagerVo> queryByApplyUserAndServiceId(@Param("userName") String userName, @Param("serviceId") Long serviceId);

    List<TokenManagerVo> queryByApplyUserAndVersionId(@Param("userName") String userName, @Param("apiVersionId") Long apiVersionId);



    /**
     * query
     * */
    List<TokenManagerVo> query(TokenQuery tokenQuery);

    /**
     * query token by token id
     * */
    TokenManagerVo queryTokenById(@Param("id") Long tokenId);

    /**
     * query token by api service id
     * */
    List<TokenManagerVo> queryTokenByApiServiceId(@Param("api_id") Long apiServiceId);

    /**
     * query token avoid submit again
     * */
    int queryApprovalNo(@Param("approvalNo") String approvalNo);

    /**
     * query tokens according to status
     * */
    List<TokenManagerVo> queryTokenByStatus(@Param("status") Integer status);

    /**
     * disable token status
     * */
    void disableTokenStatus(@Param("id") Long id);


    /**
     * disable token status
     * */
    void disableTokenStatusByVersionId(@Param("apiVersionId") Long apiVersionId);


    /**
     * disable token status
     * */
    void disableTokenStatusByApiId(@Param("apiId") Long apiId);

    /**
     * enable token status
     * */
    void enableTokenStatus(@Param("id") Long id);

    void enableTokenStatusByVersionId(@Param("apiVersionId") Long apiVersionId);

    void enableTokenStatusByApiId(@Param("apiId") Long apiId);


    /**
     * delete token
     * */
    void deleteTokenById(@Param("id") Long tokenId);
    
    /**
     * update token status
     * */
//    void updateTokenStatus(@Param(id) Long tokenId, String status);

}
