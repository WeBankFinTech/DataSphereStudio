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

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import com.webank.wedatasphere.dss.appconn.core.AppConn;
import com.webank.wedatasphere.dss.appconn.core.ext.OnlySSOAppConn;
import com.webank.wedatasphere.dss.appconn.manager.AppConnManager;
import com.webank.wedatasphere.dss.appconn.manager.utils.AppInstanceConstants;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.common.label.EnvDSSLabel;
import com.webank.wedatasphere.dss.framework.admin.conf.AdminConf;
import com.webank.wedatasphere.dss.framework.admin.service.DssAdminUserService;
import com.webank.wedatasphere.dss.framework.common.exception.DSSFrameworkWarnException;
import com.webank.wedatasphere.dss.framework.workspace.bean.*;
import com.webank.wedatasphere.dss.framework.workspace.bean.dto.response.WorkspaceDepartmentVo;
import com.webank.wedatasphere.dss.framework.workspace.bean.dto.response.WorkspaceFavoriteVo;
import com.webank.wedatasphere.dss.framework.workspace.bean.dto.response.WorkspaceMenuAppconnVo;
import com.webank.wedatasphere.dss.framework.workspace.bean.dto.response.WorkspaceMenuVo;
import com.webank.wedatasphere.dss.framework.workspace.bean.vo.*;
import com.webank.wedatasphere.dss.framework.workspace.constant.ApplicationConf;
import com.webank.wedatasphere.dss.framework.workspace.dao.*;
import com.webank.wedatasphere.dss.framework.workspace.exception.DSSWorkspaceDuplicateNameException;
import com.webank.wedatasphere.dss.framework.workspace.service.DSSWorkspaceRoleService;
import com.webank.wedatasphere.dss.framework.workspace.service.DSSWorkspaceService;
import com.webank.wedatasphere.dss.framework.workspace.service.DSSWorkspaceUserService;
import com.webank.wedatasphere.dss.framework.workspace.service.StaffInfoGetter;
import com.webank.wedatasphere.dss.framework.workspace.util.CommonRoleEnum;
import com.webank.wedatasphere.dss.framework.workspace.util.DSSWorkspaceConstant;
import com.webank.wedatasphere.dss.framework.workspace.util.WorkspaceDBHelper;
import com.webank.wedatasphere.dss.framework.workspace.util.WorkspaceServerConstant;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.app.sso.builder.SSOUrlBuilderOperation;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;
import com.webank.wedatasphere.dss.standard.sso.utils.SSOHelper;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.linkis.common.exception.ErrorException;
import org.apache.linkis.protocol.util.ImmutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.webank.wedatasphere.dss.framework.workspace.util.DSSWorkspaceConstant.DEFAULT_DEMO_WORKSPACE_NAME;

