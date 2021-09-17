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

package com.webank.wedatasphere.dss.framework.workspace.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.webank.wedatasphere.dss.framework.workspace.bean.DSSFavorite;
import com.webank.wedatasphere.dss.framework.workspace.bean.DSSWorkspace;
import com.webank.wedatasphere.dss.framework.workspace.bean.DSSWorkspaceComponentRolePriv;
import com.webank.wedatasphere.dss.framework.workspace.bean.DSSWorkspaceHomepageSetting;
import com.webank.wedatasphere.dss.framework.workspace.bean.DSSWorkspaceMenuRolePriv;
import com.webank.wedatasphere.dss.framework.workspace.bean.DSSWorkspaceUser;
import com.webank.wedatasphere.dss.framework.workspace.bean.DSSWorkspaceUser01;
import com.webank.wedatasphere.dss.framework.workspace.bean.dto.response.HomepageDemoInstanceVo;
import com.webank.wedatasphere.dss.framework.workspace.bean.dto.response.HomepageDemoMenuVo;
import com.webank.wedatasphere.dss.framework.workspace.bean.dto.response.HomepageVideoVo;
import com.webank.wedatasphere.dss.framework.workspace.bean.dto.response.OnestopMenuAppInstanceVo;
import com.webank.wedatasphere.dss.framework.workspace.bean.dto.response.OnestopMenuVo;
import com.webank.wedatasphere.dss.framework.workspace.bean.dto.response.WorkspaceDepartmentVo;
import com.webank.wedatasphere.dss.framework.workspace.bean.dto.response.WorkspaceFavoriteVo;
import com.webank.wedatasphere.dss.framework.workspace.bean.vo.DSSWorkspaceComponentPrivVO;
import com.webank.wedatasphere.dss.framework.workspace.bean.vo.DSSWorkspaceHomePageVO;
import com.webank.wedatasphere.dss.framework.workspace.bean.vo.DSSWorkspaceHomepageSettingVO;
import com.webank.wedatasphere.dss.framework.workspace.bean.vo.DSSWorkspaceMenuPrivVO;
import com.webank.wedatasphere.dss.framework.workspace.bean.vo.DSSWorkspaceOverviewVO;
import com.webank.wedatasphere.dss.framework.workspace.bean.vo.DSSWorkspacePrivVO;
import com.webank.wedatasphere.dss.framework.workspace.bean.vo.DSSWorkspaceRoleVO;
import com.webank.wedatasphere.dss.framework.workspace.bean.vo.DSSWorkspaceUserVO;
import com.webank.wedatasphere.dss.framework.workspace.bean.vo.DepartmentVO;
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
import com.webank.wedatasphere.dss.framework.workspace.service.DSSUserService;
import com.webank.wedatasphere.dss.framework.workspace.service.DSSWorkspaceMenuService;
import com.webank.wedatasphere.dss.framework.workspace.service.DSSWorkspaceService;
import com.webank.wedatasphere.dss.framework.workspace.service.DSSWorkspaceUserService;
import com.webank.wedatasphere.dss.framework.workspace.util.CommonRoleEnum;
import com.webank.wedatasphere.dss.framework.workspace.util.DSSWorkspaceConstant;
import com.webank.wedatasphere.dss.framework.workspace.util.WorkspaceDBHelper;
import com.webank.wedatasphere.dss.framework.workspace.util.WorkspaceServerConstant;
import com.webank.wedatasphere.linkis.common.exception.ErrorException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.webank.wedatasphere.dss.framework.workspace.util.DSSWorkspaceConstant.DEFAULT_DEMO_WORKSPACE_NAME;
/**
 * @Author: chaogefeng
 * @Date: 2020/3/9
 */
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
        dssWorkspaceUserMapper.insertUser(userName, dssWorkspace.getId(), userName,userId);
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
            //保存 - dss_user、linkis_user
            dssUserService.saveWorkspaceUser(userName);
        }
        //保存 - dss_workspace_user
        dssWorkspaceUserMapper.insertUser(userName, workspaceId, creator,userId);
        //保存 - 保存用户角色关系 dss_workspace_user_role
        for (Integer roleId : roleIds) {
            dssWorkspaceUserMapper.setUserRoleInWorkspace(workspaceId, roleId, userName, creator,userId);
        }
    }

    //获取所有的工作空间
    @Override
    public  List<DSSWorkspace> getWorkspaces(String userName){
        List<DSSWorkspace> workspaces = dssWorkspaceMapper.getWorkspaces(userName);
        //用于展示demo的工作空间是不应该返回的,除非用户是管理员
        if(dssWorkspaceUserMapper.isAdmin(userName)) {
            return workspaces;
        }else{
            //踢掉那个演示demo工作空间
            List<DSSWorkspace> retWorkspaces = new ArrayList<>();
            String defaultDemoWorkspaceName = DEFAULT_DEMO_WORKSPACE_NAME.getValue();
            for (DSSWorkspace workspace : workspaces) {
                if (!workspace.getName().equals(defaultDemoWorkspaceName)){
                    retWorkspaces.add(workspace);
                }
            }
            return retWorkspaces;
        }
    }

    @Override
    public DSSWorkspaceHomePageVO getWorkspaceHomePage(String userName,String moduleName) throws Exception {
        //根据用户名 拿到用户ID
        //根据用户id 和工作空间id 拿到 角色id
        //根据role id 和工作空间id 拿到 重定向的 url
        List<Integer> tempWorkspaceIds = dssWorkspaceUserMapper.getWorkspaceIds(userName);
        if(tempWorkspaceIds == null || tempWorkspaceIds.isEmpty()){
            throw  new Exception("该账号尚未加入工作空间，请联系管理员分配工作空间及用户角色");
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
            dssWorkspaceUserMapper.insertUser(userName, workspaceId, "system",userId);
            dssWorkspaceUserMapper.setUserRoleInWorkspace(workspaceId, CommonRoleEnum.ANALYSER.getId(), userName, "system",userId);
            String homepageUrl = dssWorkspaceUserMapper.getHomepageUrl(workspaceId, CommonRoleEnum.ANALYSER.getId());
            if(ApplicationConf.HOMEPAGE_MODULE_NAME.getValue().equalsIgnoreCase(moduleName)){
                homepageUrl= ApplicationConf.HOMEPAGE_URL.getValue() + workspaceIds.get(0);
            }
            dssWorkspaceHomePageVO.setHomePageUrl(homepageUrl);
            dssWorkspaceHomePageVO.setWorkspaceId(workspaceId);
            dssWorkspaceHomePageVO.setRoleName(CommonRoleEnum.ANALYSER.getName());
        }else if(workspaceIds.size() == 1){
            //只有一个工作空间，那么就返回该工作空间的首页
            List<Integer> roleIds = dssWorkspaceUserMapper.getRoleInWorkspace(workspaceIds.get(0), userName);
            int minRoleId = Collections.min(roleIds);
            String homepageUrl = dssWorkspaceUserMapper.getHomepageUrl(workspaceIds.get(0), minRoleId);
            if(StringUtils.isNotEmpty(homepageUrl)) {
                homepageUrl = "/home" + "?workspaceId=" + workspaceIds.get(0);
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
            dssWorkspaceHomePageVO.setHomePageUrl(homepageUrl);
        }
        return dssWorkspaceHomePageVO;
    }

    @Override
    public List<DSSWorkspaceUser01> getWorkspaceUsers(String workspaceId, String department, String username,
                                                      String roleName, int pageNow, int pageSize, List<Long> total) {
        int roleId = -1;
        if (StringUtils.isNotEmpty(roleName)){
            roleId = workspaceDBHelper.getRoleIdByName(roleName);
        }
        PageHelper.startPage(pageNow, pageSize);
        List<DSSWorkspaceUser01> workspaceUsers = new ArrayList<>();
        try{
            workspaceUsers = workspaceMapper.getWorkspaceUsers(Long.valueOf(workspaceId));
        }finally {
            PageHelper.clearPage();
        }
        PageInfo<DSSWorkspaceUser01> pageInfo = new PageInfo<>(workspaceUsers);
        total.add(pageInfo.getTotal());
        List<DSSWorkspaceUserVO> dssWorkspaceUserVOs = new ArrayList<>();
        return workspaceUsers;
    }


    private DSSWorkspaceUserVO changeToUserVO(DSSWorkspaceUser dssWorkspaceUser, List<Integer> roles){
        DSSWorkspaceUserVO vo = new DSSWorkspaceUserVO();
        String userName = dssWorkspaceUser.getUsername();
        vo.setName(userName);
        String orgFullName = "WeDataSphere";
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
        List<DSSWorkspaceRoleVO>   workspaceRoleVOList = workspaceDBHelper.getRoleVOs(Integer.parseInt(workspaceId));
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
            int componentId = dssWorkspaceComponentRolePriv.getComponentId();
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
    public List<DepartmentVO> getDepartments() {
        List<String> allDepartments = Arrays.asList("WeDataSphere","DataSP", "linkis");
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
    public List<DSSWorkspace> getWorkspaces() {

        return workspaceMapper.getWorkspaces();
    }

    @Override
    public DSSWorkspace getWorkspacesById(Long id) {
        DSSWorkspace dssWorkSpace = workspaceMapper.getWorkspaceById(id);
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
    public List<HomepageDemoMenuVo> getHomepageDemos(boolean isChinese) {
        List<HomepageDemoMenuVo> demoMenuVos = isChinese ? workspaceMapper.getHomepageDemoMenusCn() : workspaceMapper.getHomepageDemoMenusEn();
        for (HomepageDemoMenuVo demoMenuVo : demoMenuVos) {
            Long menuId = demoMenuVo.getId();
            List<HomepageDemoInstanceVo> demoInstanceVos = isChinese ? workspaceMapper.getHomepageInstancesByMenuIdCn(menuId) : workspaceMapper.getHomepageInstancesByMenuIdEn(menuId);
            demoMenuVo.setDemoInstances(demoInstanceVos);
        }
        return demoMenuVos;
    }

    @Override
    public List<HomepageVideoVo> getHomepageVideos(boolean isChinese) {
        return isChinese ? workspaceMapper.getHomepageVideosCn() : workspaceMapper.getHomepageVideosEn();
    }

    @Override
    public List<OnestopMenuVo> getWorkspaceManagements(Long workspaceId, String username, boolean isChinese) {
        if (!isAdminUser(workspaceId, username)) {
            return new ArrayList<>();
        }
        List<OnestopMenuVo> managementMenuVos = isChinese ? workspaceMapper.getManagementMenuCn() : workspaceMapper.getManagementMenuEn();
        return getMenuAppInstances(managementMenuVos, isChinese);
    }

    private List<OnestopMenuVo> getMenuAppInstances(List<OnestopMenuVo> menuVos, boolean isChinese) {
        for (OnestopMenuVo menuVo : menuVos) {
            Long menuId = menuVo.getId();
            List<OnestopMenuAppInstanceVo> menuAppInstanceVos = isChinese ? workspaceMapper.getMenuAppInstancesCn(menuId) : workspaceMapper.getMenuAppInstancesEn(menuId);

            for (OnestopMenuAppInstanceVo menuAppInstanceVo : menuAppInstanceVos) {
                Map<String, String> nameAndUrl = new HashMap<>();
                if ("visualis".equals(menuAppInstanceVo.getName())){
                    nameAndUrl.put("进入开发中心", menuAppInstanceVo.getAccessButtonUrl());
                    nameAndUrl.put("进入生产中心", menuAppInstanceVo.getAccessButtonUrl());
                }else{
                    nameAndUrl.put(menuAppInstanceVo.getAccessButton(), menuAppInstanceVo.getAccessButtonUrl());
                }
                menuAppInstanceVo.setNameAndUrls(nameAndUrl);
            }
            menuVo.setAppInstances(menuAppInstanceVos);
        }
        return menuVos;
    }

    private List<OnestopMenuVo> getMenuAppInstances(List<OnestopMenuVo> menuVos, List<Long> userMenuApplicationId,
                                                    boolean isChinese) {
        for (OnestopMenuVo menuVo : menuVos) {
            Long menuId = menuVo.getId();
            List<OnestopMenuAppInstanceVo> menuAppInstanceVos = isChinese ? workspaceMapper.getMenuAppInstancesCn(menuId) : workspaceMapper.getMenuAppInstancesEn(menuId);

            for (OnestopMenuAppInstanceVo menuAppInstanceVo : menuAppInstanceVos) {
                // 如果该工作空间中用户拥有该组件权限，则该组件的accessable属性为true；否则为false
                menuAppInstanceVo.setAccessable(userMenuApplicationId.contains(menuAppInstanceVo.getId()));
                Map<String, String> nameAndUrl = new HashMap<>();
                if ("visualis".equals(menuAppInstanceVo.getName())){
                    nameAndUrl.put("进入开发中心", menuAppInstanceVo.getAccessButtonUrl());
                    nameAndUrl.put("进入生产中心", menuAppInstanceVo.getAccessButtonUrl());
                }else{
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
    public List<WorkspaceFavoriteVo> getWorkspaceFavorites(Long workspaceId, String username, boolean isChinese) {
        return isChinese ? workspaceMapper.getWorkspaceFavoritesCn(username, workspaceId) : workspaceMapper.getWorkspaceFavoritesEn(username, workspaceId);
    }

    @Override
    public Long addFavorite(String username, Long workspaceId, Long menuApplicationId) {
        DSSFavorite dssFavorite = new DSSFavorite();
        dssFavorite.setUsername(username);
        dssFavorite.setWorkspaceId(workspaceId);
        dssFavorite.setMenuApplicationId(menuApplicationId);
        // todo: order will from the front end
        dssFavorite.setOrder(1);
        dssFavorite.setCreateBy(username);
        dssFavorite.setLastUpdateUser(username);
        workspaceMapper.addFavorite(dssFavorite);
        return dssFavorite.getId();
    }

    @Override
    public Long deleteFavorite(String username, Long applicationId, Long workspaceId) {
        workspaceMapper.deleteFavorite(username, applicationId, workspaceId);
        return applicationId;
    }

    private boolean isAdminUser(Long workspaceId, String username) {
        DSSWorkspace workspace = workspaceMapper.getWorkspaceById(workspaceId);
        List<Integer> roles = dssWorkspaceUserMapper.getRoleInWorkspace(workspaceId.intValue(), username);
        if(roles != null && roles.size() > 0) {
            for (Integer role : roles){
                if (role == 1){
                    return true;
                }
            }

        }
        return username != null && workspace != null && username.equals(workspace.getCreateBy());
    }
}
