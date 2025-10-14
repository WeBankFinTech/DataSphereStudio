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

import cn.hutool.core.util.StrUtil;
import com.webank.wedatasphere.dss.common.StaffInfoGetter;
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
import com.webank.wedatasphere.dss.framework.workspace.bean.itsm.ItsmRequest;
import com.webank.wedatasphere.dss.framework.workspace.bean.itsm.ItsmResponse;
import com.webank.wedatasphere.dss.framework.workspace.bean.request.CreateWorkspaceRequest;
import com.webank.wedatasphere.dss.framework.workspace.bean.vo.DSSWorkspaceHomePageVO;
import com.webank.wedatasphere.dss.framework.workspace.bean.vo.DSSWorkspaceOverviewVO;
import com.webank.wedatasphere.dss.framework.workspace.bean.vo.DSSWorkspaceVO;
import com.webank.wedatasphere.dss.framework.workspace.service.DSSWorkspaceRoleCheckService;
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
import static com.webank.wedatasphere.dss.standard.sso.utils.SSOHelper.USERNAME_NAME_COOKIE_KEY;

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
    @Autowired
    private StaffInfoGetter staffInfoGetter;


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
     * 提供给ITSM 单使用， 新建或者修改工作空间信息接口
     */
    @RequestMapping(path = "updateWorkspace", method = RequestMethod.POST)
    public ItsmResponse updateWorkspace(@RequestBody ItsmRequest itsmRequest, HttpServletRequest req, HttpServletResponse resp) {
        LOGGER.info("itrtsm try to update workspace, itsm id:{}.", itsmRequest.getExternalId());
        // 获取请求头中的timestamp和sign字段
        String timestamp = req.getHeader("timeStamp");
        String sign = req.getHeader("sign");
        // 验证鉴权
        if (!WorkspaceUtils.validateAuth(timestamp, sign)) {
            // 鉴权失败，返回错误信息
            LOGGER.error("Authentication failed.");
            // 设置HTTP状态码为403
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return ItsmResponse.error().retDetail("Authentication failed.");
        }
        List<Map<String, String>> dataList = itsmRequest.getDataList();

        // 鉴权成功
        if (dataList.isEmpty()) {
            return ItsmResponse.error().retDetail("data is empty");
        }

        String createUser = itsmRequest.getCreateUser();

        for (Map<String, String> table : dataList) {
            String workspaceName = table.get("workspaceName");

            if(StrUtil.isEmpty(workspaceName)){
                LOGGER.error(workspaceName + " workspace is empty");
                return ItsmResponse.error().retDetail("工作空间信息不能为空！");
            }

            workspaceName = workspaceName.trim();
            // 判断命名空间是否存在
            boolean exists = dssWorkspaceService.existWorkspaceName(workspaceName);
            String option = table.get("option");
            String desc = table.get("desc");
            // 工作空间owner为NULl，则获取提单人信息
            String workspaceOwner = table.getOrDefault("workspaceOwner", createUser).trim();
            String oldOwner = table.get("oldOwner");
            String newOwner = table.get("newOwner");

            if ("add".equalsIgnoreCase(option)) {
                // 新建
                if (exists) {
                    LOGGER.error(workspaceName + " workspace info is exists!");
                    return ItsmResponse.error().retDetail(workspaceName + "同名工作空间已存在,不能再次创建！");
                }

                try {
                    // 判断用户是否可以设置为管理员
                    if(!dssWorkspaceService.checkUserIfSettingAdmin(workspaceOwner)){
                        return ItsmResponse.error().retDetail("无权限进行该操作");
                    }

                    int workspaceId = dssWorkspaceService.createWorkspace(workspaceName, "", workspaceOwner, desc,
                            null, "DSS", "project");

                    AuditLogUtils.printLog(workspaceOwner, workspaceId, workspaceName, TargetTypeEnum.WORKSPACE, workspaceId, workspaceName,
                            OperateTypeEnum.CREATE, itsmRequest);

                } catch (Exception e) {
                    LOGGER.info("{} workspace add fail , (工作空间 申请失败)", workspaceName);
                    LOGGER.error(e.getMessage());
                    return  ItsmResponse.error().retDetail(workspaceName + "工作空间新建失败!\n" + e.getMessage());
                }

            } else if ("modify".equalsIgnoreCase(option)) {
                // 修改
                if (!exists) {
                    LOGGER.error(workspaceName + " workspace info not exists, not modify!");
                    return ItsmResponse.error().retDetail(workspaceName + "工作空间名称不存在,不能进行修改！");
                }

                if(StrUtil.isEmpty(oldOwner) || StrUtil.isEmpty(newOwner)){
                    return ItsmResponse.error().retDetail(workspaceName + "工作空间的新Owner和原Owner不能为空！");
                }

                oldOwner = oldOwner.trim();
                newOwner = newOwner.trim();

                if(oldOwner.equalsIgnoreCase(newOwner)){
                    return ItsmResponse.error().retDetail(workspaceName + "工作空间的新Owner和原Owner不能是同一个用户！");
                }

                try {

                    // 判断用户是否可以设置为管理员
                    if(!dssWorkspaceService.checkUserIfSettingAdmin(newOwner)){
                        return ItsmResponse.error().retDetail("无权限进行该操作");
                    }

                    int workspaceId = dssWorkspaceService.transferWorkspace(workspaceName,oldOwner,newOwner,desc);

                    AuditLogUtils.printLog(newOwner, workspaceId, workspaceName, TargetTypeEnum.WORKSPACE, workspaceId, workspaceName,
                            OperateTypeEnum.UPDATE, itsmRequest);

                } catch (Exception e) {
                    LOGGER.error("{} workspace modify fail , (工作空间 申请失败)", workspaceName);
                    LOGGER.error(e.getMessage(), e);
                    return  ItsmResponse.error().retDetail(workspaceName + "工作空间信息修改失败!\n"+ e.getMessage());
                }

            } else {
                return ItsmResponse.error().retDetail(workspaceName + "无法进行该操作,请正确选择操作选项!");
            }

        }
        LOGGER.info("success to update workspace, itsm id:{}.", itsmRequest.getExternalId());
        return ItsmResponse.ok().retDetail("Success to update workspace");

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
        int workspaceId = dssWorkspaceService.getWorkspaceId(workspaceName);
        Workspace workspace = SSOHelper.setAndGetWorkspace(httpServletRequest, httpServletResponse, workspaceId, workspaceName);
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

        Set<String> allUsernames = staffInfoGetter.getAllUsernames();
        if(allUsernames.contains(username) || "hadoop".equals(username)) {
            SSOHelper.addUsernameCookie(httpServletRequest, httpServletResponse, username);
        }else{
            SSOHelper.deleteCookieByName(httpServletRequest, httpServletResponse, USERNAME_NAME_COOKIE_KEY);
        }

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


    @RequestMapping(value = "/updateWorkspaceInfo",method = RequestMethod.POST)
    public Message updateWorkspaceInfo(@RequestBody Map<String, String> json) throws  DSSErrorException{

        Workspace workspace = SSOHelper.getWorkspace(httpServletRequest);
        String username = SecurityFilter.getLoginUsername(httpServletRequest);

        String workspaceId= json.getOrDefault("workspaceId",String.valueOf(workspace.getWorkspaceId()));

        if(!workspaceId.equals(String.valueOf(workspace.getWorkspaceId()))){
            throw  new DSSErrorException(90053,"当前工作空间与cookie中的不一致，重新刷新页面后在操作");
        }

        String enabledFlowKeywordsCheck = json.getOrDefault("enabledFlowKeywordsCheck","0");
        String isDefaultReference = json.getOrDefault("isDefaultReference","0");

        DSSWorkspace dssWorkspace = new DSSWorkspace();
        dssWorkspace.setId(Integer.parseInt(workspaceId));
        dssWorkspace.setEnabledFlowKeywordsCheck(enabledFlowKeywordsCheck);
        dssWorkspace.setIsDefaultReference(isDefaultReference);

        if (!dssWorkspaceService.checkAdminByWorkspace(username, dssWorkspace.getId())) {
            return Message.error(String.format("%s 用户不是当前工作空间管理员,无权限进行该操作!",username));
        }

        dssWorkspaceService.updateWorkspaceInfo(dssWorkspace);

        AuditLogUtils.printLog(username, workspaceId, workspace.getWorkspaceName(), TargetTypeEnum.WORKSPACE, workspaceId, workspace.getWorkspaceName(),
                OperateTypeEnum.UPDATE, json);

        return  Message.ok().data("workspaceId", workspaceId);

    }
}

