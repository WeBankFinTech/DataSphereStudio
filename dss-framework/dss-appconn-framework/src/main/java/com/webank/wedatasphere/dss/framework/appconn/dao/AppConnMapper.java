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

package com.webank.wedatasphere.dss.framework.appconn.dao;


import com.webank.wedatasphere.dss.framework.appconn.entity.AppConnBean;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author: jinyangrao on 2020/10/15
 */
@Mapper
public interface AppConnMapper {

    /**
     * get all appconnbeans
     * */
    List<AppConnBean> getAllAppConnBeans();

    /**
     * get all appconns' name
     * */
    List<String> getAllAppConnsName();

    /**
     * get appconnbeans by name
     * */
    AppConnBean getAppConnBeanByName(@Param("appConnName") String appConnName);

    /**
     * get appconn by id
     * */
    AppConnBean getAppConnBeanById(@Param("appConnId") Long appConnId);

    /**
     * get appconnname by node type
     * */
    String getAppConnNameByNodeType(@Param("nodeType") String nodeType);

    String getRedirectUrl(@Param("nodeType") String nodeType, @Param("appInstanceId") Long appInstanceId);
}