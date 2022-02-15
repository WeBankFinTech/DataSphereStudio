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

package com.webank.wedatasphere.dss.data.api.server.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.webank.wedatasphere.dss.data.api.server.entity.ApiAuth;
import com.webank.wedatasphere.dss.data.api.server.entity.response.ApiAuthInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ApiAuthMapper extends BaseMapper<ApiAuth> {

    void addApiAuth(ApiAuth dssDataApiAuth);

    List<ApiAuthInfo> getApiAuthList(@Param("workspaceId") Long workspaceId, @Param("caller") String caller);

    @Update("UPDATE dss_dataapi_auth SET `is_delete` = 1,`update_time` = NOW() WHERE `id` = #{id}")
    void deleteApiAuth(@Param("id") Long id);

    @Select("select UNIX_TIMESTAMP(expire) from dss_dataapi_auth where caller = #{caller} and group_id = #{groupId} and token = #{token}")
    Long getToken(@Param("caller") String caller, @Param("groupId") int groupId, @Param("token") String token);


}
