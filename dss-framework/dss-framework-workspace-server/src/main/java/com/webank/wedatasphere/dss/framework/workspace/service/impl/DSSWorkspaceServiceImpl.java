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

package com.webank.wedatasphere.dss.framework.workspace.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import com.webank.wedatasphere.dss.appconn.core.AppConn;
import com.webank.wedatasphere.dss.appconn.core.ext.OnlySSOAppConn;
import com.webank.wedatasphere.dss.appconn.manager.AppConnManager;
import com.webank.wedatasphere.dss.appconn.manager.utils.AppInstanceConstants;
import com.webank.wedatasphere.dss.common.StaffInfoGetter;
import com.webank.wedatasphere.dss.common.entity.workspace.DSSStarRocksCluster;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.common.exception.DSSRuntimeException;
import com.webank.wedatasphere.dss.common.label.EnvDSSLabel;
import com.webank.wedatasphere.dss.framework.admin.conf.AdminConf;
import com.webank.wedatasphere.dss.framework.admin.service.DssAdminUserService;
import com.webank.wedatasphere.dss.framework.common.exception.DSSFrameworkWarnException;
import com.webank.wedatasphere.dss.framework.workspace.bean.*;
import com.webank.wedatasphere.dss.framework.workspace.bean.dto.response.WorkspaceDepartmentVo;
import com.webank.wedatasphere.dss.framework.workspace.bean.dto.response.WorkspaceFavoriteVo;
import com.webank.wedatasphere.dss.framework.workspace.bean.dto.response.WorkspaceMenuAppconnVo;
import com.webank.wedatasphere.dss.framework.workspace.bean.dto.response.WorkspaceMenuVo;
import com.webank.wedatasphere.dss.framework.workspace.bean.request.UpdateWorkspaceStarRocksClusterRequest;
import com.webank.wedatasphere.dss.framework.workspace.bean.vo.*;
import com.webank.wedatasphere.dss.framework.workspace.constant.ApplicationConf;
import com.webank.wedatasphere.dss.framework.workspace.dao.*;
import com.webank.wedatasphere.dss.framework.workspace.exception.DSSWorkspaceDuplicateNameException;
import com.webank.wedatasphere.dss.framework.workspace.service.*;
import com.webank.wedatasphere.dss.framework.workspace.util.CommonRoleEnum;
import com.webank.wedatasphere.dss.framework.workspace.util.DSSWorkspaceConstant;
import com.webank.wedatasphere.dss.framework.workspace.util.WorkspaceDBHelper;
import com.webank.wedatasphere.dss.common.conf.WorkspaceServerConstant;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.app.sso.builder.SSOUrlBuilderOperation;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;
import com.webank.wedatasphere.dss.standard.sso.utils.SSOHelper;
import com.webank.wedatasphere.dss.workflow.entity.StarRocksNodeInfo;
import com.webank.wedatasphere.dss.workflow.entity.request.BatchEditFlowRequest;
import com.webank.wedatasphere.dss.workflow.entity.request.EditFlowRequest;
import com.webank.wedatasphere.dss.workflow.service.DSSFlowService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.linkis.common.exception.ErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.webank.wedatasphere.dss.framework.workspace.util.DSSWorkspaceConstant.DEFAULT_DEMO_WORKSPACE_NAME;

