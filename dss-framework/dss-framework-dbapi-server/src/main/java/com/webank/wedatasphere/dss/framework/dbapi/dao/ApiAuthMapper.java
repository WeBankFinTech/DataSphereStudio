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

package com.webank.wedatasphere.dss.framework.dbapi.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.webank.wedatasphere.dss.framework.dbapi.entity.ApiAuth;
import com.webank.wedatasphere.dss.framework.dbapi.entity.response.ApiInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @Classname DSSDataApiAuthMapper
 * @Description
 * @Date 2021/7/14 10:44
 * @Created by suyc
 */
@Mapper
public interface ApiAuthMapper extends BaseMapper<ApiAuth> {

    void addApiAuth(ApiAuth dssDataApiAuth);

    List<ApiAuth> getApiAuthList(Long workspaceId);


    List<ApiInfo> getApiInfoList(Long workspaceId);

    @Update("UPDATE dss_dataapi_auth SET `is_delete` = 1 WHERE `id` = #{id}")
    void deleteApiAuth(@Param("id") Long id);


    @Update("UPDATE dss_dataapi_config SET `status` = 0 WHERE `id` = #{apiId}")
    void offlineApi(@Param("apiId") Long apiId);

    @Update("UPDATE dss_dataapi_config SET `status` = 1 WHERE `id` = #{apiId}")
    void onlineApi(@Param("apiId") Long apiId);

    @Select("SELECT COUNT(1) FROM dss_dataapi_config WHERE `is_delete` = 0 AND `status` = 1 AND `workspace_id` = #{workspaceId}")
    Long getOnlineApiCnt(Long workspaceId);

    @Select("SELECT COUNT(1) FROM dss_dataapi_config WHERE `is_delete` = 0 AND `status` = 0 AND `workspace_id` = #{workspaceId}")
    Long getOfflineApiCnt(Long workspaceId);

    @Select("select UNIX_TIMESTAMP(expire) from dss_dataapi_auth where caller = #{caller} and group_id = #{groupId} and token = #{token}")
    Long getToken(@Param("caller") String caller,@Param("groupId") int groupId,@Param("token") String  token);


}
