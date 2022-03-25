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
import com.webank.wedatasphere.dss.appconn.core.AppConn;
import com.webank.wedatasphere.dss.appconn.manager.AppConnManager;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.framework.workspace.bean.*;
import com.webank.wedatasphere.dss.framework.workspace.bean.dto.response.OnestopMenuAppInstanceVo;
import com.webank.wedatasphere.dss.framework.workspace.bean.dto.response.OnestopMenuVo;
import com.webank.wedatasphere.dss.framework.workspace.bean.dto.response.WorkspaceDepartmentVo;
import com.webank.wedatasphere.dss.framework.workspace.bean.dto.response.WorkspaceFavoriteVo;
import com.webank.wedatasphere.dss.framework.workspace.bean.vo.*;
import com.webank.wedatasphere.dss.framework.workspace.constant.ApplicationConf;
import com.webank.wedatasphere.dss.framework.workspace.dao.DSSComponentRoleMapper;
import com.webank.wedatasphere.dss.framework.workspace.dao.DSSMenuRoleMapper;
import com.webank.wedatasphere.dss.framework.workspace.dao.DSSWorkspaceHomepageMapper;
import com.webank.wedatasphere.dss.framework.workspace.dao.DSSWorkspaceInfoMapper;
import com.webank.wedatasphere.dss.framework.workspace.dao.DSSWorkspaceMapper;
import com.webank.wedatasphere.dss.framework.workspace.dao.DSSWorkspaceMenuMapper;
import com.webank.wedatasphere.dss.framework.workspace.dao.DSSWorkspaceUserMapper;
import com.webank.wedatasphere.dss.framework.workspace.dao.WorkspaceMapper;
import com.webank.wedatasphere.dss.framework.workspace.exception.DSSWorkspaceDuplicateNameException;
import com.webank.wedatasphere.dss.framework.workspace.service.*;
import com.webank.wedatasphere.dss.framework.workspace.util.CommonRoleEnum;
import com.webank.wedatasphere.dss.framework.workspace.util.DSSWorkspaceConstant;
import com.webank.wedatasphere.dss.framework.workspace.util.WorkspaceDBHelper;
import com.webank.wedatasphere.dss.framework.workspace.util.WorkspaceServerConstant;
import org.apache.commons.lang.ArrayUtils;
import org.apache.linkis.common.exception.ErrorException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.webank.wedatasphere.dss.framework.workspace.util.DSSWorkspaceConstant.DEFAULT_DEMO_WORKSPACE_NAME;

