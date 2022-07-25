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
import com.webank.wedatasphere.dss.common.utils.DSSCommonUtils;
import com.webank.wedatasphere.dss.framework.admin.service.DssAdminUserService;
import com.webank.wedatasphere.dss.framework.workspace.bean.DSSWorkspace;
import com.webank.wedatasphere.dss.framework.workspace.bean.dto.response.WorkspaceFavoriteVo;
import com.webank.wedatasphere.dss.framework.workspace.bean.dto.response.WorkspaceMenuVo;
import com.webank.wedatasphere.dss.framework.workspace.bean.request.CreateWorkspaceRequest;
import com.webank.wedatasphere.dss.framework.workspace.bean.vo.DSSWorkspaceHomePageVO;
import com.webank.wedatasphere.dss.framework.workspace.bean.vo.DSSWorkspaceOverviewVO;
import com.webank.wedatasphere.dss.framework.workspace.bean.vo.DSSWorkspaceVO;
import com.webank.wedatasphere.dss.framework.workspace.bean.vo.DepartmentVO;
import com.webank.wedatasphere.dss.framework.workspace.service.DSSWorkspaceRoleService;
import com.webank.wedatasphere.dss.framework.workspace.service.DSSWorkspaceService;
import com.webank.wedatasphere.dss.framework.workspace.util.WorkspaceDBHelper;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.common.exception.AppStandardWarnException;
import com.webank.wedatasphere.dss.standard.sso.utils.SSOHelper;
import org.apache.linkis.common.exception.ErrorException;
import org.apache.linkis.server.Message;
import org.apache.linkis.server.security.SecurityFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

import static com.webank.wedatasphere.dss.framework.workspace.util.DSSWorkspaceConstant.WORKSPACE_ID_STR;

@RequestMapping(path = "/dss/framework/workspace", produces = {"application/json"})
@RestController
public class DSSWorkspaceRestful {

    private static final Logger LOGGER = LoggerFactory.getLogger(DSSWorkspaceRestful.class);

    @Autowired
    private DSSWorkspaceService dssWorkspaceService;
    @Autowired
    private DssAdminUserService dssUserService;
    @Autowired
    private WorkspaceDBHelper workspaceDBHelper;
    @Autowired
    private DSSWorkspaceRoleService dssWorkspaceRoleService;

    @RequestMapping(path ="createWorkspace", method = RequestMethod.POST)
    public Message createWorkspace(HttpServletRequest request, @RequestBody CreateWorkspaceRequest createWorkspaceRequest)throws ErrorException {
        String userName = SecurityFilter.getLoginUsername(request);
        if (!dssWorkspaceService.checkAdmin(userName)){
            return Message.error("您好，您不是管理员，没有权限建立工作空间");
        }
        String workSpaceName = createWorkspaceRequest.getWorkspaceName();
        String department = createWorkspaceRequest.getDepartment();
        String description = createWorkspaceRequest.getDescription();
        String stringTags = createWorkspaceRequest.getTags();
        String productName = createWorkspaceRequest.getProductName();
        LOGGER.info("user {} begin to createWorkspace, workSpaceName:{}, description:{}", userName, workSpaceName, description);
        int workspaceId = dssWorkspaceService.createWorkspace(workSpaceName, stringTags, userName, description, department, productName,"");
        return Message.ok().data("workspaceId", workspaceId).data("workspaceName",workSpaceName);
    }

    @RequestMapping(path ="listDepartments", method = RequestMethod.GET)
    public Message listDepartments(HttpServletRequest request, @RequestParam(WORKSPACE_ID_STR) String workspaceId){
        //todo 要从um中获取
        List<DepartmentVO> departments  = dssWorkspaceService.getDepartments();
        return Message.ok().data("departments", departments);
    }

    @RequestMapping(path ="getWorkspaces", method = RequestMethod.GET)
    public Message getWorkspaces(HttpServletRequest request){
        String username = SecurityFilter.getLoginUsername(request);
        List<DSSWorkspace> workspaces = dssWorkspaceService.getWorkspaces(username);
        List<DSSWorkspaceVO> dssWorkspaceVOS = new ArrayList<>();
        for (DSSWorkspace workspace:workspaces ){
            String name = workspace.getName();
            int id = workspace.getId();
            String labels = workspace.getLabel();
            DSSWorkspaceVO dssWorkspaceVO = new DSSWorkspaceVO();
            dssWorkspaceVO.setId(id);
            dssWorkspaceVO.setName(name);
            dssWorkspaceVO.setTags(labels);
            dssWorkspaceVO.setDepartment(workspace.getDepartment());
            dssWorkspaceVO.setDescription(workspace.getDescription());
            dssWorkspaceVO.setProduct(workspace.getProduct());
            dssWorkspaceVOS.add(dssWorkspaceVO);
        }
        //todo 获取用户所有所有能够访问的工作空间
        return Message.ok().data("workspaces", dssWorkspaceVOS);
    }

