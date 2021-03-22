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


import com.google.gson.Gson;
import com.webank.wedatasphere.dss.framework.workspace.bean.DSSApplication;
import com.webank.wedatasphere.dss.framework.workspace.bean.vo.DSSWorkspaceRoleVO;
import com.webank.wedatasphere.dss.framework.workspace.constant.ApplicationConf;
import com.webank.wedatasphere.dss.framework.workspace.dao.DSSApplicationMapper;
import com.webank.wedatasphere.dss.framework.workspace.service.DSSUserService;
import com.webank.wedatasphere.dss.framework.workspace.service.DSSWorkspaceRoleService;
import com.webank.wedatasphere.dss.framework.workspace.service.DSSWorkspaceService;
import com.webank.wedatasphere.dss.framework.workspace.util.ApplicationUtils;
import com.webank.wedatasphere.linkis.server.Message;
import com.webank.wedatasphere.linkis.server.security.SecurityFilter;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * created by cooperyang on 2020/3/18
 * Description:
 */
@Component
@Path("/dss/framework/workspace")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DSSWorkspaceRoleRestful {

    private static final Logger LOGGER = LoggerFactory.getLogger(DSSWorkspaceRoleRestful.class);
    private static final String WORKSPACE_ID_STR = "workspaceId";
    private static final String DEFAULT_PATH = "/api/rest_j/v1";
    @Autowired
    DSSWorkspaceService dssWorkspaceService;
    @Autowired
    private DSSWorkspaceRoleService dssWorkspaceRoleService;
    @Autowired
    private DSSUserService userService;
    @Autowired
    private DSSApplicationMapper applicationMapper;

    @GET
    @Path("getBaseInfo")
    public Response getBaseInfo(@Context HttpServletRequest req) {
        String username = SecurityFilter.getLoginUsername(req);
        userService.saveWorkspaceUser(username);
        List<DSSApplication> applicationList = applicationMapper.selectList(null);
        DSSApplication schedulis = new DSSApplication();
        schedulis.setName("schedulis");
        String url = ApplicationConf.SCHEDULIS_URL.getValue();
        schedulis.setUrl(url);
        schedulis.setHomepageUrl(url + "/homepage");
        schedulis.setProjectUrl(url + "/manager?project=${projectName}");
        schedulis.setRedirectUrl(url + "/api/v1/redirect");
        schedulis.setIfIframe(true);
        applicationList.add(schedulis);
        for (DSSApplication application : applicationList) {
            String redirectUrl = application.getRedirectUrl();
            String enhanceJson = application.getEnhanceJson();
            if (redirectUrl != null) {
                application.setHomepageUrl(ApplicationUtils.redirectUrlFormat(redirectUrl, application.getHomepageUrl()));
                application.setProjectUrl(ApplicationUtils.redirectUrlFormat(redirectUrl, application.getProjectUrl()));
                if (StringUtils.isNotEmpty(enhanceJson) && enhanceJson.contains("scheduleHistory")) {
                    Gson gson = new Gson();
                    HashMap<String, Object> scheduleHistoryMap = gson.fromJson(enhanceJson, HashMap.class);
                    String oldUrl = scheduleHistoryMap.get("scheduleHistory").toString();
                    String formatUrl = ApplicationUtils.redirectUrlFormat(redirectUrl, oldUrl);
                    scheduleHistoryMap.replace("scheduleHistory", formatUrl);
                    application.setEnhanceJson(gson.toJson(scheduleHistoryMap));
                }
            }
        }
        //返回FAQ地址
        String faqUrl = ApplicationConf.FAQ.getValue();
        boolean isAdmin = userService.isAdminUser(username);
        //前台需要返回username
        return Message.messageToResponse(Message.ok().data("applications", applicationList).
                data("username", username).data("isAdmin", isAdmin)
                .data("DWSParams", Collections.singletonMap("faq", faqUrl)));
    }

    @GET
    @Path("getWorkspaceRoles")
    public Response getWorkspaceRoles(@Context HttpServletRequest request, @QueryParam(WORKSPACE_ID_STR) int workspaceId){
        //todo 获取工作空间中所有的角色
        List<DSSWorkspaceRoleVO> workspaceRoles = dssWorkspaceService.getWorkspaceRoles(workspaceId);
        return Message.messageToResponse(Message.ok().data("workspaceRoles", workspaceRoles));
    }


    @POST
    @Path("addWorkspaceRole")
    public Response addWorkspaceRole(@Context HttpServletRequest request, JsonNode jsonNode){
        String username = SecurityFilter.getLoginUsername(request);
        int workspaceId = jsonNode.get("workspaceId").getIntValue();
        String roleName = jsonNode.get("roleName").getTextValue();
        List<Integer> menuIds = new ArrayList<>();
        List<Integer> componentIds = new ArrayList<>();
        JsonNode menuIdsNode = jsonNode.get("menuIds");
        if (menuIdsNode != null && menuIdsNode.isArray()){
            menuIdsNode.forEach(tmpMenuId -> menuIds.add(tmpMenuId.getIntValue()));
        }
        JsonNode componentIdsNode = jsonNode.get("componentIds");
        if (componentIdsNode != null && componentIdsNode.isArray()){
            componentIdsNode.forEach(tmpMenuId -> componentIds.add(tmpMenuId.getIntValue()));
        }
        dssWorkspaceRoleService.addWorkspaceRole(roleName, workspaceId, menuIds, componentIds, username);
        return Message.messageToResponse(Message.ok("创建角色成功"));
    }

    @POST
    @Path("deleteWorkspaceRole")
    public Response deleteWorkspaceRole(@Context HttpServletRequest request, JsonNode jsonNode){

        return null;
    }

    @GET
    @Path("getWorkspaceBaseInfo")
    public Response getWorkspaceInfo(@Context HttpServletRequest request,
                                     @Context HttpServletResponse response,
                                     @QueryParam(WORKSPACE_ID_STR) Integer workspaceId){
        String username = SecurityFilter.getLoginUsername(request);
        //如果workspaceId为null的话,那么就找到这个用户工作空间
        if (workspaceId == null || workspaceId <= 0){
            workspaceId = dssWorkspaceRoleService.getWorkspaceIdByUser(username);
        }
        //将workspaceId作为cookie写入
        Cookie[] cookies = request.getCookies();
        for(Cookie cookie : cookies){
            if(WORKSPACE_ID_STR.equals(cookie.getName())){
                cookie.setMaxAge(0);
                cookie.setPath("/");
                cookie.setValue(null);
                response.addCookie(cookie);
                break;
            }
        }
        Cookie workspaceCookie = new Cookie(WORKSPACE_ID_STR, workspaceId.toString());
        workspaceCookie.setPath("/");
        response.addCookie(workspaceCookie);
        List<String> roles = dssWorkspaceRoleService.getRoleInWorkspace(username, workspaceId);
        if(roles == null || roles.isEmpty()){
            LOGGER.error("username {}, in workspace {} roles are null or empty", username, workspaceId);
            return Message.messageToResponse(Message.error("can not get roles information"));
        }
        //判断如果是没有权限的，那么就直接干掉
        if (roles.contains("apiUser")){
            int priv = dssWorkspaceRoleService.getApiPriv(username, workspaceId, "apiUser", "apiService");
            if(priv <= 0) {
                roles.remove("apiUser");
            }
        }
        Message retMessage = Message.ok();
        //工作空间中，加上用户在顶部的菜单
        if (roles.contains("analyser")){
            retMessage.data("topName", "Scriptis");
            retMessage.data("topUrl", "/home");
        } else if (roles.contains("developer")){
            retMessage.data("topName", "工作流开发");
            retMessage.data("topUrl", "/project");
        }else if(roles.contains("apiUser") && roles.size() == 1){
            retMessage.data("topName","数据服务");
            retMessage.data("topUrl", "/apiservices");
        }else{
            retMessage.data("topName", "Scriptis");
            retMessage.data("topUrl", "/home");
        }
        //如果其他的角色也是有这个api权限的，那么就加上这个apiUser
        boolean flag = false;
        for (String role : roles){
            int priv = dssWorkspaceRoleService.getApiPriv(username, workspaceId, role, "apiService");
            if (priv >= 1) {
                flag = true;
                break;
            }
        }
        if(flag && !roles.contains("apiUser")){
            roles.add("apiUser");
        }
        //roles.add("apiUser");
        return Message.messageToResponse(retMessage.data("roles", roles).data("workspaceId", workspaceId));
    }

}