@Service
public class DSSWorkspaceServiceImpl implements DSSWorkspaceService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DSSWorkspaceServiceImpl.class);

    @Autowired
    private DSSWorkspaceMapper dssWorkspaceMapper;
    @Autowired
    private DSSWorkspaceUserMapper dssWorkspaceUserMapper;
    @Autowired
    private DSSWorkspaceMenuMapper dssWorkspaceMenuMapper;
    @Autowired
    private DSSWorkspaceInfoMapper dssWorkspaceInfoMapper;
    @Autowired
    private WorkspaceDBHelper workspaceDBHelper;
    @Autowired
    private DSSWorkspaceService dssWorkspaceService;
    @Autowired
    private DSSWorkspaceUserService dssWorkspaceUserService;
    @Autowired
    private DSSUserService dssUserService;
    @Autowired
    private DSSMenuRoleMapper dssMenuRoleMapper;
    @Autowired
    private DSSWorkspaceHomepageMapper dssWorkspaceHomepageMapper;
    @Autowired
    private DSSComponentRoleMapper dssComponentRoleMapper;
    @Autowired
    private DSSWorkspaceMenuService dssWorkspaceMenuService;
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
                               String description, String department, String productName,String workspaceType) throws ErrorException {
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
        try{
            dssWorkspaceMapper.createWorkSpace(dssWorkspace);
        }catch(Exception e){
            DSSWorkspaceDuplicateNameException exception1 = new DSSWorkspaceDuplicateNameException(50010, "工作空间名重复");
            exception1.initCause(e);
            throw exception1;
        }
        String userId = String.valueOf(dssWorkspaceUserMapper.getUserID(userName));
        dssWorkspaceUserMapper.setUserRoleInWorkspace(dssWorkspace.getId(), CommonRoleEnum.ADMIN.getId(), userName, userName,userId);
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


    //把用户及角色添加到工作空间
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addWorkspaceUser(List<Integer> roleIds, int workspaceId, String userName, String creator,String userId) {
        //根据用户名 从用户表拿到用户id
//        Long userId = dssUserService.getUserID(userName);
        if(userId == null){
            //保存 - dss_user
            dssUserService.saveWorkspaceUser(userName);
        }
        //保存 - 保存用户角色关系 dss_workspace_user_role
        for (Integer roleId : roleIds) {
            dssWorkspaceUserMapper.setUserRoleInWorkspace(workspaceId, roleId, userName, creator,userId);
        }
    }

    //获取所有的工作空间
    @Override
    public List<DSSWorkspace> getWorkspaces(String userName){
        List<DSSWorkspace> workspaces = dssWorkspaceMapper.getWorkspaces(userName);
        //用于展示demo的工作空间是不应该返回的,除非用户是管理员
        if(dssWorkspaceUserMapper.isAdmin(userName)) {
            return workspaces;
        }else{
            //踢掉那个演示demo工作空间
            List<DSSWorkspace> retWorkspaces = new ArrayList<>();
            String[] defaultDemoWorkspaceNames = DEFAULT_DEMO_WORKSPACE_NAME.getValue().split(",");
            for (DSSWorkspace workspace : workspaces) {
                if (!ArrayUtils.contains(defaultDemoWorkspaceNames, workspace.getName())){
                    retWorkspaces.add(workspace);
                }
            }
            return retWorkspaces;
        }
    }

    @Override
    public DSSWorkspaceHomePageVO getWorkspaceHomePage(String userName,String moduleName) throws DSSErrorException {
        //根据用户名 拿到用户ID
        //根据用户id 和工作空间id 拿到 角色id
        //根据role id 和工作空间id 拿到 重定向的 url
        List<Integer> tempWorkspaceIds = dssWorkspaceUserMapper.getWorkspaceIds(userName);
        if(tempWorkspaceIds == null || tempWorkspaceIds.isEmpty()){
            throw new DSSErrorException(30020, "该账号尚未加入工作空间，请联系管理员分配工作空间及用户角色");
        }
        List<Integer> workspaceIds = new ArrayList<>();
        tempWorkspaceIds.stream().
                map(dssWorkspaceInfoMapper::getWorkspaceNameById).
                filter(name -> !DEFAULT_DEMO_WORKSPACE_NAME.getValue().equals(name)).
                map(dssWorkspaceInfoMapper::getWorkspaceIdByName).
                forEach(workspaceIds::add);
        DSSWorkspaceHomePageVO dssWorkspaceHomePageVO = new DSSWorkspaceHomePageVO();
        if (workspaceIds.size() == 0){
            String userId = String.valueOf(dssWorkspaceUserMapper.getUserID(userName));
            int workspaceId = dssWorkspaceInfoMapper.getWorkspaceIdByName(DSSWorkspaceConstant.DEFAULT_WORKSPACE_NAME.getValue());
            dssWorkspaceUserMapper.setUserRoleInWorkspace(workspaceId, CommonRoleEnum.ANALYSER.getId(), userName, "system",userId);
            String homepageUrl = dssWorkspaceUserMapper.getHomepageUrl(workspaceId, CommonRoleEnum.ANALYSER.getId());
            if(ApplicationConf.HOMEPAGE_MODULE_NAME.getValue().equalsIgnoreCase(moduleName)){
                homepageUrl= ApplicationConf.HOMEPAGE_URL.getValue() + workspaceIds.get(0);
            }
            if(StringUtils.isEmpty(homepageUrl)) {
                homepageUrl = "/home" + "?workspaceId=" + workspaceId;
            }
            dssWorkspaceHomePageVO.setHomePageUrl(homepageUrl);
            dssWorkspaceHomePageVO.setWorkspaceId(workspaceId);
            dssWorkspaceHomePageVO.setRoleName(CommonRoleEnum.ANALYSER.getName());
        }else if(workspaceIds.size() == 1){
            //只有一个工作空间，那么就返回该工作空间的首页
            List<Integer> roleIds = dssWorkspaceUserMapper.getRoleInWorkspace(workspaceIds.get(0), userName);
            int minRoleId = Collections.min(roleIds);
            String homepageUrl = dssWorkspaceUserMapper.getHomepageUrl(workspaceIds.get(0), minRoleId);
            if("/workspace".equals(homepageUrl)){
                homepageUrl = "/workspaceHome";
            }
            if(StringUtils.isNotEmpty(homepageUrl)) {
                homepageUrl = homepageUrl + "?workspaceId=" + workspaceIds.get(0);
            }else{
                homepageUrl = "/home" + "?workspaceId=" + workspaceIds.get(0);
            }
            if(ApplicationConf.HOMEPAGE_MODULE_NAME.getValue().equalsIgnoreCase(moduleName)){
                homepageUrl= ApplicationConf.HOMEPAGE_URL.getValue() + workspaceIds.get(0);
            }
            dssWorkspaceHomePageVO.setHomePageUrl(homepageUrl);
            dssWorkspaceHomePageVO.setWorkspaceId(workspaceIds.get(0));
            dssWorkspaceHomePageVO.setRoleName(workspaceDBHelper.getRoleNameById(minRoleId));
        }else{
            //排除掉默认的默认工作空间bdapWorkspace
            String homepageUrl="/workspaceHome?workspaceId=" + workspaceIds.get(0);
            if(ApplicationConf.HOMEPAGE_MODULE_NAME.getValue().equalsIgnoreCase(moduleName)){
                homepageUrl= ApplicationConf.HOMEPAGE_URL.getValue() + workspaceIds.get(0);
            }
            dssWorkspaceHomePageVO.setWorkspaceId(workspaceIds.get(0));
            dssWorkspaceHomePageVO.setHomePageUrl(homepageUrl);
        }
        return dssWorkspaceHomePageVO;
    }

    @Override
    public List<DSSWorkspaceUserVO> getWorkspaceUsers(String workspaceId, String department, String username,
                                                      String roleName, int pageNow, int pageSize, List<Long> total) {
        int roleId = -1;
        if (StringUtils.isNotEmpty(roleName)){
            roleId = workspaceDBHelper.getRoleIdByName(roleName);
        }
        PageHelper.startPage(pageNow, pageSize);
        List<DSSWorkspaceUser> workspaceUsers = new ArrayList<>();
        try{
            workspaceUsers = dssWorkspaceUserMapper.getWorkspaceUsers(workspaceId,username);
        }finally {
            //PageHelper.clearPage();
        }
        PageInfo<DSSWorkspaceUser> pageInfo = new PageInfo<>(workspaceUsers);
        total.add(pageInfo.getTotal());
        List<DSSWorkspaceUserVO> dssWorkspaceUserVOs = new ArrayList<>();
        for (DSSWorkspaceUser workspaceUser : workspaceUsers) {
            List<Integer> roles = dssWorkspaceUserMapper.getRoleInWorkspace(Integer.parseInt(workspaceId), workspaceUser.getUsername());
            dssWorkspaceUserVOs.add(changeToUserVO(workspaceUser, roles));
        }
        return dssWorkspaceUserVOs;
    }

    private DSSWorkspaceUserVO changeToUserVO(DSSWorkspaceUser dssWorkspaceUser, List<Integer> roles){
        DSSWorkspaceUserVO vo = new DSSWorkspaceUserVO();
        String userName = dssWorkspaceUser.getUsername();
        vo.setName(userName);
        String orgFullName = staffInfoGetter.getFullOrgNameByUsername(userName);
        if (StringUtils.isNotEmpty(orgFullName)){
            try{
                String departmentName = orgFullName.split(WorkspaceServerConstant.DEFAULT_STAFF_SPLIT)[0];
                String officeName = orgFullName.split(WorkspaceServerConstant.DEFAULT_STAFF_SPLIT)[1];
                vo.setDepartment(departmentName);
                vo.setOffice(officeName);
            }catch(Exception e){
                //LOGGER.warn("fail to get department and office {} ", e.getMessage());
                vo.setDepartment(WorkspaceServerConstant.DEFAULT_DEPARTMENT);
                vo.setOffice(WorkspaceServerConstant.DEFAULT_OFFICE);
            }
        }
        vo.setRoles(roles);
        vo.setCreator(dssWorkspaceUser.getCreator());
        vo.setJoinTime(dssWorkspaceUser.getJoinTime());
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
        Map<Integer, List<DSSWorkspaceMenuRolePriv>> map = new HashMap<>();
        for (DSSWorkspaceMenuRolePriv dssWorkspaceMenuPriv : dssWorkspaceMenuRolePrivList){
            int menuId = dssWorkspaceMenuPriv.getMenuId();
            if(!map.containsKey(menuId)){
                map.put(menuId, new ArrayList<>());
            }
            map.get(menuId).add(dssWorkspaceMenuPriv);
        }
        // 得到(menuId, dssWorkspaceMenuRolePrivs)

        map.forEach((k,v) ->{
            DSSWorkspaceMenuPrivVO vo = new DSSWorkspaceMenuPrivVO();
            vo.setId(k);
            if(workspaceDBHelper.getMenuNameById(k) != null){
                vo.setName(workspaceDBHelper.getMenuNameById(k).getTitleCn());
                Map<String, Boolean> menuPrivs = new HashMap<>();
                workspaceRoleVOList.forEach(role->{
                    int roleId = role.getRoleId();
                    boolean isContain = false;
                    for(DSSWorkspaceMenuRolePriv dssWorkspaceMenuRolePriv:v){
                        if(roleId == dssWorkspaceMenuRolePriv.getRoleId()){
                            menuPrivs.put(role.getRoleName(), dssWorkspaceMenuRolePriv.getPriv() == 1);
                            isContain = true;
                            break;
                        }
                    }
                    if(!isContain){
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
        for (DSSWorkspaceComponentRolePriv p : defaultDssWorkspaceComponentRolePrivList){
            if (!dssWorkspaceComponentRolePrivList.contains(p)) {
                dssWorkspaceComponentRolePrivList.add(p);
            }
        }
        Map<Integer, List<DSSWorkspaceComponentRolePriv>> map1 = new HashMap<>();
        for (DSSWorkspaceComponentRolePriv dssWorkspaceComponentRolePriv : dssWorkspaceComponentRolePrivList){
            Integer componentId = dssWorkspaceComponentRolePriv.getComponentId();
            if (componentId == null) {
                continue;
            }
            if(!map1.containsKey(componentId)){
                List<DSSWorkspaceComponentRolePriv> tempList = new ArrayList<>();
                tempList.add(dssWorkspaceComponentRolePriv);
                map1.put(componentId, tempList);
            }else{
                map1.get(componentId).add(dssWorkspaceComponentRolePriv);
            }
        }
        map1.forEach((k,v) ->{
            DSSWorkspaceComponentPrivVO vo = new DSSWorkspaceComponentPrivVO();
            vo.setId(k);
            Map<String, Boolean> componentPrivs = new HashMap<>();

            if (workspaceDBHelper.getComponent(k) != null){
                vo.setName(workspaceDBHelper.getComponent(k).getName());
                workspaceRoleVOList.forEach(role->{
                    int roleId = role.getRoleId();
                    boolean isContain = false;
                    for(DSSWorkspaceComponentRolePriv dssWorkspaceComponentRolePriv:v){
                        if(roleId == dssWorkspaceComponentRolePriv.getRoleId()){
                            componentPrivs.put(role.getRoleName(), dssWorkspaceComponentRolePriv.getPriv() == null ? false :dssWorkspaceComponentRolePriv.getPriv() == 1);
                            isContain = true;
                            break;
                        }
                    }
                    if(!isContain){
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
        DSSWorkspace dssWorkspace = dssWorkspaceInfoMapper.getWorkspace(workspaceId);
        dssWorkspaceOverviewVO.setTitle(dssWorkspace.getName());
        dssWorkspaceOverviewVO.setDescription(dssWorkspace.getDescription());
        return dssWorkspaceOverviewVO;
    }

    @Override
    public DSSWorkspaceHomepageSettingVO getWorkspaceHomepageSettings(int workspaceId) {
        DSSWorkspaceHomepageSettingVO dssWorkspaceHomepageSettingVO = new DSSWorkspaceHomepageSettingVO();

        List<DSSWorkspaceHomepageSetting> dssWorkspaceHomepageSettings = dssWorkspaceMenuMapper.getWorkspaceHompageSettings(workspaceId);
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
    public String getWorkspaceName(String workspaceId) {
        return dssWorkspaceInfoMapper.getWorkspaceNameById(Integer.parseInt(workspaceId));
    }

    @Override
    public boolean checkAdmin(String userName) {
        return dssWorkspaceUserMapper.isAdmin(userName);
    }

    @Override
    public boolean checkAdminByWorkspace(String username, int workspaceId) {
        List<String> roles = dssWorkspaceRoleService.getRoleInWorkspace(username, workspaceId);
        return roles.stream().anyMatch(role->role.equalsIgnoreCase("admin"));
    }

    @Override
    public List<DepartmentVO> getDepartments() {
        List<String> allDepartments = staffInfoGetter.getAllDepartments();
        List<DepartmentVO> departmentVOs = new ArrayList<>();
        int count = 1;
        for (String department : allDepartments){
            DepartmentVO departmentVO = new DepartmentVO();
            departmentVO.setFrontName(department);
            departmentVO.setName(department);
            departmentVO.setId(count);
            departmentVOs.add(departmentVO);
            count ++;
        }
        return departmentVOs;
    }

    @Override
    public List<DSSWorkspaceUserVO> getWorkspaceUsersByRole(int workspaceId, String roleName, List<Long> totals, int pageNow, int pageSize) {
        int roleId = workspaceDBHelper.getRoleIdByName(roleName);
        PageHelper.startPage(pageNow, pageSize);
        List<DSSWorkspaceUser> workspaceUsers = new ArrayList<>();
        try{
            workspaceUsers = dssWorkspaceUserMapper.getWorkspaceUsersByRole(workspaceId, roleId);
        }finally {
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


    @Override
    public DSSWorkspace getWorkspacesById(Long id, String username) throws DSSErrorException {
        List<String> users = dssWorkspaceUserMapper.getAllWorkspaceUsers(id.intValue());
        if(!users.contains(username)) {
            throw new DSSErrorException(30021, "You have no permission to access this workspace " + id);
        }
        DSSWorkspace dssWorkSpace = workspaceMapper.getWorkspaceById(id);
        if(dssWorkSpace == null) {
            throw new DSSErrorException(30022, "workspace " + id + " is not exists.");
        }
        String originDepart = dssWorkSpace.getDepartment();
        if(StringUtils.isNotBlank(originDepart)){
            String departName = workspaceMapper.getDepartName(Long.valueOf(originDepart));
            dssWorkSpace.setDepartment(departName);
        }
        return dssWorkSpace;
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

    @Override
    public List<OnestopMenuVo> getWorkspaceManagements(Long workspaceId, String username, boolean isChinese) {
        List<OnestopMenuVo> applicationMenuVos = isChinese ? workspaceMapper.getApplicationMenuCn() : workspaceMapper.getApplicationMenuEn();
        List<Long> userMenuApplicationId = dssWorkspaceMapper.getUserMenuApplicationId(username, workspaceId);
        return getMenuAppInstances(applicationMenuVos, userMenuApplicationId, isChinese);
    }

    private List<OnestopMenuVo> getMenuAppInstances(List<OnestopMenuVo> menuVos, List<Long> userMenuApplicationId,
                                                    boolean isChinese) {
        List<AppConn> appConns = AppConnManager.getAppConnManager().listAppConns();
        for (OnestopMenuVo menuVo : menuVos) {
            Long menuId = menuVo.getId();
            List<OnestopMenuAppInstanceVo> menuAppInstanceVos = isChinese ? workspaceMapper.getMenuAppInstancesCn(menuId) : workspaceMapper.getMenuAppInstancesEn(menuId);
            for (OnestopMenuAppInstanceVo menuAppInstanceVo : menuAppInstanceVos) {
                // 如果该工作空间中用户拥有该组件权限，则该组件的accessable属性为true；否则为false
                menuAppInstanceVo.setAccessable(userMenuApplicationId.contains(menuAppInstanceVo.getId()));
                Map<String, String> nameAndUrl = new HashMap<>();
                appConns.forEach(appConn -> {
                    if(appConn.getAppDesc().getAppName().equalsIgnoreCase(menuAppInstanceVo.getName())) {
                        if (appConn.getAppDesc().getAppInstances().size() == 2) {
                            appConn.getAppDesc().getAppInstances().forEach(appInstance -> {
                                nameAndUrl.put("进入开发中心", appInstance.getBaseUrl());
                            });
                        }
                    }
                });
                if(nameAndUrl.size()==0) {
                    nameAndUrl.put(menuAppInstanceVo.getAccessButton(), menuAppInstanceVo.getAccessButtonUrl());
                }
                menuAppInstanceVo.setNameAndUrls(nameAndUrl);
            }
            menuVo.setAppInstances(menuAppInstanceVos);
        }
        return menuVos;
    }

    @Override
    public List<OnestopMenuVo> getWorkspaceApplications(Long workspaceId, String username, boolean isChinese) {
        List<OnestopMenuVo> applicationMenuVos = isChinese ? workspaceMapper.getApplicationMenuCn() : workspaceMapper.getApplicationMenuEn();
        List<Long> userMenuApplicationId = dssWorkspaceMapper.getUserMenuApplicationId(username, workspaceId);
        return getMenuAppInstances(applicationMenuVos, userMenuApplicationId, isChinese);
    }

    @Override
    public List<WorkspaceFavoriteVo> getWorkspaceFavorites(Long workspaceId, String username, boolean isChinese,String type) {
        return isChinese ? workspaceMapper.getWorkspaceFavoritesCn(username, workspaceId,type) : workspaceMapper.getWorkspaceFavoritesEn(username, workspaceId,type);
    }

    @Override
    public Long addFavorite(String username, Long workspaceId, Long menuApplicationId,String type) {
        DSSFavorite dssFavorite = new DSSFavorite();
        dssFavorite.setUsername(username);
        dssFavorite.setWorkspaceId(workspaceId);
        dssFavorite.setMenuApplicationId(menuApplicationId);
        // todo: order will from the front end
        dssFavorite.setOrder(1);
        dssFavorite.setCreateBy(username);
        dssFavorite.setLastUpdateUser(username);
        dssFavorite.setType(type);
        workspaceMapper.addFavorite(dssFavorite);
        return dssFavorite.getId();
    }

    @Override
    public Long deleteFavorite(String username, Long applicationId, Long workspaceId,String type) {
        workspaceMapper.deleteFavorite(username, applicationId, workspaceId,type);
        return applicationId;
    }


    /**
     * 是否超级管理员
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
        if (workspace.getName().equals(DSSWorkspaceConstant.DEFAULT_WORKSPACE_NAME.getValue())) {
            String superAdmin = DSSWorkspaceConstant.SUPER_ADMIN;
            if (StringUtils.isNotBlank(superAdmin)) {
                superAdmin = superAdmin.replace("，", ",");
                String[] accounts = superAdmin.split(",");
                for (int i = 0; i < accounts.length; i++) {
                    if (accounts[i].equals(username)) {
                        return true;
                    }
                }
            }
        }
        return username != null && workspace != null && username.equals(workspace.getCreateBy());
    }
}
