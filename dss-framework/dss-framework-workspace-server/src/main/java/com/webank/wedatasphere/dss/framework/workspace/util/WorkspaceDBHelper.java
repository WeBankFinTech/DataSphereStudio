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

package com.webank.wedatasphere.dss.framework.workspace.util;


import com.webank.wedatasphere.dss.appconn.manager.utils.AppInstanceConstants;
import com.webank.wedatasphere.dss.framework.workspace.bean.*;
import com.webank.wedatasphere.dss.framework.workspace.bean.vo.DSSWorkspaceRoleVO;
import com.webank.wedatasphere.dss.framework.workspace.dao.DSSWorkspaceRoleMapper;
import org.apache.commons.lang.StringUtils;
import org.apache.linkis.common.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class WorkspaceDBHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkspaceDBHelper.class);

    @Autowired
    private DSSWorkspaceRoleMapper dssWorkspaceRoleMapper;

    private List<DSSWorkspaceRole> dssRoles;

    private final Object lock = new Object();

    private List<DSSWorkspaceMenu> dssWorkspaceMenus;

    private List<DSSApplicationBean> dssApplicationBeans;

    private Map<String, String> dssHomepageNameMap = new HashMap<>();


    @PostConstruct
    public void init() {
        Utils.defaultScheduler().scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                synchronized (lock) {
                    dssRoles = dssWorkspaceRoleMapper.getRoles();
                }
            }
        }, 0, 1, TimeUnit.MINUTES);

        Utils.defaultScheduler().scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                synchronized (lock) {
                    dssWorkspaceMenus = dssWorkspaceRoleMapper.getWorkspaceMenus();
                }
            }
        }, 0, 1, TimeUnit.MINUTES);

        Utils.defaultScheduler().scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                synchronized (lock) {
                    dssApplicationBeans = dssWorkspaceRoleMapper.getDSSAppConns();
                }
            }
        }, 0, 1, TimeUnit.MINUTES);


        Utils.defaultScheduler().scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                synchronized (lock) {
                    String defaultUrl = "/workspace";
                    String maintenanceUrl = "/workspaceHome/scheduleCenter";
                    String developerUrl =
                            dssApplicationBeans.stream().
                                    filter(dssApplicationBean -> "workflow".equals(dssApplicationBean.getName().toLowerCase())).
                                    findFirst().map(WorkspaceDBHelper.this::getHomepageUrl).orElse(defaultUrl);
                    String analyserUrl = dssApplicationBeans.stream().
                            filter(dssApplicationBean -> "linkis".equals(dssApplicationBean.getName().toLowerCase())).
                            findFirst().map(WorkspaceDBHelper.this::getHomepageUrl).orElse(defaultUrl);
                    dssHomepageNameMap.put("/workspace", "工作空间首页");
                    dssHomepageNameMap.put(developerUrl, "工作流开发");
                    dssHomepageNameMap.put(analyserUrl, "意书(Scriptis)");
                    dssHomepageNameMap.put(maintenanceUrl, "调度中心首页");
                }
            }
        }, 1, 1, TimeUnit.MINUTES);
    }

    private String getHomepageUrl(DSSApplicationBean bean) {
        return AppInstanceConstants.getHomepageUrl(bean.getUrl(), bean.getHomepageUri(), null, null);
    }

    public void retrieveFromDB() {
        synchronized (lock) {
            dssApplicationBeans = dssWorkspaceRoleMapper.getDSSAppConns();
            dssWorkspaceMenus = dssWorkspaceRoleMapper.getWorkspaceMenus();
            dssRoles = dssWorkspaceRoleMapper.getRoles();
        }
    }

    public List<DSSWorkspaceMenuRole> generateDefaultWorkspaceMenuRole(int workspaceId, String username) {
        List<DSSWorkspaceMenuRole> list = new ArrayList<>();
        Date date = new Date(System.currentTimeMillis());
        //todo 需要确定各角色默认的菜单权限
        list.add(new DSSWorkspaceMenuRole(workspaceId, getMenuIdByName(CommonMenuEnum.DATA_ANALYSIS.getName()),
                getRoleIdByName(CommonRoleEnum.ADMIN.getName()), 1, date, username));
        list.add(new DSSWorkspaceMenuRole(workspaceId, getMenuIdByName(CommonMenuEnum.PRODUCTION_OPERATION.getName()),
                getRoleIdByName(CommonRoleEnum.ADMIN.getName()), 1, date, username));

        list.add(new DSSWorkspaceMenuRole(workspaceId, getMenuIdByName(CommonMenuEnum.DATA_ANALYSIS.getName()),
                getRoleIdByName(CommonRoleEnum.MAINTENANCE.getName()), 0, date, username));
        list.add(new DSSWorkspaceMenuRole(workspaceId, getMenuIdByName(CommonMenuEnum.PRODUCTION_OPERATION.getName()),
                getRoleIdByName(CommonRoleEnum.MAINTENANCE.getName()), 0, date, username));

        list.add(new DSSWorkspaceMenuRole(workspaceId, getMenuIdByName(CommonMenuEnum.DATA_ANALYSIS.getName()),
                getRoleIdByName(CommonRoleEnum.DEVELOPER.getName()), 1, date, username));
        list.add(new DSSWorkspaceMenuRole(workspaceId, getMenuIdByName(CommonMenuEnum.PRODUCTION_OPERATION.getName()),
                getRoleIdByName(CommonRoleEnum.DEVELOPER.getName()), 0, date, username));

        list.add(new DSSWorkspaceMenuRole(workspaceId, getMenuIdByName(CommonMenuEnum.DATA_ANALYSIS.getName()),
                getRoleIdByName(CommonRoleEnum.ANALYSER.getName()), 0, date, username));
        list.add(new DSSWorkspaceMenuRole(workspaceId, getMenuIdByName(CommonMenuEnum.PRODUCTION_OPERATION.getName()),
                getRoleIdByName(CommonRoleEnum.ANALYSER.getName()), 1, date, username));

        list.add(new DSSWorkspaceMenuRole(workspaceId, getMenuIdByName(CommonMenuEnum.DATA_ANALYSIS.getName()),
                getRoleIdByName(CommonRoleEnum.OPERATOR.getName()), 0, date, username));
        list.add(new DSSWorkspaceMenuRole(workspaceId, getMenuIdByName(CommonMenuEnum.PRODUCTION_OPERATION.getName()),
                getRoleIdByName(CommonRoleEnum.OPERATOR.getName()), 0, date, username));

        list.add(new DSSWorkspaceMenuRole(workspaceId, getMenuIdByName(CommonMenuEnum.DATA_ANALYSIS.getName()),
                getRoleIdByName(CommonRoleEnum.BOSS.getName()), 1, date, username));
        list.add(new DSSWorkspaceMenuRole(workspaceId, getMenuIdByName(CommonMenuEnum.PRODUCTION_OPERATION.getName()),
                getRoleIdByName(CommonRoleEnum.BOSS.getName()), 1, date, username));

//        if (StringUtils.isNotBlank(ApplicationConf.ESB_APPID)) {
//            list.add(new DSSWorkspaceMenuRole(workspaceId, 1, 1, 1, date, username));
//            list.add(new DSSWorkspaceMenuRole(workspaceId, 1, 2, 1, date, username));
//            list.add(new DSSWorkspaceMenuRole(workspaceId, 1, 3, 0, date, username));
//            list.add(new DSSWorkspaceMenuRole(workspaceId, 1, 4, 0, date, username));
//            list.add(new DSSWorkspaceMenuRole(workspaceId, 1, 5, 0, date, username));
//            list.add(new DSSWorkspaceMenuRole(workspaceId, 1, 6, 1, date, username));
//        }
        return list;
    }


    public List<DSSWorkspaceHomepage> generateDefaultWorkspaceHomepage(int workspaceId, String username) {
        String defaultUrl = "/workspace";
        String maintenanceUrl = "/workspaceHome/scheduleCenter";
        String developerUrl =
                dssApplicationBeans.stream().
                        filter(dssApplicationBean -> "workflow".equals(dssApplicationBean.getName().toLowerCase())).
                        findFirst().map(this::getHomepageUrl).orElse(defaultUrl);
        String analyserUrl = dssApplicationBeans.stream().
                filter(dssApplicationBean -> "linkis".equals(dssApplicationBean.getName().toLowerCase())).
                findFirst().map(this::getHomepageUrl).orElse(defaultUrl);
        List<DSSWorkspaceHomepage> dssWorkspaceHomepages = new ArrayList<>();
        Date date = new Date(System.currentTimeMillis());
        dssWorkspaceHomepages.add(new DSSWorkspaceHomepage(workspaceId, getRoleIdByName(CommonRoleEnum.ADMIN.getName()), defaultUrl, date));
        dssWorkspaceHomepages.add(new DSSWorkspaceHomepage(workspaceId, getRoleIdByName(CommonRoleEnum.MAINTENANCE.getName()), maintenanceUrl, date));
        dssWorkspaceHomepages.add(new DSSWorkspaceHomepage(workspaceId, getRoleIdByName(CommonRoleEnum.DEVELOPER.getName()), developerUrl, date));
        dssWorkspaceHomepages.add(new DSSWorkspaceHomepage(workspaceId, getRoleIdByName(CommonRoleEnum.ANALYSER.getName()), analyserUrl, date));
        dssWorkspaceHomepages.add(new DSSWorkspaceHomepage(workspaceId, getRoleIdByName(CommonRoleEnum.OPERATOR.getName()), defaultUrl, date));
        dssWorkspaceHomepages.add(new DSSWorkspaceHomepage(workspaceId, getRoleIdByName(CommonRoleEnum.BOSS.getName()), defaultUrl, date));
        return dssWorkspaceHomepages;
    }


    public List<DSSWorkspaceComponentPriv> generateDefaultWorkspaceComponentPrivs(int workspaceId,
                                                                                  String username) {
        List<DSSWorkspaceComponentPriv> dssWorkspaceComponentPrivs = new ArrayList<>();
        Date updateTime = new Date(System.currentTimeMillis());
        //admin 添加所有appconn组件的访问权限
        new HashSet<>(getAppConnIds()).forEach(id -> {
            dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, getRoleIdByName(CommonRoleEnum.ADMIN.getName()),
                    id, 1, updateTime, username));
        });

        //运维
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, getRoleIdByName(CommonRoleEnum.MAINTENANCE.getName()),
                getAppConnIdByName(CommonAppConnEnum.SCRIPTIS.getName()), 1, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, getRoleIdByName(CommonRoleEnum.MAINTENANCE.getName()),
                getAppConnIdByName(CommonAppConnEnum.WORKFLOW.getName()), 1, updateTime, username));
        //开发人员
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, getRoleIdByName(CommonRoleEnum.DEVELOPER.getName()),
                getAppConnIdByName(CommonAppConnEnum.SCRIPTIS.getName()), 1, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, getRoleIdByName(CommonRoleEnum.DEVELOPER.getName()),
                getAppConnIdByName(CommonAppConnEnum.WORKFLOW.getName()), 1, updateTime, username));
        //分析人员
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, getRoleIdByName(CommonRoleEnum.ANALYSER.getName()),
                getAppConnIdByName(CommonAppConnEnum.SCRIPTIS.getName()), 1, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, getRoleIdByName(CommonRoleEnum.ANALYSER.getName()),
                getAppConnIdByName(CommonAppConnEnum.WORKFLOW.getName()), 1, updateTime, username));
        //运营人员
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, getRoleIdByName(CommonRoleEnum.OPERATOR.getName()),
                getAppConnIdByName(CommonAppConnEnum.SCRIPTIS.getName()), 1, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, getRoleIdByName(CommonRoleEnum.OPERATOR.getName()),
                getAppConnIdByName(CommonAppConnEnum.WORKFLOW.getName()), 1, updateTime, username));
        //领导
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, getRoleIdByName(CommonRoleEnum.BOSS.getName()),
                getAppConnIdByName(CommonAppConnEnum.SCRIPTIS.getName()), 1, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, getRoleIdByName(CommonRoleEnum.BOSS.getName()),
                getAppConnIdByName(CommonAppConnEnum.WORKFLOW.getName()), 1, updateTime, username));

        /* if(StringUtils.isNotBlank(ApplicationConf.ESB_APPID)){
            dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 1, 3, 1, updateTime, username));
            dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 2, 3, 1, updateTime, username));
            dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 3, 3, 1, updateTime, username));
            dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 4, 3, 1, updateTime, username));
            dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 5, 3, 1, updateTime, username));
            dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 6, 3, 1, updateTime, username));
        }*/
        return dssWorkspaceComponentPrivs;
    }


    public List<DSSWorkspaceRole> getRoles() {
        return this.dssRoles;
    }


    public List<DSSWorkspaceRole> getRoles(int workspaceId) {
        return this.dssRoles.stream().
                filter(dssRole -> dssRole.getWorkspaceId() == workspaceId || dssRole.getWorkspaceId() == -1).
                collect(Collectors.toList());
    }


    public String getRoleFrontName(int roleId) {
        DSSWorkspaceRole role = dssRoles.stream().filter(dssRole -> dssRole.getId() == roleId).findFirst().orElse(null);
        if (role != null) {
            return role.getFrontName();
        } else {
            return null;
        }
    }

    public int getRoleIdByName(String roleName) {
        if (StringUtils.isBlank(roleName)) {
            return -1;
        }
        return dssRoles.stream().
                filter(dssRole -> roleName.equals(dssRole.getName())).
                findFirst().
                map(DSSWorkspaceRole::getId).
                orElse(-1);
    }


    public List<DSSWorkspaceRoleVO> getRoleVOs(int workspaceId) {
        return getRoles(workspaceId).stream().map(this::changeToRoleVO).collect(Collectors.toList());
    }

    private DSSWorkspaceRoleVO changeToRoleVO(DSSWorkspaceRole dssRole) {
        DSSWorkspaceRoleVO vo = new DSSWorkspaceRoleVO();
        vo.setRoleFrontName(dssRole.getFrontName());
        vo.setRoleId(dssRole.getId());
        vo.setRoleName(dssRole.getName());
        return vo;
    }

    public DSSWorkspaceMenu getMenuNameById(int menuId) {
        if (dssWorkspaceMenus.stream().anyMatch(dssMenu -> dssMenu.getId() == menuId)) {
            return dssWorkspaceMenus.stream().filter(dssMenu -> dssMenu.getId() == menuId).findFirst().get();
        } else {
            return null;
        }
    }

    public int getMenuIdByName(String name) {
        if (dssWorkspaceMenus.stream().anyMatch(dssMenu -> dssMenu.getName().equals(name))) {
            return dssWorkspaceMenus.stream().filter(dssMenu -> dssMenu.getName().equals(name)).findFirst().get().getId();
        } else {
            return -1;
        }
    }


    public String getRoleNameById(int roleId) {
        return dssRoles.stream().filter(dssRole -> dssRole.getId() == roleId).findFirst().orElse(new DSSWorkspaceRole()).getName();
    }

    public DSSApplicationBean getAppConn(int appConnId) {
        return dssApplicationBeans.stream().
                filter(dssApplicationBean -> dssApplicationBean.getId() == appConnId).
                findFirst().
                orElse(null);
    }

    public DSSApplicationBean getAppConn(String appConnName) {
        return dssApplicationBeans.stream().
                filter(dssApplicationBean -> dssApplicationBean.getName().equalsIgnoreCase(appConnName)).
                findFirst().
                orElse(null);
    }

    public Integer getAppConnIdByName(String appConnName) {
        return dssApplicationBeans.stream().
                filter(l -> l.getName().equalsIgnoreCase(appConnName)).
                findFirst().get().getId();
    }

    public List<Integer> getAppConnIds() {
        return dssApplicationBeans.stream().map(DSSApplicationBean::getId).collect(Collectors.toList());
    }

    public String getHomepageName(String homepage) {
        return this.dssHomepageNameMap.get(homepage);
    }

    public List<Integer> getAllMenuIds() {
        return dssWorkspaceMenus.stream().map(DSSWorkspaceMenu::getId).collect(Collectors.toList());
    }

}
