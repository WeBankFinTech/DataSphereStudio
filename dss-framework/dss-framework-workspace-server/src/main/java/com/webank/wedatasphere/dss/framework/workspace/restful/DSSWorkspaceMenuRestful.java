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

package com.webank.wedatasphere.dss.framework.workspace.restful;


import com.webank.wedatasphere.dss.framework.workspace.bean.DSSWorkspaceMenuComponentUrl;
import com.webank.wedatasphere.dss.framework.workspace.bean.vo.info.AbstractWorkspaceComponentInfoVO;
import com.webank.wedatasphere.dss.framework.workspace.bean.vo.info.DSSWorkspaceScriptisInfoVO;
import com.webank.wedatasphere.dss.framework.workspace.bean.vo.info.DSSWorkspaceVisualisInfoVO;
import com.webank.wedatasphere.dss.framework.workspace.bean.vo.info.DSSWorkspaceWorkflowInfoVO;
import com.webank.wedatasphere.dss.framework.workspace.dao.DSSWorkspaceRoleMapper;
import com.webank.wedatasphere.dss.framework.workspace.service.DSSWorkspaceMenuService;
import com.webank.wedatasphere.dss.framework.workspace.util.WorkspaceDBHelper;
import com.webank.wedatasphere.linkis.server.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.webank.wedatasphere.dss.framework.workspace.util.DSSWorkspaceConstant.WORKSPACE_ID_STR;


/**
 * created by cooperyang on 2020/3/17
 * Description:
 */
@Component
@Path("/dss/framework/workspace")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DSSWorkspaceMenuRestful {

    private static final Logger LOGGER = LoggerFactory.getLogger(DSSWorkspaceMenuRestful.class);

    @Autowired
    private DSSWorkspaceMenuService dssWorkspaceMenuService;
    @Autowired
    private WorkspaceDBHelper workspaceDBHelper;
    @Autowired
    private DSSWorkspaceRoleMapper dssWorkspaceRoleMapper;
    private Map<Integer, Class<? extends AbstractWorkspaceComponentInfoVO>> components;

    @PostConstruct
    public void init(){
        //todo 要从dss_menu_component_url这个表去拿
        List<DSSWorkspaceMenuComponentUrl> dssWorkspaceMenuComponentUrls = dssWorkspaceRoleMapper.getMenuComponentUrl();

        components = new HashMap<>();
        components.put(16, DSSWorkspaceScriptisInfoVO.class);
        components.put(17, DSSWorkspaceVisualisInfoVO.class);
        components.put(12, DSSWorkspaceWorkflowInfoVO.class);
    }

    @GET
    @Path("getStatistics")
    public Response getStatistics(@Context HttpServletRequest request,
                                  @QueryParam(WORKSPACE_ID_STR) int workspaceId, @QueryParam("componentId") int componentId){
        Class<? extends AbstractWorkspaceComponentInfoVO> clazz = components.get(componentId);
        AbstractWorkspaceComponentInfoVO vo;
        List<DSSWorkspaceMenuComponentUrl> dssWorkspaceMenuComponentUrls = dssWorkspaceRoleMapper.getMenuComponentUrl();
        try {
            vo = clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            LOGGER.info("instantiate a vo failed reason is ",e);
            return Message.messageToResponse(Message.error("不能获取到相应的信息"));
        }
        if(workspaceDBHelper.getComponentUrlsById(componentId) != null){
            vo.setComponentUrl(workspaceDBHelper.getComponentUrlsById(componentId).get(0));
        }
        dssWorkspaceMenuComponentUrls.stream().forEach(url -> {
            if (url.getMenuId() == componentId){
                vo.setUserManualUrl(url.getManulUrl());
            }
        });
        Message message = Message.ok().data("statistic", vo).data("workspaceId", workspaceId);
        return Message.messageToResponse(message);
    }

}