    @RequestMapping(path ="getWorkspaceHomePage", method = RequestMethod.GET)
    public Message getWorkspaceHomePage(HttpServletRequest request, @RequestParam(required = false, name = "micro_module") String moduleName) throws Exception{
        //如果用户的工作空间大于两个，那么就直接返回/workspace页面
        String username = SecurityFilter.getLoginUsername(request);
        Workspace workspace = new Workspace();
        try {
            LOGGER.info("Put gateway url and cookies into workspace.");
            SSOHelper.addWorkspaceInfo(request, workspace);
        } catch (AppStandardWarnException ignored) {} // ignore it.
        dssUserService.insertOrUpdateUser(username, workspace);
        DSSWorkspaceHomePageVO dssWorkspaceHomePageVO = dssWorkspaceService.getWorkspaceHomePage(username,moduleName);
        return Message.ok().data("workspaceHomePage", dssWorkspaceHomePageVO);
    }

    @RequestMapping(path ="getOverview", method = RequestMethod.GET)
    public Message getOverview(HttpServletRequest request, @RequestParam(WORKSPACE_ID_STR) int workspaceId){
        String username = SecurityFilter.getLoginUsername(request);
        String language = request.getHeader("Content-language");
        boolean isEnglish = "en".equals(language);
        DSSWorkspaceOverviewVO dssWorkspaceOverviewVO = dssWorkspaceService.getOverview(username, workspaceId, isEnglish);
        return Message.ok().data("overview", dssWorkspaceOverviewVO);
    }

    @RequestMapping(path ="refreshCache", method = RequestMethod.GET)
    public Message refreshCache(HttpServletRequest request){
        workspaceDBHelper.retrieveFromDB();
        return Message.ok("refresh ok");
    }

    @RequestMapping(path = "workspaces", method = RequestMethod.GET)
    public Message getAllWorkspaces(HttpServletRequest req) {
        String username = SecurityFilter.getLoginUsername(req);
        List<DSSWorkspace> workspaces = dssWorkspaceService.getWorkspaces(username);
        return Message.ok().data("workspaces", workspaces);
    }

    /**
     * 获取所有工程或者单个工程
     *
     * @param request
     * @return
     */
    @RequestMapping(path = "getWorkSpaceStr", method = RequestMethod.GET)
    public Message getWorkSpaceStr(HttpServletRequest request,
                                   HttpServletResponse response,
                                   @RequestParam(name = "workspaceName") String workspaceName) {
        String username = SecurityFilter.getLoginUsername(request);
        DSSWorkspace workspaceEntity;
        try {
            workspaceEntity = dssWorkspaceService.getWorkspacesByName(workspaceName, username);
        } catch (DSSErrorException e) {
            LOGGER.error("User {} get workspace {} failed.", username, workspaceName, e);
            return Message.error(e);
        }
        Workspace workspace = SSOHelper.setAndGetWorkspace(request, response, workspaceEntity.getId(), workspaceName);
        return Message.ok("succeed.").data("workspaceStr", DSSCommonUtils.COMMON_GSON.toJson(workspace));
    }

    @RequestMapping(path = "/workspaces/{id}", method = RequestMethod.GET)
    public Message getWorkspacesById(HttpServletRequest req,
                                     HttpServletResponse resp,
                                     @PathVariable("id") Long workspaceId) {
        String username = SecurityFilter.getLoginUsername(req);
        DSSWorkspace workspace = null;
        try {
            workspace = dssWorkspaceService.getWorkspacesById(workspaceId, username);
        } catch (DSSErrorException e) {
            LOGGER.error("User {} get workspace {} failed.", username, workspaceId, e);
            return Message.error(e);
        }
        SSOHelper.setAndGetWorkspace(req, resp, workspace.getId(), workspace.getName());
        List<String> roles = dssWorkspaceRoleService.getRoleInWorkspace(username, workspaceId.intValue());
        if (roles == null || roles.isEmpty()) {
            LOGGER.error("username {}, in workspace {} roles are null or empty", username, workspaceId);
            return Message.error("can not get roles information");
        }
        //判断如果是没有权限的，那么就直接干掉
        if (roles.contains("apiUser")) {
            int priv = dssWorkspaceRoleService.getApiPriv(username, workspaceId.intValue(), "apiUser", "apiService");
            if (priv <= 0) {
                roles.remove("apiUser");
            }
        }
        Message retMessage = Message.ok();
        //如果其他的角色也是有这个api权限的，那么就加上这个apiUser
        boolean flag = false;
        for (String role : roles) {
            int priv = dssWorkspaceRoleService.getApiPriv(username, workspaceId.intValue(), role, "apiService");
            if (priv >= 1) {
                flag = true;
                break;
            }
        }
        if (flag && !roles.contains("apiUser")) {
            roles.add("apiUser");
        }
        return retMessage.data("roles", roles).data("workspace", workspace);
    }