public class DSSWorkspaceServiceImpl implements DSSWorkspaceService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DSSWorkspaceServiceImpl.class);

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
                workspaceDBHelper.getRoleIdByName(CommonRoleEnum.ADMIN.getName()),new Date(), userName, userName, userId, userName);
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


    //获取所有的工作空间
    @Override
    public List<DSSWorkspace> getWorkspaces(String userName) {
        List<DSSWorkspace> workspaces = dssWorkspaceMapper.getWorkspaces(userName);
        //用于展示demo的工作空间是不应该返回的,除非用户是管理员
        if (dssWorkspaceUserMapper.isAdmin(userName) == 1) {
            return workspaces;
        } else {
            //踢掉那个演示demo工作空间
            List<DSSWorkspace> retWorkspaces = new ArrayList<>();
            String[] defaultDemoWorkspaceNames = DEFAULT_DEMO_WORKSPACE_NAME.getValue().split(",");
            for (DSSWorkspace workspace : workspaces) {
                if (!ArrayUtils.contains(defaultDemoWorkspaceNames, workspace.getName())) {
                    retWorkspaces.add(workspace);
                }
            }
            return retWorkspaces;
        }
    }

    @Override
    public DSSWorkspaceHomePageVO getWorkspaceHomePage(String userName, String moduleName) throws DSSErrorException {
        List<DSSWorkspace> dssWorkspaces = dssWorkspaceMapper.getWorkspaces(userName);
        List<Integer> workspaceIds = dssWorkspaces.stream().filter(l -> !DEFAULT_DEMO_WORKSPACE_NAME.getValue().equals(l.getName()))
                .map(DSSWorkspace::getId).collect(Collectors.toList());
        DSSWorkspaceHomePageVO dssWorkspaceHomePageVO = new DSSWorkspaceHomePageVO();
        if (workspaceIds.size() == 0) {
            Long userId = dssWorkspaceUserMapper.getUserID(userName);
            int workspaceId = dssWorkspaceMapper.getWorkspaceIdByName(DSSWorkspaceConstant.DEFAULT_WORKSPACE_NAME.getValue());
            Integer analyserRole = workspaceDBHelper.getRoleIdByName(CommonRoleEnum.ANALYSER.getName());

            dssWorkspaceUserMapper.insertUserRoleInWorkspace(workspaceId,analyserRole ,new Date(),
                    userName, "system", userId, "system");
            Integer workspace0xId = dssWorkspaceMapper.getWorkspaceIdByName(DSSWorkspaceConstant.DEFAULT_0XWORKSPACE_NAME.getValue());
            if (workspace0xId != null) {
                dssWorkspaceUserMapper.insertUserRoleInWorkspace(workspace0xId, analyserRole ,new Date(),
                        userName, "system", userId, "system");
            }
            //todo 初始化做的各项事情改为listener模式
            //为新用户自动加入部门关联的工作空间
            joinWorkspaceForNewUser(userName, userId);

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
            }else if("/workspaceHome/scheduleCenter".equals(homepageUrl)){
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
            String homepageUrl = "/workspaceHome?workspaceId=" + workspaceIds.get(0);
            if (ApplicationConf.HOMEPAGE_MODULE_NAME.getValue().equalsIgnoreCase(moduleName)) {
                homepageUrl = ApplicationConf.HOMEPAGE_URL.getValue() + workspaceIds.get(0);
            }
            dssWorkspaceHomePageVO.setWorkspaceId(workspaceIds.get(0));
            dssWorkspaceHomePageVO.setHomePageUrl(homepageUrl);
        }
        return dssWorkspaceHomePageVO;
    }

    @Override
    public List<DSSWorkspaceUserVO> getWorkspaceUsers(String workspaceId, String department, String username,
                                                      String roleName, int pageNow, int pageSize, List<Long> total) {
        String roleId = StringUtils.isBlank(roleName) ? null : String.valueOf(workspaceDBHelper.getRoleIdByName(roleName));
        PageHelper.startPage(pageNow, pageSize);
        // 转译用户名模糊查询中的特殊字符
        String queryName = username.contains("_") ? username.split("_")[0] + "\\" + username.split("_")[1] : username;
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
                String departmentName = orgFullName.split(WorkspaceServerConstant.DEFAULT_STAFF_SPLIT)[0];
                String officeName = orgFullName.split(WorkspaceServerConstant.DEFAULT_STAFF_SPLIT)[1];
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
        PageMethod.startPage(currentPage,pageSize);
        List<DSSUserRoleComponentPriv> users = dssWorkspaceUserMapper.getAllUsers();
        Map<String, DSSUserRoleComponentPriv> userRolePrivMap = dssWorkspaceUserMapper.getWorkspaceRolePrivByUsername(users);
        //处理用户没有角色权限时只返回用户
        for (DSSUserRoleComponentPriv user : users) {
            if (MapUtils.isEmpty(userRolePrivMap) || Objects.isNull(userRolePrivMap.get(user.getUserName()))) {
                continue;
            }
            user.setRoles(userRolePrivMap.get(user.getUserName()).getRoles());
        }
        return new PageInfo<>(users);
    }

    private void joinWorkspaceForNewUser(String userName, Long userId) {
        String userOrgName = staffInfoGetter.getFullOrgNameByUsername(userName);
        String orgName = userOrgName.split("-")[0];
        List<DSSWorkspaceAssociateDepartments> workspaceAssociateDepartments = dssWorkspaceMapper.getWorkspaceAssociateDepartments();
        Set<ImmutablePair<Long, String>> needToAdd = new HashSet<>();
        for (DSSWorkspaceAssociateDepartments item : workspaceAssociateDepartments) {
            String departments = item.getDepartments();
            if (StringUtils.isNotBlank(departments) && StringUtils.isNotBlank(item.getRoleIds())) {
                Arrays.stream(departments.split(",")).forEach(org -> {
                    if (org.equals(userOrgName) || orgName.equals(org)) {
                        needToAdd.add(new ImmutablePair<>(item.getWorkspaceId(), item.getRoleIds()));
                    }
                });
            }
        }
        needToAdd.forEach(pair -> {
            Arrays.stream(pair.getValue().split(",")).forEach(roleId -> {
                dssWorkspaceUserMapper.insertUserRoleInWorkspace(pair.getKey().intValue(), Integer.parseInt(roleId),new Date(), userName, "system", userId, "system");
            });
        });
    }
}
