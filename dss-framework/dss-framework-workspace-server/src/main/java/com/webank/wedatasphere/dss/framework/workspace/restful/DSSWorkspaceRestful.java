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

import com.webank.wedatasphere.dss.common.auditlog.OperateTypeEnum;
import com.webank.wedatasphere.dss.common.auditlog.TargetTypeEnum;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.common.utils.AuditLogUtils;
import com.webank.wedatasphere.dss.common.utils.DSSCommonUtils;
import com.webank.wedatasphere.dss.common.utils.RpcAskUtils;
import com.webank.wedatasphere.dss.framework.admin.service.DssAdminUserService;
import com.webank.wedatasphere.dss.framework.workspace.bean.DSSWorkspace;
import com.webank.wedatasphere.dss.framework.workspace.bean.dto.response.WorkspaceFavoriteVo;
import com.webank.wedatasphere.dss.framework.workspace.bean.dto.response.WorkspaceMenuVo;
import com.webank.wedatasphere.dss.framework.workspace.bean.request.CreateWorkspaceRequest;
import com.webank.wedatasphere.dss.framework.workspace.bean.vo.DSSWorkspaceHomePageVO;
import com.webank.wedatasphere.dss.framework.workspace.bean.vo.DSSWorkspaceOverviewVO;
import com.webank.wedatasphere.dss.framework.workspace.bean.vo.DSSWorkspaceVO;
import com.webank.wedatasphere.dss.framework.workspace.service.DSSWorkspaceRoleService;
import com.webank.wedatasphere.dss.framework.workspace.service.DSSWorkspaceService;
import com.webank.wedatasphere.dss.framework.workspace.util.WorkspaceDBHelper;
import com.webank.wedatasphere.dss.framework.workspace.util.WorkspaceUtils;
import com.webank.wedatasphere.dss.git.common.protocol.GitUserEntity;
import com.webank.wedatasphere.dss.git.common.protocol.request.GitConnectRequest;
import com.webank.wedatasphere.dss.git.common.protocol.request.GitUserInfoRequest;
import com.webank.wedatasphere.dss.git.common.protocol.request.GitUserUpdateRequest;
import com.webank.wedatasphere.dss.git.common.protocol.response.GitConnectResponse;
import com.webank.wedatasphere.dss.git.common.protocol.response.GitUserInfoResponse;
import com.webank.wedatasphere.dss.git.common.protocol.response.GitUserUpdateResponse;
import com.webank.wedatasphere.dss.sender.service.DSSSenderServiceFactory;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.common.exception.AppStandardWarnException;
import com.webank.wedatasphere.dss.standard.sso.utils.SSOHelper;
import org.apache.linkis.common.exception.ErrorException;
import org.apache.linkis.rpc.Sender;
import org.apache.linkis.server.Message;
import org.apache.linkis.server.security.SecurityFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

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

    @Autowired
    private HttpServletRequest httpServletRequest;
    @Autowired
    private HttpServletResponse httpServletResponse;

    @RequestMapping(path = "createWorkspace", method = RequestMethod.POST)
    public Message createWorkspace(@RequestBody CreateWorkspaceRequest createWorkspaceRequest) throws ErrorException {
        String userName = SecurityFilter.getLoginUsername(httpServletRequest);
        if (!dssWorkspaceService.checkAdmin(userName)) {
            return Message.error("您好，您不是管理员，没有权限建立工作空间");
        }
        String workSpaceName = createWorkspaceRequest.getWorkspaceName();
        String department = createWorkspaceRequest.getDepartment();
        String description = createWorkspaceRequest.getDescription();
        String stringTags = createWorkspaceRequest.getTags();
        String productName = createWorkspaceRequest.getProductName();
        int workspaceId = dssWorkspaceService.createWorkspace(workSpaceName, stringTags, userName, description, department, productName, "");
        AuditLogUtils.printLog(userName, workspaceId, workSpaceName, TargetTypeEnum.WORKSPACE, workspaceId, workSpaceName,
                OperateTypeEnum.CREATE, createWorkspaceRequest);
        return Message.ok().data("workspaceId", workspaceId).data("workspaceName", workSpaceName);
    }

    /**
     * 返回所有office,格式为：a-b-c
     *
     * @param workspaceId
     * @return
     */
    @RequestMapping(path = "listAllDepartments", method = RequestMethod.GET)
    public Message listAllDepartments(@RequestParam(value = WORKSPACE_ID_STR, required = false) String workspaceId) {
        List<String> departments = dssWorkspaceService.getAllDepartmentWithOffices();
        return Message.ok().data("departmentWithOffices", departments);
    }

    /**
     * 返回所有department
     *
     * @param workspaceId
     * @return
     */
    @RequestMapping(path = "getAllDepartments", method = RequestMethod.GET)
    public Message getAllDepartments(@RequestParam(value = WORKSPACE_ID_STR, required = false) String workspaceId) {
        List<String> departments = dssWorkspaceService.getAllDepartments();
        return Message.ok().data("departments", departments);
    }

    /**
     * 绑定工作空间和部门-科室关系
     *
     * @return
     */
    @PostMapping(value = "associateDepartments")
    public Message associateDepartments(@RequestBody Map<String, Object> params) throws Exception {
        String userName = SecurityFilter.getLoginUsername(httpServletRequest);
        Long workspaceId = Long.valueOf((Integer) params.get("workspaceId"));
        WorkspaceUtils.validateWorkspace(workspaceId, httpServletRequest);
        ArrayList<String> departmentWithOffices = (ArrayList<String>) params.get("departmentWithOffices");
        ArrayList<Integer> roles = (ArrayList<Integer>) params.get("roles");
        if (CollectionUtils.isEmpty(departmentWithOffices) || CollectionUtils.isEmpty(roles)) {
            return Message.error("参数不能为空！(params can not be null!)");
        }
        String roleStr = roles.stream().map(String::valueOf).collect(Collectors.joining(","));
        dssWorkspaceService.associateDepartments(workspaceId, String.join(",", departmentWithOffices), roleStr, userName);
        return Message.ok();
    }

    @GetMapping(value = "{workspaceId}/associateDepartmentsInfo")
    public Message getAssociateDepartments(@PathVariable("workspaceId") Long workspaceId) throws Exception {
        WorkspaceUtils.validateWorkspace(workspaceId, httpServletRequest);
        return Message.ok().data("associateDepartments", dssWorkspaceService.getAssociateDepartmentsInfo(workspaceId));
    }

    @RequestMapping(path = "getWorkspaces", method = RequestMethod.GET)
    public Message getWorkspaces() {
        String username = SecurityFilter.getLoginUsername(httpServletRequest);
        List<DSSWorkspace> workspaces = dssWorkspaceService.getWorkspaces(username);
        List<DSSWorkspaceVO> dssWorkspaceVOS = new ArrayList<>();
        for (DSSWorkspace workspace : workspaces) {
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

    @RequestMapping(path = "getWorkspaceHomePage", method = RequestMethod.GET)
    public Message getWorkspaceHomePage(@RequestParam(required = false, name = "micro_module") String moduleName) throws Exception {
        //如果用户的工作空间大于两个，那么就直接返回/workspace页面
        String username = SecurityFilter.getLoginUsername(httpServletRequest);
//        if (username != null && username.toLowerCase().startsWith("hduser")) {
//            return Message.error("Do not allow hduser* accounts to log in DSS system.");
//        }
        Workspace workspace = new Workspace();
        try {
            LOGGER.info("Put gateway url and cookies into workspace.");
            SSOHelper.addWorkspaceInfo(httpServletRequest, workspace);
        } catch (AppStandardWarnException ignored) {
        } // ignore it.
        dssUserService.insertOrUpdateUser(username, workspace);
        DSSWorkspaceHomePageVO dssWorkspaceHomePageVO = dssWorkspaceService.getWorkspaceHomePage(username, moduleName);
        return Message.ok().data("workspaceHomePage", dssWorkspaceHomePageVO);
    }

    @RequestMapping(path = "getOverview", method = RequestMethod.GET)
    public Message getOverview(@RequestParam(WORKSPACE_ID_STR) int workspaceId) {
        String username = SecurityFilter.getLoginUsername(httpServletRequest);
        String language = httpServletRequest.getHeader("Content-language");
        boolean isEnglish = "en".equals(language);
        DSSWorkspaceOverviewVO dssWorkspaceOverviewVO = dssWorkspaceService.getOverview(username, workspaceId, isEnglish);
        return Message.ok().data("overview", dssWorkspaceOverviewVO);
    }

    @RequestMapping(path = "refreshCache", method = RequestMethod.GET)
    public Message refreshCache() {
        workspaceDBHelper.retrieveFromDB();
        return Message.ok("refresh ok");
    }

    @RequestMapping(path = "workspaces", method = RequestMethod.GET)
    public Message getAllWorkspaces() {
        String username = SecurityFilter.getLoginUsername(httpServletRequest);
        List<DSSWorkspace> workspaces = dssWorkspaceService.getWorkspaces(username);
        return Message.ok().data("workspaces", workspaces);
    }

    /**
     * 获取所有工程或者单个工程
     *
     * @return
     */
    @RequestMapping(path = "getWorkSpaceStr", method = RequestMethod.GET)
    public Message getWorkSpaceStr(@RequestParam(name = "workspaceName") String workspaceName) {
        String username = SecurityFilter.getLoginUsername(httpServletRequest);
        DSSWorkspace workspaceEntity;
        try {
            workspaceEntity = dssWorkspaceService.getWorkspacesByName(workspaceName, username);
        } catch (DSSErrorException e) {
            LOGGER.error("User {} get workspace {} failed.", username, workspaceName, e);
            return Message.error(e);
        }
        Workspace workspace = SSOHelper.setAndGetWorkspace(httpServletRequest, httpServletResponse, workspaceEntity.getId(), workspaceName);
        return Message.ok("succeed.").data("workspaceStr", DSSCommonUtils.COMMON_GSON.toJson(workspace));
    }

    @RequestMapping(path = "/workspaces/{id}", method = RequestMethod.GET)
    public Message getWorkspacesById(@PathVariable("id") Long workspaceId) {
        String username = SecurityFilter.getLoginUsername(httpServletRequest);
        DSSWorkspace workspace = null;
        try {
            workspace = dssWorkspaceService.getWorkspacesById(workspaceId, username);
        } catch (DSSErrorException e) {
            LOGGER.error("User {} get workspace {} failed.", username, workspaceId, e);
            return Message.error(e);
        }
        SSOHelper.setAndGetWorkspace(httpServletRequest, httpServletResponse, workspace.getId(), workspace.getName());
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

    @RequestMapping(path = "/workspaces/exists", method = RequestMethod.GET)
    public Message getUsernameExistence(@RequestParam(required = false, name = "name") String name) {
        boolean exists = dssWorkspaceService.existWorkspaceName(name);
        return Message.ok().data("workspaceNameExists", exists);
    }

    @RequestMapping(path = "/workspaces", method = RequestMethod.POST)
    public Message addWorkspace(@RequestBody Map<String, String> json) throws ErrorException {
        String userName = SecurityFilter.getLoginUsername(httpServletRequest);
        Workspace workspace = SSOHelper.getWorkspace(httpServletRequest);
        if (!dssWorkspaceService.checkAdmin(userName)) {
            return Message.error("您好，您不是管理员，没有权限建立工作空间");

        }
        String workspaceName = json.get("name");
        if (dssWorkspaceService.existWorkspaceName(workspaceName)) {
            return Message.error("工作空间名重复");
        }
        String department = json.get("department");
        String label = json.get("label");
        String description = json.get("description");
        String workspaceType = json.get("workspace_type");

        String productName = "DSS";
        int workspaceId = dssWorkspaceService.createWorkspace(workspaceName, label, userName, description, department, productName, workspaceType);
        AuditLogUtils.printLog(userName, workspaceId, workspaceName, TargetTypeEnum.WORKSPACE, workspaceId, workspaceName,
                OperateTypeEnum.CREATE, json);
        return Message.ok().data("workspaceId", workspaceId);
    }

    @RequestMapping(path = "workspaces/{workspaceId}/appconns", method = RequestMethod.GET)
    public Message getWorkspaceAppConns(@PathVariable("workspaceId") Long workspaceId) {
        String header = httpServletRequest.getHeader("Content-language").trim();
        boolean isChinese = "zh-CN".equals(header);
        String username = SecurityFilter.getLoginUsername(httpServletRequest);
        Workspace workspace = SSOHelper.getWorkspace(httpServletRequest);
        List<WorkspaceMenuVo> menuAppconnVos;
        try {
            menuAppconnVos = dssWorkspaceService.getWorkspaceAppConns(workspace, workspaceId, username, isChinese);
        } catch (DSSErrorException e) {
            LOGGER.warn("{} get appconns from workspace {} failed.", username, workspaceId, e);
            return Message.error(e);
        }
        return Message.ok().data("menus", menuAppconnVos);
    }


    @RequestMapping(path = "/workspaces/{workspaceId}/favorites", method = RequestMethod.GET)
    public Message getWorkspaceFavorites(@PathVariable("workspaceId") Long workspaceId, @RequestParam(value = "type", required = false) String type) {
        String header = httpServletRequest.getHeader("Content-language").trim();
        boolean isChinese = "zh-CN".equals(header);
        String username = SecurityFilter.getLoginUsername(httpServletRequest);
        List<WorkspaceFavoriteVo> favorites = dssWorkspaceService.getWorkspaceFavorites(workspaceId, username, isChinese, type == null ? "" : type);
        Set<WorkspaceFavoriteVo> favoriteVos = new HashSet<>(favorites);
        return Message.ok().data("favorites", favoriteVos);
    }

    /**
     * 应用加入收藏，返回收藏后id
     *
     * @param json
     * @return
     */
    @RequestMapping(path = "/workspaces/{workspaceId}/favorites", method = RequestMethod.POST)
    public Message addFavorite(@PathVariable("workspaceId") Long workspaceId, @RequestBody Map<String, String> json) {
        String username = SecurityFilter.getLoginUsername(httpServletRequest);
        Long menuApplicationId = Long.valueOf(json.get("menuApplicationId"));
        String type = json.get("type") == null ? "" : json.get("type");
        String workspaceName = dssWorkspaceService.getWorkspaceName(workspaceId);
        Long favoriteId = dssWorkspaceService.addFavorite(username, workspaceId, menuApplicationId, type);
        AuditLogUtils.printLog(username, workspaceId, workspaceName, TargetTypeEnum.WORKSPACE, workspaceId, workspaceName,
                OperateTypeEnum.ADD_TO_FAVORITES, json);
        return Message.ok().data("favoriteId", favoriteId);
    }


    @RequestMapping(path = "/workspaces/{workspaceId}/favorites/{appconnId}", method = RequestMethod.POST)
    public Message deleteFavorite(@PathVariable("workspaceId") Long workspaceId, @PathVariable("appconnId") Long appconnId, @RequestBody Map<String, String> json) {
        String username = SecurityFilter.getLoginUsername(httpServletRequest);
        String type = json.get("type") == null ? "" : json.get("type");
        String workspaceName = dssWorkspaceService.getWorkspaceName(workspaceId);
        Long favoriteId = dssWorkspaceService.deleteFavorite(username, appconnId, workspaceId, type);
        AuditLogUtils.printLog(username, workspaceId, workspaceName, TargetTypeEnum.WORKSPACE, workspaceId, workspaceName,
                OperateTypeEnum.REM_FROM_FAVORITES, json);
        return Message.ok().data("favoriteId", favoriteId);
    }

    @RequestMapping(path = "git", method = RequestMethod.POST)
    public Message associateGit(@RequestBody GitUserEntity gitUser) {
        String username = SecurityFilter.getLoginUsername(httpServletRequest);
        if (StringUtils.isEmpty(gitUser.getType())) {
            return Message.error("需指定当前帐号配置读写权限");
        }
        Sender gitSender = DSSSenderServiceFactory.getOrCreateServiceInstance().getGitSender();
        GitUserUpdateRequest gitUserUpdateRequest = new GitUserUpdateRequest();
        gitUserUpdateRequest.setGitUser(gitUser);
        gitUserUpdateRequest.setUsername(username);
        GitUserUpdateResponse response = RpcAskUtils.processAskException(gitSender.ask(gitUserUpdateRequest), GitUserUpdateResponse.class, GitUserUpdateRequest.class);
        if (0 == response.getStatus()) {
            return Message.ok();
        } else {
            String errorMessage = response.getMsg();
            String workspaceName = dssWorkspaceService.getWorkspaceName(response.getWorkspaceId());
            return Message.error(errorMessage + " 工作空间名称为：" + workspaceName);
        }
    }

    @RequestMapping(path = "git", method = RequestMethod.GET)
    public Message selectGit(@RequestParam Long workspaceId, @RequestParam String type) {
        String username = SecurityFilter.getLoginUsername(httpServletRequest);
        Sender gitSender = DSSSenderServiceFactory.getOrCreateServiceInstance().getGitSender();
        GitUserInfoRequest gitUserInfoRequest = new GitUserInfoRequest();
        gitUserInfoRequest.setWorkspaceId(workspaceId);
        gitUserInfoRequest.setType(type);
        GitUserInfoResponse response = RpcAskUtils.processAskException(gitSender.ask(gitUserInfoRequest), GitUserInfoResponse.class, GitUserInfoRequest.class);
        GitUserEntity gitUser = response.getGitUser();
        return Message.ok().data("gitUser", gitUser);
    }


    @RequestMapping(path = "gitConnect", method = RequestMethod.GET)
    public Message gitConnect(@RequestParam String gitUserName, @RequestParam String gitPassword) {
        String username = SecurityFilter.getLoginUsername(httpServletRequest);
        Sender gitSender = DSSSenderServiceFactory.getOrCreateServiceInstance().getGitSender();
        GitConnectRequest connectRequest = new GitConnectRequest(gitUserName, gitPassword);
        GitConnectResponse connectResponse = RpcAskUtils.processAskException(gitSender.ask(connectRequest), GitConnectResponse.class, GitConnectRequest.class);
        if (connectResponse.getConnect()) {
            return Message.ok().data("result", "success");
        }else {
            return Message.ok().data("result", "invalid");
        }
    }


}

