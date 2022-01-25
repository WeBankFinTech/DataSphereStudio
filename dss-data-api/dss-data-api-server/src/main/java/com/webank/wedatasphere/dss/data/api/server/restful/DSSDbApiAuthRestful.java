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

package com.webank.wedatasphere.dss.data.api.server.restful;



import com.webank.wedatasphere.dss.data.api.server.entity.ApiAuth;
import com.webank.wedatasphere.dss.data.api.server.entity.response.ApiAuthInfo;
import com.webank.wedatasphere.dss.data.api.server.entity.response.ApiGroupInfo;
import com.webank.wedatasphere.dss.data.api.server.service.ApiAuthService;
import org.apache.linkis.common.exception.ErrorException;
import org.apache.linkis.server.Message;
import org.apache.linkis.server.security.SecurityFilter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @Classname DSSDataAPIAuthRestful
 * @Description 服务管理--API授权
 * @Date 2021/7/14 10:44
 * @Created by suyc
 */
@RestController
@RequestMapping(path = "/dss/data/api/apiauth", produces = {"application/json"})
@Slf4j
public class DSSDbApiAuthRestful {
    @Autowired
    private ApiAuthService apiAuthService;
    
    @RequestMapping(path ="save", method = RequestMethod.POST)
    public Message saveApiAuth(HttpServletRequest request, @RequestBody ApiAuth apiAuth) throws ErrorException {
        String userName = SecurityFilter.getLoginUsername(request);
        if(apiAuth.getId() ==null) {
            String token = DigestUtils.md5Hex(UUID.randomUUID().toString());
            apiAuth.setToken(token);

            apiAuth.setCreateBy(userName);
            apiAuth.setCreateTime(new Date(System.currentTimeMillis()));
            apiAuth.setUpdateTime(new Date(System.currentTimeMillis()));
        }
        else{
            apiAuth.setUpdateBy(userName);
            apiAuth.setUpdateTime(new Date(System.currentTimeMillis()));
        }

        boolean flag = apiAuthService.saveApiAuth(apiAuth);
        if(flag) {
            return Message.ok("保存成功");
        }else{
            return Message.error("保存失败");
        }
    }

  
    @RequestMapping(path = "token", method = RequestMethod.GET)
    public Message generateToken( ) {
        String token = DigestUtils.md5Hex(UUID.randomUUID().toString());
        return Message.ok().data("token",token);
    }

  
    @RequestMapping(path = "list", method = RequestMethod.GET)
    public Message getApiAuthList(@RequestParam("workspaceId") Long workspaceId, @RequestParam("caller") String caller,
                                   @RequestParam("pageNow") Integer pageNow, @RequestParam("pageSize") Integer pageSize){
        if(pageNow == null){
            pageNow = 1;
        }
        if(pageSize == null){
            pageSize = 20;
        }

        List<Long> totals = new ArrayList<>();
        List<ApiAuthInfo> apiAuths = apiAuthService.getApiAuthList(workspaceId,caller,totals,pageNow,pageSize);
        return Message.ok().data("list",apiAuths).data("total", totals.get(0));
    }


    @RequestMapping(path ="/{id}", method = RequestMethod.POST)
    public Message deleteApiAuth(@PathVariable("id") Long id){
        log.info("-------delete apiauth:    " + id + ", begin");
        apiAuthService.deleteApiAuth(id);
        Message message = Message.ok("删除成功");
        return message;
    }

 
    @RequestMapping(path = "apigroup", method = RequestMethod.GET)
    public Message getApiGroup(@RequestParam("workspaceId") Long workspaceId){
        List<ApiGroupInfo> apiGroupInfoList = apiAuthService.getApiGroupList(workspaceId);

        Message message = Message.ok().data("list",apiGroupInfoList);
        return message;
    }
}

