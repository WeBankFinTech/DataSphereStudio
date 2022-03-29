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


import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.framework.workspace.bean.DSSWorkspace;
import com.webank.wedatasphere.dss.framework.workspace.bean.request.AddWorkspaceRoleRequest;
import com.webank.wedatasphere.dss.framework.workspace.bean.vo.DSSWorkspaceRoleVO;
import com.webank.wedatasphere.dss.framework.workspace.constant.ApplicationConf;
import com.webank.wedatasphere.dss.framework.workspace.service.DSSUserService;
import com.webank.wedatasphere.dss.framework.workspace.service.DSSWorkspaceRoleService;
import com.webank.wedatasphere.dss.framework.workspace.service.DSSWorkspaceService;
import com.webank.wedatasphere.dss.standard.sso.utils.SSOHelper;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.linkis.server.Message;
import org.apache.linkis.server.security.SecurityFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;

import static com.webank.wedatasphere.dss.framework.workspace.util.DSSWorkspaceConstant.WORKSPACE_ID_STR;


@RequestMapping(path = "/dss/framework/workspace", produces = {"application/json"})
@RestController
public class DSSWorkspaceRoleRestful {

    private static final Logger LOGGER = LoggerFactory.getLogger(DSSWorkspaceRoleRestful.class);
    @Autowired
    DSSWorkspaceService dssWorkspaceService;
    @Autowired
    private DSSWorkspaceRoleService dssWorkspaceRoleService;
    @Autowired
    private DSSUserService userService;
//    @Autowired
//    private DSSApplicationMapper applicationMapper;

//    @RequestMapping(path ="getBaseInfo", method = RequestMethod.GET)
//    public Message getBaseInfo(HttpServletRequest req) {
//        String username = SecurityFilter.getLoginUsername(req);
//        userService.saveWorkspaceUser(username);
//        List<DSSApplication> applicationList = applicationMapper.selectList(null);
//        for (DSSApplication application : applicationList) {
//            String redirectUrl = application.getRedirectUrl();
//            String enhanceJson = application.getEnhanceJson();
//            if (redirectUrl != null && redirectUrl.startsWith("http")) {
//                application.setHomepageUrl(ApplicationUtils.redirectUrlFormat(redirectUrl, application.getHomepageUrl()));
//                application.setProjectUrl(ApplicationUtils.redirectUrlFormat(redirectUrl, application.getProjectUrl()));
//                if (StringUtils.isNotEmpty(enhanceJson) && enhanceJson.contains("scheduleHistory")) {
//                    Gson gson = new Gson();
//                    HashMap<String, Object> scheduleHistoryMap = gson.fromJson(enhanceJson, HashMap.class);
//                    String oldUrl = scheduleHistoryMap.get("scheduleHistory").toString();
//                    String formatUrl = ApplicationUtils.redirectUrlFormat(redirectUrl, oldUrl);
//                    scheduleHistoryMap.replace("scheduleHistory", formatUrl);
//                    application.setEnhanceJson(gson.toJson(scheduleHistoryMap));
//                }
//            }
//        }
        //返回FAQ地址
//        String faqUrl = ApplicationConf.FAQ.getValue();
//        boolean isAdmin = userService.isAdminUser(username);
//        //前台需要返回username
//        return Message.ok().
//                data("username", username).data("isAdmin", isAdmin)
//                .data("DWSParams", Collections.singletonMap("faq", faqUrl));
//    }

    @RequestMapping(path ="getWorkspaceRoles", method = RequestMethod.GET)
    public Message getWorkspaceRoles(HttpServletRequest request, @RequestParam(WORKSPACE_ID_STR) int workspaceId){
        //todo 获取工作空间中所有的角色
        List<DSSWorkspaceRoleVO> workspaceRoles = dssWorkspaceService.getWorkspaceRoles(workspaceId);
        return Message.ok().data("workspaceRoles", workspaceRoles);
    }

    @RequestMapping(path ="addWorkspaceRole", method = RequestMethod.POST)
    public Message addWorkspaceRole(HttpServletRequest request,@RequestBody AddWorkspaceRoleRequest addWorkspaceRoleRequest){
        String username = SecurityFilter.getLoginUsername(request);
        int workspaceId = addWorkspaceRoleRequest.getWorkspaceId();
        String roleName = addWorkspaceRoleRequest.getRoleName();
        List<Integer> menuIds = addWorkspaceRoleRequest.getMenuIds();
        List<Integer> componentIds = addWorkspaceRoleRequest.getComponentIds();
        if(!dssWorkspaceService.checkAdmin(username)||!dssWorkspaceService.checkAdminByWorkspace(username,workspaceId)){
            return Message.error("无权限进行该操作");
        }
        dssWorkspaceRoleService.addWorkspaceRole(roleName, workspaceId, menuIds, componentIds, username);
        return Message.ok("创建角色成功");
    }

    @RequestMapping(path ="deleteWorkspaceRole", method = RequestMethod.POST)
    public Message deleteWorkspaceRole(HttpServletRequest request){

        return null;
    }

    @RequestMapping(path ="getWorkspaceBaseInfo", method = RequestMethod.GET)
    public Message getWorkspaceInfo(HttpServletRequest request,
                                     HttpServletResponse response,
                                     @RequestParam(WORKSPACE_ID_STR) Integer workspaceId){
        String username = SecurityFilter.getLoginUsername(request);
        //如果workspaceId为null的话,那么就找到这个用户工作空间
        if (workspaceId == null || workspaceId <= 0){
            workspaceId = dssWorkspaceRoleService.getWorkspaceIdByUser(username);
        }
        DSSWorkspace workspace;
        try {
            workspace = dssWorkspaceService.getWorkspacesById(workspaceId.longValue(), username);
        } catch (DSSErrorException e) {
            return Message.error(ExceptionUtils.getRootCauseMessage(e));
        }
        //将workspaceId作为cookie写入
        SSOHelper.setAndGetWorkspace(request, response, workspaceId, workspace.getName());
        List<String> roles = dssWorkspaceRoleService.getRoleInWorkspace(username, workspaceId);
        if(roles == null || roles.isEmpty()){
            LOGGER.error("username {}, in workspace {} roles are null or empty", username, workspaceId);
            return Message.error("can not get roles information");
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
            retMessage.data("topName", "Scriptis");
            retMessage.data("topUrl", "/home");
        }else if(roles.contains("apiUser") && roles.size() == 1){
            retMessage.data("topName","Scriptis");
            retMessage.data("topUrl", "/home");
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
        return retMessage.data("roles", roles).data("workspaceId", workspaceId);
    }

}
