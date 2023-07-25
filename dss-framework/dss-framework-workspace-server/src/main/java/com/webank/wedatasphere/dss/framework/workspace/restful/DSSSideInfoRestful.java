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

package com.webank.wedatasphere.dss.framework.workspace.restful;

import com.webank.wedatasphere.dss.framework.workspace.service.DSSSideInfoService;
import org.apache.linkis.server.Message;
import org.apache.linkis.server.security.SecurityFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;


@RequestMapping(path = "/dss/framework/workspace", produces = {"application/json"})
@RestController
public class DSSSideInfoRestful {

    private static final Logger LOGGER = LoggerFactory.getLogger(DSSSideInfoRestful.class);

    @Autowired
    private DSSSideInfoService dssSideInfoService;

    @RequestMapping(path ="getSideInfos", method = RequestMethod.GET)
    public Message getSideInfos(HttpServletRequest request, @RequestParam(required = false, name = "workspaceID") Long workspaceId){
        String username = SecurityFilter.getLoginUsername(request);
        LOGGER.info("Begin to getSideInfos for user:{}", username);
        try{
            boolean isEnglish = "en".equals(request.getHeader("Content-language"));
            return Message.ok("获取侧边栏成功").data("presentations", dssSideInfoService.getSidebarVOList(username, workspaceId,isEnglish));
        }catch(Exception e){
            LOGGER.info("Fail to get sideinfos for user {} in workspace {}",username, workspaceId, e);
            return Message.error("获取侧边栏失败");
        }
    }

}