    @RequestMapping(path = "/workspaces/departments", method = RequestMethod.GET)
    public Message getAllWorkspaceDepartments(HttpServletRequest req) {
        List<DepartmentVO> departments = dssWorkspaceService.getDepartments();
        return Message.ok().data("departments", departments);
    }

    @RequestMapping(path = "/workspaces/exists", method = RequestMethod.GET)
    public Message getUsernameExistence(HttpServletRequest req, @RequestParam(required = false, name = "name") String name) {
        boolean exists = dssWorkspaceService.existWorkspaceName(name);
        return Message.ok().data("workspaceNameExists", exists);
    }

    @RequestMapping(path = "/workspaces", method = RequestMethod.POST)
    public Message addWorkspace(HttpServletRequest req, @RequestBody Map<String, String> json) throws ErrorException {
        String userName = SecurityFilter.getLoginUsername(req);
        if (!dssWorkspaceService.checkAdmin(userName)) {
            return Message.error("您好，您不是管理员，没有权限建立工作空间");

        }
        String name = json.get("name");
        if (dssWorkspaceService.existWorkspaceName(name)) {
            return Message.error("工作空间名重复");
        }
        String department = json.get("department");
        String label = json.get("label");
        String description = json.get("description");
        String workspaceType = json.get("workspace_type");

        String productName = "DSS";
        int workspaceId = dssWorkspaceService.createWorkspace(name, label, userName, description, department, productName, workspaceType);
        return Message.ok().data("workspaceId", workspaceId);
    }

    @RequestMapping(path = "workspaces/{workspaceId}/appconns", method = RequestMethod.GET)
    public Message getWorkspaceAppConns(HttpServletRequest req, @PathVariable("workspaceId") Long workspaceId) {
        String header = req.getHeader("Content-language").trim();
        boolean isChinese = "zh-CN".equals(header);
        String username = SecurityFilter.getLoginUsername(req);
        Workspace workspace = SSOHelper.getWorkspace(req);
        List<WorkspaceMenuVo> appconns;
        try {
            appconns = dssWorkspaceService.getWorkspaceAppConns(workspace, workspaceId, username, isChinese);
        } catch (DSSErrorException e) {
            LOGGER.warn("{} get appconns from workspace {} failed.", username, workspaceId, e);
            return Message.error(e);
        }
        return Message.ok().data("menus", appconns);
    }


    @RequestMapping(path = "/workspaces/{workspaceId}/favorites", method = RequestMethod.GET)
    public Message getWorkspaceFavorites(HttpServletRequest req, @PathVariable("workspaceId") Long workspaceId, @RequestParam(value = "type", required = false) String type) {
        String header = req.getHeader("Content-language").trim();
        boolean isChinese = "zh-CN".equals(header);
        String username = SecurityFilter.getLoginUsername(req);
        List<WorkspaceFavoriteVo> favorites = dssWorkspaceService.getWorkspaceFavorites(workspaceId, username, isChinese, type == null ? "" : type);
        Set<WorkspaceFavoriteVo> favoriteVos = new HashSet<>(favorites);
        return Message.ok().data("favorites", favoriteVos);
    }

    /**
     * 应用加入收藏，返回收藏后id
     *
     * @param req
     * @param json
     * @return
     */
    @RequestMapping(path = "/workspaces/{workspaceId}/favorites", method = RequestMethod.POST)
    public Message addFavorite(HttpServletRequest req, @PathVariable("workspaceId") Long workspaceId, @RequestBody Map<String, String> json) {
        String username = SecurityFilter.getLoginUsername(req);
//        Long menuApplicationId = json.get("menuApplicationId").getLongValue();
        Long menuApplicationId = Long.valueOf(json.get("menuApplicationId"));
        String type = json.get("type") == null ? "" : json.get("type");
        LOGGER.info("user {} begin to addFavorite, menuApplicationId:{}, type:{}", username, menuApplicationId, type);
        Long favoriteId = dssWorkspaceService.addFavorite(username, workspaceId, menuApplicationId, type);
        return Message.ok().data("favoriteId", favoriteId);
    }


    @RequestMapping(path = "/workspaces/{workspaceId}/favorites/{appconnId}", method = RequestMethod.POST)
    public Message deleteFavorite(HttpServletRequest req, @PathVariable("workspaceId") Long workspaceId, @PathVariable("appconnId") Long appconnId, @RequestBody Map<String, String> json) {
        String username = SecurityFilter.getLoginUsername(req);
        String type = json.get("type") == null ? "" : json.get("type");
        LOGGER.info("user {} begin to deleteFavorite, appconnId:{}, type:{}", username, appconnId, type);
        Long favoriteId = dssWorkspaceService.deleteFavorite(username, appconnId, workspaceId, type);
        return Message.ok().data("favoriteId", favoriteId);
    }


}