public class DSSWorkspaceServiceImpl implements DSSWorkspaceService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DSSWorkspaceServiceImpl.class);
    private static final Pattern IPV4_PATTERN = Pattern.compile(
            "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$"
    );
    @Autowired
    private DSSWorkspaceMapper dssWorkspaceMapper;
    @Autowired
    private DSSWorkspaceUserMapper dssWorkspaceUserMapper;
    @Autowired
    private DSSWorkspaceMenuMapper dssWorkspaceMenuMapper;
    @Autowired
    private WorkspaceDBHelper workspaceDBHelper;
    @Autowired
    private DSSWorkspaceService dssWorkspaceService;
    @Autowired
    private DSSWorkspaceUserService dssWorkspaceUserService;
    @Autowired
    private DssAdminUserService dssUserService;
    @Autowired
    private DSSMenuRoleMapper dssMenuRoleMapper;
    @Autowired
    private DSSWorkspaceHomepageMapper dssWorkspaceHomepageMapper;
    @Autowired
    private DSSComponentRoleMapper dssComponentRoleMapper;
    @Autowired
    private WorkspaceMapper workspaceMapper;

    @Autowired
    private StaffInfoGetter staffInfoGetter;

    @Autowired
    private DSSWorkspaceRoleService dssWorkspaceRoleService;

    @Autowired
    private DSSWorkspaceRoleCheckService roleCheckService;

    @Autowired
    private DSSWorkspaceStarRocksClusterMapper dssWorkspaceStarRocksClusterMapper;
    @Autowired
    private DSSFlowService dssFlowService;
    //创建工作空间
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int createWorkspace(String workspaceName, String tags, String userName,
                               String description, String department, String productName, String workspaceType) throws ErrorException {
        DSSWorkspace dssWorkspace = new DSSWorkspace();
        dssWorkspace.setDescription(description);
        dssWorkspace.setName(workspaceName);
        dssWorkspace.setLabel(tags);
        dssWorkspace.setCreateBy(userName);
        dssWorkspace.setDepartment(department);
        dssWorkspace.setProduct(productName);
        dssWorkspace.setCreateTime(new Date());
        dssWorkspace.setLastUpdateTime(new Date());
        dssWorkspace.setLastUpdateUser(userName);
        dssWorkspace.setWorkspaceType(workspaceType);
        try {
            dssWorkspaceMapper.createWorkSpace(dssWorkspace);
        } catch (Exception e) {
            DSSWorkspaceDuplicateNameException exception1 = new DSSWorkspaceDuplicateNameException(50010, "工作空间名重复");
            exception1.initCause(e);
            throw exception1;
        }
        Long userId = dssWorkspaceUserMapper.getUserID(userName);
        dssWorkspaceUserMapper.insertUserRoleInWorkspace(dssWorkspace.getId(),
                workspaceDBHelper.getRoleIdByName(CommonRoleEnum.ADMIN.getName()), new Date(), userName, userName, userId, userName);
        dssMenuRoleMapper.insertBatch(workspaceDBHelper.generateDefaultWorkspaceMenuRole(dssWorkspace.getId(), userName));
        dssWorkspaceHomepageMapper.insertBatch(workspaceDBHelper.generateDefaultWorkspaceHomepage(dssWorkspace.getId(), userName));
        dssComponentRoleMapper.insertBatch(workspaceDBHelper.generateDefaultWorkspaceComponentPrivs(dssWorkspace.getId(), userName));
        setAllRolesToWorkspaceCreator(dssWorkspace.getId(), userName);
        return dssWorkspace.getId();
    }

    private void setAllRolesToWorkspaceCreator(int workspaceId, String userName) {
        List<Integer> roleIds = dssWorkspaceService.getWorkspaceRoles(workspaceId)
                .stream()
                .map(DSSWorkspaceRoleVO::getRoleId)
                .collect(Collectors.toList());
        dssWorkspaceUserService.updateWorkspaceUser(roleIds, workspaceId, userName, userName);
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public int transferWorkspace(String workspaceName, String oldOwner, String newOwner, String desc) throws DSSErrorException {

        List<DSSWorkspace> dssWorkspaces = workspaceMapper.findByWorkspaceName(workspaceName);

        if (dssWorkspaces == null || dssWorkspaces.isEmpty()) {
            throw new DSSFrameworkWarnException(30021, workspaceName + "工作空间不存在!");
        } else if (dssWorkspaces.size() > 1) {
            throw new DSSFrameworkWarnException(30021, "Too many workspaces named " + workspaceName);
        }

        DSSWorkspace dssWorkspace =  dssWorkspaces.get(0);

        LOGGER.info("workspace name is {}, oldOwner is {}, newOwner is {}", workspaceName, oldOwner, newOwner);
        dssWorkspace.setCreateBy(newOwner);
        dssWorkspace.setLastUpdateTime(new Date());
        dssWorkspace.setLastUpdateUser(newOwner);
        dssWorkspace.setDescription(desc);
        LOGGER.info("updateWorkSpace name is {}", dssWorkspace.getName());
        // 更改创建者 和更新时间
        dssWorkspaceMapper.updateWorkSpace(dssWorkspace);

        List<DSSWorkspaceMenuRole> dssWorkspaceMenuRoleList = dssMenuRoleMapper.queryWorkspaceMenuRole(dssWorkspace.getId(), oldOwner);

        if (CollectionUtils.isNotEmpty(dssWorkspaceMenuRoleList)) {
            LOGGER.info("updateWorkspaceMenuRoleById  workspace name is {}, menuRoleIdList size is {}", workspaceName, dssWorkspaceMenuRoleList.size());
            List<Integer> menuRoleIdList = dssWorkspaceMenuRoleList.stream().map(DSSWorkspaceMenuRole::getId).collect(Collectors.toList());
            dssMenuRoleMapper.updateWorkspaceMenuRoleById(menuRoleIdList, newOwner);

        }

        List<DSSWorkspaceComponentPriv> dssWorkspaceComponentList = dssComponentRoleMapper.queryDSSComponentRole(dssWorkspace.getId(), oldOwner);

        if (CollectionUtils.isNotEmpty(dssWorkspaceComponentList)) {
            LOGGER.info("updateDSSComponentRoleById  workspace name is {}, componentList size is {}", workspaceName, dssWorkspaceComponentList.size());
            List<Integer> componetPrivIdList = dssWorkspaceComponentList.stream().map(DSSWorkspaceComponentPriv::getId).collect(Collectors.toList());
            dssComponentRoleMapper.updateDSSComponentRoleById(componetPrivIdList, newOwner);
        }

        List<Integer> roleIds = dssWorkspaceService.getWorkspaceRoles(dssWorkspace.getId())
                .stream()
                .map(DSSWorkspaceRoleVO::getRoleId)
                .collect(Collectors.toList());
        dssWorkspaceUserService.updateWorkspaceRole(roleIds, dssWorkspace.getId(), newOwner);

        LOGGER.info("success transfer workspace {} ", workspaceName);
        return dssWorkspace.getId();
    }


    @Override
    public boolean checkUserIfSettingAdmin(String username){

        int roleId= workspaceDBHelper.getRoleIdByName(CommonRoleEnum.ADMIN.getName());
        // 判断用户是否可以设置为管理员(代理和非合作伙伴不能设置为管理员)
        return roleCheckService.checkUserRolesOperation(username, Collections.singletonList(roleId));
    }

    //获取所有的工作空间
    @Override
    public List<DSSWorkspace> getWorkspaces(String userName) {
        List<DSSWorkspace> workspaces = dssWorkspaceMapper.getWorkspaces(userName);
        workspaces = sortDssWorkspacesIndex(workspaces);
        Integer isAdmin = dssWorkspaceUserMapper.isAdmin(userName);
        // 获取默认工作空间
        DSSUserDefaultWorkspace dssUserDefaultWorkspace = dssWorkspaceUserMapper.getDefaultWorkspaceByUsername(userName);
        Long workspaceId = dssUserDefaultWorkspace == null ? null : dssUserDefaultWorkspace.getWorkspaceId();
        // 如果用户未设置默认工作空间,且用户为管理员
        if (workspaceId == null && isAdmin == 1){
            return workspaces;
        }

        List<DSSWorkspace> retWorkspaces = new ArrayList<>();
        String[] defaultDemoWorkspaceNames = DEFAULT_DEMO_WORKSPACE_NAME.getValue().split(",");
        for (DSSWorkspace workspace : workspaces) {

            // 除非用户是管理员, 否则踢掉那个演示demo工作空间
            if (isAdmin != 1 && ArrayUtils.contains(defaultDemoWorkspaceNames, workspace.getName()) ) {
                continue;
            }

            boolean  isDefaultWorkspace = workspaceId != null && workspace.getId() == workspaceId.intValue();
            workspace.setIsDefaultWorkspace(isDefaultWorkspace);
            retWorkspaces.add(workspace);
        }

        return  retWorkspaces;
    }

    @Override
    public DSSWorkspaceHomePageVO getWorkspaceHomePage(String userName, String moduleName) throws DSSErrorException {
        List<DSSWorkspace> dssWorkspaces = dssWorkspaceMapper.getWorkspaces(userName);
        List<Integer> workspaceIds = sortDssWorkspacesIndex(dssWorkspaces).stream().map(DSSWorkspace::getId).collect(Collectors.toList());
        DSSWorkspaceHomePageVO dssWorkspaceHomePageVO = new DSSWorkspaceHomePageVO();
        if (workspaceIds.size() == 0) {
            Long userId = dssWorkspaceUserMapper.getUserID(userName);
            int workspaceId = dssWorkspaceMapper.getWorkspaceIdByName(DSSWorkspaceConstant.DEFAULT_WORKSPACE_NAME.getValue());
            Integer analyserRole = workspaceDBHelper.getRoleIdByName(CommonRoleEnum.ANALYSER.getName());

            dssWorkspaceUserMapper.insertUserRoleInWorkspace(workspaceId, analyserRole, new Date(),
                    userName, "system", userId, "system");
            Integer workspace0xId = dssWorkspaceMapper.getWorkspaceIdByName(DSSWorkspaceConstant.DEFAULT_0XWORKSPACE_NAME.getValue());
            if (workspace0xId != null) {
                dssWorkspaceUserMapper.insertUserRoleInWorkspace(workspace0xId, analyserRole, new Date(),
                        userName, "system", userId, "system");
            }
            //todo 初始化做的各项事情改为listener模式
            //为新用户自动加入部门关联的工作空间
            joinWorkspaceForNewUser(userName, userId, workspaceId, workspace0xId, analyserRole);

            //若路径没有workspaceId会出现页面没有首页、管理台
            String homepageUrl = "/home" + "?workspaceId=" + workspaceId;
            if (ApplicationConf.HOMEPAGE_MODULE_NAME.getValue().equalsIgnoreCase(moduleName)) {
                homepageUrl = ApplicationConf.HOMEPAGE_URL.getValue() + workspaceIds.get(0);
            }
            dssWorkspaceHomePageVO.setHomePageUrl(homepageUrl);
            dssWorkspaceHomePageVO.setWorkspaceId(workspaceId);
            dssWorkspaceHomePageVO.setRoleName(CommonRoleEnum.ANALYSER.getName());
        } else if (workspaceIds.size() == 1) {
            //只有一个工作空间，那么就返回该工作空间的首页
            List<Integer> roleIds = dssWorkspaceUserMapper.getRoleInWorkspace(workspaceIds.get(0), userName);
            int minRoleId = Collections.min(roleIds);
            String homepageUrl = dssWorkspaceUserMapper.getHomepageUrl(workspaceIds.get(0), minRoleId);
            if ("/workspace".equals(homepageUrl)) {
                //兼容旧的默认首页配置
                homepageUrl = "/workspaceHome";
            } else if ("/workspaceHome/scheduleCenter".equals(homepageUrl)) {
                //兼容旧的生产中心首页配置
                homepageUrl = "/scheduleCenter";
            }
            if (StringUtils.isNotEmpty(homepageUrl)) {
                homepageUrl = homepageUrl + "?workspaceId=" + workspaceIds.get(0);
            } else {
                homepageUrl = "/home" + "?workspaceId=" + workspaceIds.get(0);
            }
            if (ApplicationConf.HOMEPAGE_MODULE_NAME.getValue().equalsIgnoreCase(moduleName)) {
                homepageUrl = ApplicationConf.HOMEPAGE_URL.getValue() + workspaceIds.get(0);
            }
            dssWorkspaceHomePageVO.setHomePageUrl(homepageUrl);
            dssWorkspaceHomePageVO.setWorkspaceId(workspaceIds.get(0));
            dssWorkspaceHomePageVO.setRoleName(workspaceDBHelper.getRoleNameById(minRoleId));
        } else {
            Integer workspaceId = workspaceIds.get(0);
            String homepageUrl;


            if (ApplicationConf.HOMEPAGE_MODULE_NAME.getValue().equalsIgnoreCase(moduleName)) {

                homepageUrl = ApplicationConf.HOMEPAGE_URL.getValue() + workspaceId;

            }else{

                workspaceId = getDefaultWorkspaceId(dssWorkspaces,userName,workspaceIds);
                homepageUrl = "/workspaceHome?workspaceId=" + workspaceId;

            }

            dssWorkspaceHomePageVO.setWorkspaceId(workspaceId);
            dssWorkspaceHomePageVO.setHomePageUrl(homepageUrl);
        }
        return dssWorkspaceHomePageVO;
    }

    private List<DSSWorkspace> sortDssWorkspacesIndex(List<DSSWorkspace> dssWorkspaces) {
        List<DSSWorkspace> dssWorkspaceReq = dssWorkspaces.stream().filter(l -> !DEFAULT_DEMO_WORKSPACE_NAME.getValue().equals(l.getName()))
                .collect(Collectors.toList());
        if(dssWorkspaces.size()>1){
            dssWorkspaceReq = dssWorkspaces.stream().filter(l -> !DSSWorkspaceConstant.DEFAULT_WORKSPACE_NAME.getValue().equals(l.getName())
                        &&!DSSWorkspaceConstant.DEFAULT_0XWORKSPACE_NAME.getValue().equals(l.getName()))
                    .collect(Collectors.toList());
            Map<String, DSSWorkspace> workspaceMap = dssWorkspaces.stream().collect(Collectors.toMap(DSSWorkspace::getName, w->w));
            if(workspaceMap.keySet().contains(DSSWorkspaceConstant.DEFAULT_WORKSPACE_NAME.getValue())){
                dssWorkspaceReq.add(workspaceMap.get(DSSWorkspaceConstant.DEFAULT_WORKSPACE_NAME.getValue()));
            }
            if(workspaceMap.keySet().contains(DSSWorkspaceConstant.DEFAULT_0XWORKSPACE_NAME.getValue())){
                dssWorkspaceReq.add(workspaceMap.get(DSSWorkspaceConstant.DEFAULT_0XWORKSPACE_NAME.getValue()));
            }
        }
       return dssWorkspaceReq;
    }

    private Integer getDefaultWorkspaceId(List<DSSWorkspace> dssWorkspaces, String username,List<Integer> workspaceIds){

        LOGGER.info("get user {} default workspace",username);
        DSSUserDefaultWorkspace dssUserDefaultWorkspace = dssWorkspaceUserMapper.getDefaultWorkspaceByUsername(username);

        if(dssUserDefaultWorkspace != null && workspaceIds.contains(dssUserDefaultWorkspace.getWorkspaceId().intValue())){
            LOGGER.info("get user {} custom setting default workspace {}",
                    username,dssUserDefaultWorkspace.getWorkspaceId());
            return  dssUserDefaultWorkspace.getWorkspaceId().intValue();
        }

        String[] defaultWorkspaceName = ApplicationConf.HOMEPAGE_DEFAULT_WORKSPACE.getValue().split(",");

        DSSWorkspace defaultWorkspace = dssWorkspaces.stream()
                .filter(workspace -> Arrays.stream(defaultWorkspaceName)
                        .anyMatch(n->n.equalsIgnoreCase(workspace.getName())))
                .findFirst().orElse(null);

        if (defaultWorkspace != null){
            LOGGER.info("get user {} homepage default config workspace  {}",
                    username,defaultWorkspace.getId());
            return  defaultWorkspace.getId();
        }
        LOGGER.info("get user {} by add workspace time , get first workspace {}",username,workspaceIds.get(0));
        return  workspaceIds.get(0);

    }

    @Override
    public List<DSSWorkspaceUserVO> getWorkspaceUsers(String workspaceId, String department, String username,
                                                      String roleName, int pageNow, int pageSize, List<Long> total) {
        String roleId = StringUtils.isBlank(roleName) ? null : String.valueOf(workspaceDBHelper.getRoleIdByName(roleName));
        PageHelper.startPage(pageNow, pageSize);
        // 转译用户名模糊查询中的特殊字符
        String queryName = StringUtils.isNotEmpty(username) && username.contains("_")
                ? username.replaceAll("_", "\\\\_")
                : username;
        List<DSSWorkspaceUser> workspaceUsers = dssWorkspaceUserMapper.getWorkspaceUsers(workspaceId, queryName, roleId);
        PageInfo<DSSWorkspaceUser> pageInfo = new PageInfo<>(workspaceUsers);
        total.add(pageInfo.getTotal());
        return workspaceUsers.stream().map(
                        workspaceUser -> changeToUserVO(workspaceUser,
                                Arrays.stream(workspaceUser.getRoleIds().split(",")).map(Integer::valueOf).collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getWorkspaceUsers(String workspaceId) {
        return
                dssWorkspaceUserMapper.getWorkspaceUsers(workspaceId, null, null).stream()
                        .map(DSSWorkspaceUser::getUsername).collect(Collectors.toList());
    }

    private DSSWorkspaceUserVO changeToUserVO(DSSWorkspaceUser dssWorkspaceUser, List<Integer> roles) {
        DSSWorkspaceUserVO vo = new DSSWorkspaceUserVO();
        String userName = dssWorkspaceUser.getUsername();
        vo.setName(userName);
        String orgFullName = staffInfoGetter.getFullOrgNameByUsername(userName);
        if (StringUtils.isNotEmpty(orgFullName)) {
            try {
                String[] fullNameArray = orgFullName.split(WorkspaceServerConstant.DEFAULT_STAFF_SPLIT);
                String departmentName = fullNameArray[0];
                String officeName = "";
                if (fullNameArray.length > 1) {
                    officeName = fullNameArray[1];
                }
                vo.setDepartment(departmentName);
                vo.setOffice(officeName);
            } catch (Exception e) {
                //LOGGER.warn("fail to get department and office {} ", e.getMessage());
                vo.setDepartment(WorkspaceServerConstant.DEFAULT_DEPARTMENT);
                vo.setOffice(WorkspaceServerConstant.DEFAULT_OFFICE);
            }
        }
        vo.setRoles(roles);
        vo.setCreator(dssWorkspaceUser.getCreator());
        vo.setJoinTime(dssWorkspaceUser.getJoinTime());
        vo.setUpdateTime(dssWorkspaceUser.getUpdateTime());
        vo.setUpdateUser(dssWorkspaceUser.getUpdateUser());
        return vo;
    }

    @Override
    public List<DSSWorkspaceRoleVO> getWorkspaceRoles(int workspaceId) {
        return workspaceDBHelper.getRoleVOs(workspaceId);
    }

    @Override
    public DSSWorkspacePrivVO getWorkspaceMenuPrivs(String workspaceId) {
        DSSWorkspacePrivVO dssWorkspacePrivVO = new DSSWorkspacePrivVO();
        dssWorkspacePrivVO.setWorkspaceId(Integer.parseInt(workspaceId));
        List<DSSWorkspaceRoleVO> workspaceRoleVOList = workspaceDBHelper.getRoleVOs(Integer.parseInt(workspaceId));
        dssWorkspacePrivVO.setRoleVOS(workspaceRoleVOList);
        List<DSSWorkspaceMenuPrivVO> dssWorkspaceMenuPrivVOList = new ArrayList<>();
        List<DSSWorkspaceMenuRolePriv> dssWorkspaceMenuRolePrivList = dssWorkspaceMapper.getDSSWorkspaceMenuPriv(workspaceId);
        List<DSSWorkspaceMenuRolePriv> defaultWorkspaceMenuRolePrivList = dssWorkspaceMapper.getDefaultWorkspaceMenuPriv();
        for (DSSWorkspaceMenuRolePriv v : defaultWorkspaceMenuRolePrivList) {
            if (!dssWorkspaceMenuRolePrivList.contains(v)) {
                v.setPriv(0);
                dssWorkspaceMenuRolePrivList.add(v);
            }
        }
        Map<Integer, List<DSSWorkspaceMenuRolePriv>> map = new HashMap<>();
        for (DSSWorkspaceMenuRolePriv dssWorkspaceMenuPriv : dssWorkspaceMenuRolePrivList) {
            int menuId = dssWorkspaceMenuPriv.getMenuId();
            if (!map.containsKey(menuId)) {
                map.put(menuId, new ArrayList<>());
            }
            map.get(menuId).add(dssWorkspaceMenuPriv);
        }
        // 得到(menuId, dssWorkspaceMenuRolePrivs)

        map.forEach((k, v) -> {
            DSSWorkspaceMenuPrivVO vo = new DSSWorkspaceMenuPrivVO();
            vo.setId(k);
            if (workspaceDBHelper.getMenuNameById(k) != null) {
                vo.setName(workspaceDBHelper.getMenuNameById(k).getTitleCn());
                Map<String, Boolean> menuPrivs = new HashMap<>();
                workspaceRoleVOList.forEach(role -> {
                    int roleId = role.getRoleId();
                    boolean isContain = false;
                    for (DSSWorkspaceMenuRolePriv dssWorkspaceMenuRolePriv : v) {
                        if (roleId == dssWorkspaceMenuRolePriv.getRoleId()) {
                            menuPrivs.put(role.getRoleName(), dssWorkspaceMenuRolePriv.getPriv() == 1);
                            isContain = true;
                            break;
                        }
                    }
                    if (!isContain) {
                        menuPrivs.put(role.getRoleName(), false);
                    }

                });
                vo.setMenuPrivs(menuPrivs);
                dssWorkspaceMenuPrivVOList.add(vo);
            }

        });
        dssWorkspacePrivVO.setMenuPrivVOS(dssWorkspaceMenuPrivVOList);

        List<DSSWorkspaceComponentPrivVO> dssWorkspaceComponentPrivVOList = new ArrayList<>();
        List<DSSWorkspaceComponentRolePriv> dssWorkspaceComponentRolePrivList =
                dssWorkspaceMenuMapper.getComponentRolePriv(Integer.parseInt(workspaceId));
        List<DSSWorkspaceComponentRolePriv> defaultDssWorkspaceComponentRolePrivList = dssWorkspaceMenuMapper.getDefaultComponentRolePriv01();
        for (DSSWorkspaceComponentRolePriv p : defaultDssWorkspaceComponentRolePrivList) {
            if (!dssWorkspaceComponentRolePrivList.contains(p)) {
                p.setPriv(0);
                dssWorkspaceComponentRolePrivList.add(p);
            }
        }
        Map<Integer, List<DSSWorkspaceComponentRolePriv>> map1 = new HashMap<>();
        for (DSSWorkspaceComponentRolePriv dssWorkspaceComponentRolePriv : dssWorkspaceComponentRolePrivList) {
            Integer componentId = dssWorkspaceComponentRolePriv.getComponentId();
            if (componentId == null) {
                continue;
            }
            if (!map1.containsKey(componentId)) {
                List<DSSWorkspaceComponentRolePriv> tempList = new ArrayList<>();
                tempList.add(dssWorkspaceComponentRolePriv);
                map1.put(componentId, tempList);
            } else {
                map1.get(componentId).add(dssWorkspaceComponentRolePriv);
            }
        }
        map1.forEach((k, v) -> {
            DSSWorkspaceComponentPrivVO vo = new DSSWorkspaceComponentPrivVO();
            vo.setId(k);
            Map<String, Boolean> componentPrivs = new HashMap<>();

            if (workspaceDBHelper.getAppConn(k) != null) {
                vo.setName(workspaceDBHelper.getAppConn(k).getName());
                workspaceRoleVOList.forEach(role -> {
                    int roleId = role.getRoleId();
                    boolean isContain = false;
                    for (DSSWorkspaceComponentRolePriv dssWorkspaceComponentRolePriv : v) {
                        if (roleId == dssWorkspaceComponentRolePriv.getRoleId()) {
                            componentPrivs.put(role.getRoleName(), dssWorkspaceComponentRolePriv.getPriv() != null && dssWorkspaceComponentRolePriv.getPriv() == 1);
                            isContain = true;
                            break;
                        }
                    }
                    if (!isContain) {
                        componentPrivs.put(role.getRoleName(), false);
                    }

                });
                vo.setComponentPrivs(componentPrivs);
                dssWorkspaceComponentPrivVOList.add(vo);

            }

        });
        dssWorkspacePrivVO.setComponentPrivVOS(dssWorkspaceComponentPrivVOList);
        return dssWorkspacePrivVO;
    }

    @Override
    public DSSWorkspaceOverviewVO getOverview(String username, int workspaceId, boolean isEnglish) {
        DSSWorkspaceOverviewVO dssWorkspaceOverviewVO = new DSSWorkspaceOverviewVO();
        DSSWorkspace dssWorkspace = dssWorkspaceMapper.getWorkspace(workspaceId);
        dssWorkspaceOverviewVO.setTitle(dssWorkspace.getName());
        dssWorkspaceOverviewVO.setDescription(dssWorkspace.getDescription());
        return dssWorkspaceOverviewVO;
    }

    @Override
    public DSSWorkspaceHomepageSettingVO getWorkspaceHomepageSettings(int workspaceId) {
        DSSWorkspaceHomepageSettingVO dssWorkspaceHomepageSettingVO = new DSSWorkspaceHomepageSettingVO();

        List<DSSWorkspaceHomepageSetting> dssWorkspaceHomepageSettings = dssWorkspaceMenuMapper.getWorkspaceHomepageSettings(workspaceId);
        List<DSSWorkspaceHomepageSettingVO.RoleHomepage> roleHomepageList = new ArrayList<>();
        dssWorkspaceHomepageSettings.forEach(homepage -> {
            DSSWorkspaceHomepageSettingVO.RoleHomepage roleHomepage = new DSSWorkspaceHomepageSettingVO.RoleHomepage();
            roleHomepage.setHomepageName(workspaceDBHelper.getHomepageName(homepage.getHomepageUrl()));
            roleHomepage.setHomepageUrl(homepage.getHomepageUrl());
            roleHomepage.setRoleId(homepage.getRoleId());
            roleHomepage.setRoleName(workspaceDBHelper.getRoleNameById(homepage.getRoleId()));
            roleHomepage.setRoleFrontName(workspaceDBHelper.getRoleFrontName(homepage.getRoleId()));
            roleHomepageList.add(roleHomepage);
        });
        dssWorkspaceHomepageSettingVO.setRoleHomepages(roleHomepageList);
        return dssWorkspaceHomepageSettingVO;
    }

    @Override
    public String getWorkspaceName(Long workspaceId) {
        return dssWorkspaceMapper.getWorkspaceNameById(workspaceId);
    }
    @Override
    public int getWorkspaceId(String workspaceName) {
        Integer workspaceId= dssWorkspaceMapper.getWorkspaceIdByName(workspaceName);
        if (workspaceId == null || workspaceId < 0) {
            throw new DSSRuntimeException("workspace is not exist.workspaceName:" + workspaceName);
        }
        return workspaceId;
    }

    @Override
    public boolean checkAdmin(String userName) {
        return dssWorkspaceUserMapper.isAdmin(userName) == 1;
    }

    @Override
    public boolean checkAdminByWorkspace(String username, int workspaceId) {
        List<String> roles = dssWorkspaceRoleService.getRoleInWorkspace(username, workspaceId);
        return roles.stream().anyMatch(role -> role.equalsIgnoreCase("admin"));
    }

    @Override
    public List<String> getAllDepartmentWithOffices() {
        List<String> allDepartments = staffInfoGetter.getAllDepartments();
        return allDepartments;
    }

    @Override
    public List<String> getAllDepartments() {
        List<String> allDepartments = staffInfoGetter.getAllDepartments().stream()
                .map(department -> department.split(WorkspaceServerConstant.DEFAULT_STAFF_SPLIT)[0]).distinct().collect(Collectors.toList());
        return allDepartments;
    }

    @Override
    public void associateDepartments(Long workspaceId, String departments, String roles, String user) throws DSSErrorException {
        List<Integer> userRoles = dssWorkspaceUserMapper.getRoleInWorkspace(workspaceId.intValue(), user);
        //管理员鉴权
        if (userRoles.stream().noneMatch(l -> l == workspaceDBHelper.getRoleIdByName(CommonRoleEnum.ADMIN.getName()))) {
            throw new DSSErrorException(80000, "无权限操作");
        }
        if (dssWorkspaceMapper.getAssociateDepartmentsByWorkspaceId(workspaceId) != null) {
            dssWorkspaceMapper.updateDepartmentsForWorkspace(workspaceId, departments, roles, user);
        } else {
            dssWorkspaceMapper.addDepartmentsForWorkspace(workspaceId, departments, roles, user);
        }
    }

    @Override
    public DSSWorkspaceAssociateDepartments getAssociateDepartmentsInfo(Long workspaceId) {
        return dssWorkspaceMapper.getAssociateDepartmentsByWorkspaceId(workspaceId);
    }

    @Override
    public List<DSSWorkspaceUserVO> getWorkspaceUsersByRole(int workspaceId, String roleName, List<Long> totals, int pageNow, int pageSize) {
        int roleId = workspaceDBHelper.getRoleIdByName(roleName);
        PageHelper.startPage(pageNow, pageSize);
        List<DSSWorkspaceUser> workspaceUsers = new ArrayList<>();
        try {
            workspaceUsers = dssWorkspaceUserMapper.getWorkspaceUsersByRole(workspaceId, roleId);
        } finally {
            PageHelper.clearPage();
        }
        PageInfo<DSSWorkspaceUser> pageInfo = new PageInfo<>(workspaceUsers);
        totals.add(pageInfo.getTotal());
        List<DSSWorkspaceUserVO> dssWorkspaceUserVOs = new ArrayList<>();
        for (DSSWorkspaceUser workspaceUser : workspaceUsers) {
            List<Integer> roles = dssWorkspaceUserMapper.getRoleInWorkspace(workspaceId, workspaceUser.getUsername());
            dssWorkspaceUserVOs.add(changeToUserVO(workspaceUser, roles));
        }
        return dssWorkspaceUserVOs;
    }

    private DSSWorkspace getWorkspace(Supplier<DSSWorkspace> workspaceSupplier, String username) throws DSSErrorException {
        DSSWorkspace dssWorkSpace = workspaceSupplier.get();
        if (dssWorkSpace == null) {
            throw new DSSFrameworkWarnException(30022, "workspace is not exists.");
        }
        List<String> users = dssWorkspaceUserMapper.getAllWorkspaceUsers(dssWorkSpace.getId());
        if (!users.contains(username)) {
            throw new DSSFrameworkWarnException(30021, "user: " + username + " have no permission to access this workspace " + dssWorkSpace.getName());
        }
        String originDepartId = dssWorkSpace.getDepartment();
        if (StringUtils.isNotBlank(originDepartId)) {
            String departName = workspaceMapper.getDepartName(Long.valueOf(originDepartId));
            dssWorkSpace.setDepartment(departName);
        }
        return dssWorkSpace;
    }

    @Override
    public DSSWorkspace getWorkspacesById(Long id, String username) throws DSSErrorException {
        return getWorkspace(() -> workspaceMapper.getWorkspaceById(id), username);
    }

    @Override
    public DSSWorkspace getWorkspacesByName(String workspaceName, String username) throws DSSErrorException {
        return getWorkspace(() -> {
            List<DSSWorkspace> dssWorkspaces = workspaceMapper.findByWorkspaceName(workspaceName);
            if (dssWorkspaces == null || dssWorkspaces.isEmpty()) {
                return null;
            } else if (dssWorkspaces.size() > 1) {
                throw new DSSFrameworkWarnException(30021, "Too many workspaces named " + workspaceName);
            } else {
                return dssWorkspaces.get(0);
            }
        }, username);
    }

    @Override
    public Long addWorkspace(String userName, String name, String department, String label, String description) {
        DSSWorkspace dssWorkspace = new DSSWorkspace();
        dssWorkspace.setName(name);
        dssWorkspace.setDepartment(department);
        dssWorkspace.setDescription(description);
        dssWorkspace.setLabel(label);
        dssWorkspace.setCreateBy(userName);
        dssWorkspace.setSource("create by user");
        dssWorkspace.setLastUpdateUser(userName);
        workspaceMapper.addWorkSpace(dssWorkspace);
        return (long) dssWorkspace.getId();
    }

    @Override
    public boolean existWorkspaceName(String name) {
        return !workspaceMapper.findByWorkspaceName(name).isEmpty();
    }

    @Override
    public List<WorkspaceDepartmentVo> getWorkSpaceDepartments() {
        // TODO: service层和dao层完善
        WorkspaceDepartmentVo dp = new WorkspaceDepartmentVo();
        dp.setId(1L);
        dp.setName("应用开发组");
        WorkspaceDepartmentVo di = new WorkspaceDepartmentVo();
        di.setId(2L);
        di.setName("平台研发组");
        List<WorkspaceDepartmentVo> departments = new ArrayList<>();
        departments.add(dp);
        departments.add(di);
        return departments;
    }

    private List<WorkspaceMenuVo> getMenuAppInstances(List<WorkspaceMenuVo> menuVos, List<Long> userMenuAppConnIds,
                                                      DSSWorkspace dssworkspace, Workspace workspace,
                                                      boolean isChinese) {
        for (WorkspaceMenuVo menuVo : menuVos) {
            Long menuId = menuVo.getId();
            List<WorkspaceMenuAppconnVo> menuAppconns = isChinese ? workspaceMapper.getMenuAppInstancesCn(menuId) : workspaceMapper.getMenuAppInstancesEn(menuId);
            for (WorkspaceMenuAppconnVo menuAppconn : menuAppconns) {
                // 如果该工作空间中用户拥有该组件权限，则该组件的accessable属性为true；否则为false
                menuAppconn.setAccessable(userMenuAppConnIds.contains(menuAppconn.getId()));
                AppConn appConn = AppConnManager.getAppConnManager().getAppConn(menuAppconn.getName());
                List<DSSApplicationBean> instanceList = new ArrayList<>();
                SSOUrlBuilderOperation operation;
                if (appConn instanceof OnlySSOAppConn) {
                    operation = ((OnlySSOAppConn) appConn).getOrCreateSSOStandard().getSSOBuilderService().createSSOUrlBuilderOperation();
                    SSOHelper.setSSOUrlBuilderOperation(operation, workspace);
                    operation.setAppName(appConn.getAppDesc().getAppName());
                } else {
                    operation = null;
                }
                appConn.getAppDesc().getAppInstances().forEach(appInstance -> {
                    String label = String.join(",", appInstance.getLabels().stream()
                            .map(l -> ((EnvDSSLabel) l).getEnv()).toArray(String[]::new));
                    String selectedName = getAppInstanceTitle(appConn, appInstance, isChinese);
                    String homepageUri = AppInstanceConstants.getHomepageUrl(appInstance, operation, (long) dssworkspace.getId(), dssworkspace.getName());
                    instanceList.add(new DSSApplicationBean(selectedName, appInstance.getBaseUrl(),
                            homepageUri, label));
                });
                menuAppconn.setAppInstances(instanceList);
            }
            menuVo.setAppconns(menuAppconns);
        }
        return menuVos;
    }

    protected String getAppInstanceTitle(AppConn appConn, AppInstance appInstance, boolean isChinese) {
        if (isChinese) {
            return "进入 " + appConn.getAppDesc().getAppName();
        } else {
            return "Enter " + appConn.getAppDesc().getAppName();
        }
    }

    @Override
    public List<WorkspaceMenuVo> getWorkspaceAppConns(Workspace workspace, Long workspaceId, String username,
                                                      boolean isChinese) throws DSSErrorException {
        DSSWorkspace dssWorkspace = getWorkspacesById(workspaceId, username);
        List<WorkspaceMenuVo> appconnMenuVos = isChinese ? workspaceMapper.getAppConnMenuCn() : workspaceMapper.getAppConnMenuEn();
        List<Long> userMenuAppConnIds = dssWorkspaceMapper.getUserMenuAppConnId(username, workspaceId);
        return getMenuAppInstances(appconnMenuVos, userMenuAppConnIds, dssWorkspace, workspace, isChinese);
    }

    @Override
    public List<WorkspaceFavoriteVo> getWorkspaceFavorites(Long workspaceId, String username, boolean isChinese, String type) {
        checkScriptis(workspaceId, username, "dingyiding");
        return isChinese ? workspaceMapper.getWorkspaceFavoritesCn(username, workspaceId, type) : workspaceMapper.getWorkspaceFavoritesEn(username, workspaceId, type);
    }

    /**
     * 保证Scriptis入口一直在页面上方
     *
     * @param workspaceId
     * @param username
     * @param type
     */
    private void checkScriptis(Long workspaceId, String username, String type) {
        Long scriptisMenuAppId = dssWorkspaceMapper.getMenuAppIdByName("Scriptis");
        int exists = dssWorkspaceMapper.getByMenuAppIdAndUser(scriptisMenuAppId, workspaceId, username, type);
        if (exists < 1) {
            addFavorite(username, workspaceId, scriptisMenuAppId, type);
        }
    }

    @Override
    public Long addFavorite(String username, Long workspaceId, Long menuApplicationId, String type) {
        DSSFavorite dssFavorite = new DSSFavorite();
        dssFavorite.setUsername(username);
        dssFavorite.setWorkspaceId(workspaceId);
        dssFavorite.setMenuAppConnId(menuApplicationId);
        // todo: order will from the front end
        dssFavorite.setOrder(1);
        dssFavorite.setCreateBy(username);
        dssFavorite.setLastUpdateUser(username);
        dssFavorite.setType(type);
        workspaceMapper.addFavorite(dssFavorite);
        return dssFavorite.getId();
    }

    @Override
    public Long deleteFavorite(String username, Long appconnId, Long workspaceId, String type) {
        workspaceMapper.deleteFavorite(username, appconnId, workspaceId, type);
        return appconnId;
    }


    /**
     * 是否超级管理员
     *
     * @param workspaceId
     * @param username
     * @return
     */
    @Override
    public boolean isAdminUser(Long workspaceId, String username) {
        DSSWorkspace workspace = workspaceMapper.getWorkspaceById(workspaceId);
        List<Integer> roles = dssWorkspaceUserMapper.getRoleInWorkspace(workspaceId.intValue(), username);
        if (roles != null && roles.size() > 0) {
            for (Integer role : roles) {
                if (role == 1) {
                    return true;
                }
            }
        }
        //默认空间配置的超级管理员，返回true
        return (workspace.getName().equals(DSSWorkspaceConstant.DEFAULT_WORKSPACE_NAME.getValue()) &&
                org.apache.commons.lang3.ArrayUtils.contains(AdminConf.SUPER_ADMIN_LIST, username)) ||
                username.equals(workspace.getCreateBy());
    }

    @Override
    public PageInfo<DSSUserRoleComponentPriv> getAllUserPrivs(Integer currentPage, Integer pageSize) {
        PageMethod.startPage(currentPage, pageSize);
        List<DSSUserRoleComponentPriv> users = dssWorkspaceUserMapper.getAllUsers();
        Map<String, DSSUserRoleComponentPriv> userRolePrivMap = dssWorkspaceUserMapper.getWorkspaceRolePrivByUsername(users);
        Set<String> allUsernames = staffInfoGetter.getAllUsernames();
        users = users.stream().filter(user->allUsernames.contains(user.getUserName())).collect(Collectors.toList());
        //处理用户没有角色权限时只返回用户
        for (DSSUserRoleComponentPriv user : users) {
            if (MapUtils.isEmpty(userRolePrivMap) || Objects.isNull(userRolePrivMap.get(user.getUserName()))) {
                continue;
            }
            user.setRoles(userRolePrivMap.get(user.getUserName()).getRoles());
        }
        return new PageInfo<>(users);
    }


    private void joinWorkspaceForNewUser(String userName, Long userId, int workspaceId, Integer workspace0xId, Integer analyserRole) {
        String userOrgName = staffInfoGetter.getFullOrgNameByUsername(userName);
        String orgName = userOrgName.split("-")[0];
        List<DSSWorkspaceAssociateDepartments> workspaceAssociateDepartments = dssWorkspaceMapper.getWorkspaceAssociateDepartments();
        Set<Map<Long, String>> needToAdd = new HashSet<>();
        for (DSSWorkspaceAssociateDepartments item : workspaceAssociateDepartments) {
            String departments = item.getDepartments();
            if (StringUtils.isNotBlank(departments) && StringUtils.isNotBlank(item.getRoleIds())) {
                Arrays.stream(departments.split(",")).forEach(org -> {
                    if (org.equals(userOrgName) || orgName.equals(org)) {
                        Map<Long, String> pair = new HashMap<>();
                        pair.put(item.getWorkspaceId(), item.getRoleIds());
                        needToAdd.add(pair);
                    }
                });
            }
        }
        needToAdd.forEach(map -> {
            map.forEach((key, value) -> {
                if (key.intValue() == workspaceId || (workspace0xId != null && key.intValue() == workspace0xId)) {
                    Arrays.stream(value.split(",")).filter(roleId -> analyserRole == null || Integer.parseInt(roleId) != analyserRole).forEach(roleId -> {
                        dssWorkspaceUserMapper.insertUserRoleInWorkspace(key.intValue(), Integer.parseInt(roleId), new Date(), userName, "system", userId, "system");
                    });
                } else {
                    Arrays.stream(value.split(",")).forEach(roleId -> {
                        dssWorkspaceUserMapper.insertUserRoleInWorkspace(key.intValue(), Integer.parseInt(roleId), new Date(), userName, "system", userId, "system");
                    });
                }
            });
        });
    }


    @Override
    public void updateWorkspaceInfo(DSSWorkspace dssWorkspace){
        dssWorkspaceMapper.updateWorkspaceInfo(dssWorkspace);
    }

    @Override
    public List<DSSWorkspaceStarRocksCluster> updateStarRocksCluster(List<UpdateWorkspaceStarRocksClusterRequest> request, String ticketId, Workspace workspace, String userName) {

        Long workspaceId = request.get(0).getWorkspaceId();
        List<DSSWorkspaceStarRocksCluster> starRocksClusters = new ArrayList<>();
        Set<String> clusterNames = new HashSet<>();
        int count = 0;

        List<DSSWorkspaceStarRocksCluster> itemsByWorkspaceId = dssWorkspaceStarRocksClusterMapper.getItemsByWorkspaceId(workspaceId);
        Map<String, DSSWorkspaceStarRocksCluster> existsClusters = new HashMap<>();
        if(CollectionUtils.isNotEmpty(itemsByWorkspaceId)){
            existsClusters = itemsByWorkspaceId.stream()
                    .collect(Collectors.toMap(DSSWorkspaceStarRocksCluster::getClusterName, cluster -> cluster));
        }

        for (UpdateWorkspaceStarRocksClusterRequest r : request) {

            if (!clusterNames.add(r.getClusterName())) {
                throw new DSSErrorException(90054, String.format("Two identical names %s are not allowed in workspace %s", r.getClusterName(), r.getWorkspaceName()));
            }

            if (StringUtils.isEmpty(r.getClusterName()) || StringUtils.isEmpty(r.getClusterIp())) {
                LOGGER.warn("工作空间{} StarRocks集群名和ip不能为空", r.getWorkspaceName());
                throw new DSSErrorException(90054, String.format("%s workspace StarRocks cluster name and ip cannot be empty.", r.getWorkspaceName()));
            }

            if (StringUtils.length(r.getClusterName()) > 256) {
                LOGGER.warn("工作空间{}StarRocks集群名称{}超过256字符", r.getWorkspaceName(), r.getClusterName());
                throw new DSSErrorException(90054, String.format("StarRocks cluster %s length cannot exceed 256 in workspace %s", r.getClusterName(), r.getWorkspaceName()));
            }

            // 已经存在的集群 不允许更改IP 和端口
            if(existsClusters.containsKey(r.getClusterName())){
                DSSWorkspaceStarRocksCluster dssWorkspaceStarRocksCluster = existsClusters.get(r.getClusterName());

                if(ObjectUtils.notEqual(dssWorkspaceStarRocksCluster.getClusterIp(),r.getClusterIp())){
                    LOGGER.warn("{} workspace, {} StarRocks cluster ip cannot be updated, ip is [{},{}]",r.getWorkspaceName(),r.getClusterName(),
                            r.getClusterIp(),dssWorkspaceStarRocksCluster.getClusterIp());
                    throw new DSSErrorException(90054, String.format("%s workspace, %s StarRocks cluster ip cannot be updated", r.getWorkspaceName(),r.getClusterName()));
                }

                if(ObjectUtils.notEqual(dssWorkspaceStarRocksCluster.getHttpPort(),r.getHttpPort())
                        || ObjectUtils.notEqual(dssWorkspaceStarRocksCluster.getTcpPort(),r.getTcpPort())
                ){
                    LOGGER.warn("{} workspace, {} StarRocks cluster port cannot be updated",r.getWorkspaceName(),r.getClusterName());
                    LOGGER.warn("tcp port is [{},{}],http port is [{},{}]",r.getTcpPort(),dssWorkspaceStarRocksCluster.getTcpPort(),
                            r.getHttpPort(),dssWorkspaceStarRocksCluster.getHttpPort());
                    throw new DSSErrorException(90054, String.format("%s workspace, %s StarRocks cluster port cannot be updated", r.getWorkspaceName(),r.getClusterName()));
                }
            }


            if (Integer.parseInt(r.getHttpPort()) < 0 || Integer.parseInt(r.getHttpPort()) > 65535 || Integer.parseInt(r.getTcpPort()) < 0 || Integer.parseInt(r.getTcpPort()) > 65535) {
                LOGGER.warn("工作空间{}StarRocks集群端口必须在0-65535之间", r.getWorkspaceName());
                throw new DSSErrorException(90054, String.format("%s workspace StarRocks cluster port must be between 0 and 65535", r.getWorkspaceName()));
            }

            if (!IPV4_PATTERN.matcher(r.getClusterIp()).matches()) {
                LOGGER.warn("工作空间{}StarRocks集群ip格式不准确", r.getWorkspaceName());
                throw new DSSErrorException(90054, String.format("%s workspace StarRocks cluster ip is illegal", r.getWorkspaceName()));
            }

            if (r.getDefaultCluster()) {
                count++;
            }
            if (count > 1) {
                LOGGER.warn("工作空间{}不允许设置两个默认集群", r.getWorkspaceName());
                throw new DSSErrorException(90054, String.format("%s workspace cannot set multiple default StarRocks cluster!", r.getWorkspaceName()) );
            }
        }

        List<StarRocksNodeInfo> starRocksNodeInfos = dssFlowService.queryStarRocksNodeList(workspaceId);
        Set<String> executeClusterSet = starRocksNodeInfos.stream().map(StarRocksNodeInfo::getNodeUiValue).collect(Collectors.toSet());

        Map<String, UpdateWorkspaceStarRocksClusterRequest> requestCluster = request.stream().
                collect(Collectors.toMap(UpdateWorkspaceStarRocksClusterRequest::getClusterName, r -> r));

        for (DSSWorkspaceStarRocksCluster item : itemsByWorkspaceId) {
            UpdateWorkspaceStarRocksClusterRequest requestOne = requestCluster.get(item.getClusterName());
            if (requestOne == null) {
                //请求中没有该集群，删除前需要校验有没有被引用，没有引用才允许删除
                if (executeClusterSet.contains(item.getClusterName())) {
                    throw new DSSErrorException(90054, String.format("集群 %s 在工作流节点中被引用，不允许删除", item.getClusterName()) );
                } else {
                    dssWorkspaceStarRocksClusterMapper.deleteItemById(item.getId());
                }
            } else {

                item.setDefaultCluster(requestOne.getDefaultCluster());
                item.setUpdateUser(requestOne.getUsername());
                item.setUpdateTime(new Date());
                LOGGER.info("更新集群{}的信息为{}", item.getClusterName(), item);
                dssWorkspaceStarRocksClusterMapper.updateStarRocksCluster(item);
                starRocksClusters.add(item);
            }
        }

        for (UpdateWorkspaceStarRocksClusterRequest r : request) {
            DSSWorkspaceStarRocksCluster existsOne = existsClusters.get(r.getClusterName());
            //如果数据库不存在，就需要添加进去
            if (existsOne == null) {
                LOGGER.info("新增集群{}", r.getClusterName());
                DSSWorkspaceStarRocksCluster dssWorkspaceStarRocksCluster = new DSSWorkspaceStarRocksCluster(r.getWorkspaceId(),
                        workspace.getWorkspaceName(), r.getClusterName(), r.getClusterIp(), r.getHttpPort(), r.getTcpPort(),
                        r.getDefaultCluster(), r.getUsername(), r.getUsername(), new Date(), new Date());
                dssWorkspaceStarRocksClusterMapper.insertStarRocksCluster(dssWorkspaceStarRocksCluster);
                starRocksClusters.add(dssWorkspaceStarRocksCluster);
            }
        }
        return starRocksClusters;
    }

    @Override
    public List<DSSWorkspaceStarRocksCluster> getStarRocksCluster(Long workspaceId) {
        return dssWorkspaceStarRocksClusterMapper.getItemsByWorkspaceId(workspaceId);
    }

    @Override
    public void deleteStarRocksClusterByWorkspaceId(Long workspaceId) {
        List<StarRocksNodeInfo> starRocksNodeInfos = dssFlowService.queryStarRocksNodeList(workspaceId);
        Set<String> executeClusterSet = starRocksNodeInfos.stream().filter(s -> "executeCluster".equals(s.getNodeUiKey())).map(StarRocksNodeInfo::getNodeUiValue).collect(Collectors.toSet());
        List<DSSWorkspaceStarRocksCluster> itemsByWorkspaceId = dssWorkspaceStarRocksClusterMapper.getItemsByWorkspaceId(workspaceId);
        for (DSSWorkspaceStarRocksCluster item : itemsByWorkspaceId) {
            if (executeClusterSet.contains(item.getClusterName())) {
                throw new DSSErrorException(90054, String.format("集群 %s 在工作流节点中被引用，不允许删除", item.getClusterName()) );
            } else {
                dssWorkspaceStarRocksClusterMapper.deleteItemByWorkspaceId(workspaceId);
            }
        }
    }

    @Override
    public List<DSSStarRocksCluster> getDSSStarrocksCluster(Long workspaceId) {
        List<DSSStarRocksCluster> starrocksClusterList = new ArrayList<>();

        List<DSSWorkspaceStarRocksCluster> dssWorkspaceStarRocksClusterList = getStarRocksCluster(workspaceId);

        for(DSSWorkspaceStarRocksCluster dssWorkspaceStarRocksCluster: dssWorkspaceStarRocksClusterList){
            DSSStarRocksCluster dssStarrocksCluster = new DSSStarRocksCluster();
            BeanUtils.copyProperties(dssWorkspaceStarRocksCluster,dssStarrocksCluster);
            starrocksClusterList.add(dssStarrocksCluster);

        }

        return starrocksClusterList;
    }

    private BatchEditFlowRequest getEditFlowRequest(List<StarRocksNodeInfo> updateNodes, DSSWorkspaceStarRocksCluster oneByClusterName) throws JsonProcessingException {
        Map<String, Map<String, String>> nodeKeyToUiMap = new HashMap<>();
        for (StarRocksNodeInfo updateNode : updateNodes) {
            String nodeKey = updateNode.getNodeKey() + "&" + updateNode.getNodeContentId() + "&" + updateNode.getOrchestratorId();
            String nodeUiKey = updateNode.getNodeUiKey();
            String nodeUiValue = updateNode.getNodeUiValue();
            nodeKeyToUiMap.computeIfAbsent(nodeKey, k -> new HashMap<>())
                    .put(nodeUiKey, nodeUiValue);
        }
        List<EditFlowRequest> editFlowRequestList = new ArrayList<>();
        for (Map.Entry<String, Map<String, String>> outerEntry : nodeKeyToUiMap.entrySet()) {
            String nodeKey = outerEntry.getKey();
            Map<String, String> innerMap = outerEntry.getValue();
            EditFlowRequest editFlowRequest = new EditFlowRequest();
            String[] split = nodeKey.split("&");
            editFlowRequest.setNodeKey(split[0]);
            editFlowRequest.setId(Long.parseLong(split[1]));
            editFlowRequest.setOrchestratorId(Long.parseLong(split[2]));
            String reuseEngine = innerMap.getOrDefault("ReuseEngine", null);
            String autoDisabled = innerMap.getOrDefault("auto.disable", null);
            String params = constructParams(reuseEngine, autoDisabled, oneByClusterName.getClusterName(),
                    oneByClusterName.getClusterIp(), oneByClusterName.getTcpPort());
            editFlowRequest.setParams(params);
            editFlowRequestList.add(editFlowRequest);
        }

        BatchEditFlowRequest batchEditFlowRequest = new BatchEditFlowRequest();
        batchEditFlowRequest.setEditNodeList(editFlowRequestList);


        return batchEditFlowRequest;
    }

    private String constructParams(String reuseEngine, String autoDisabled, String executeCluster, String host, String port) throws JsonProcessingException {
        // 使用 Jackson 构造 JSON
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode configuration = mapper.createObjectNode();

        // special
        ObjectNode special = mapper.createObjectNode();
        if (autoDisabled != null) {
            special.put("auto.disabled", autoDisabled);
        } else {
            special.putNull("auto.disabled");
        }

        // runtime
        ObjectNode runtime = mapper.createObjectNode();
        if (executeCluster != null) {
            runtime.put("executeCluster", executeCluster);
        } else {
            runtime.putNull("executeCluster");
        }
        runtime.put("linkis.datasource.type", "starrocks");
        runtime.put("linkis.datasource.params.host", host);
        runtime.put("linkis.datasource.params.port", port);

        // startup
        ObjectNode startup = mapper.createObjectNode();
        startup.put("ReuseEngine", reuseEngine);

        // 总的configuration
        configuration.set("special", special);
        configuration.set("runtime", runtime);
        configuration.set("startup", startup);
        ObjectNode rootNode = mapper.createObjectNode();
        rootNode.set("configuration", configuration);

        // 转成String
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);

    }
}
