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

import com.webank.wedatasphere.dss.apiservice.core.constant.SaveTokenEnum;
import com.webank.wedatasphere.dss.apiservice.core.exception.ApiServiceTokenException;
import com.webank.wedatasphere.dss.apiservice.core.vo.ApprovalVo;
import com.webank.wedatasphere.dss.apiservice.core.vo.TokenManagerVo;

import java.util.List;

/**
 * @author allenlliu
 * @version 2.0.0
 * @date 2020/08/14 11:41 AM
 */
public interface TokenAuth {


    /**
     * batch save token to db
     * @param tokenManagerVos
     * @return
     */
    SaveTokenEnum saveTokensToDb(List<TokenManagerVo> tokenManagerVos, String approvalNo) throws ApiServiceTokenException;

    List<TokenManagerVo>  genTokenRecord(ApprovalVo approvalVo);
}
