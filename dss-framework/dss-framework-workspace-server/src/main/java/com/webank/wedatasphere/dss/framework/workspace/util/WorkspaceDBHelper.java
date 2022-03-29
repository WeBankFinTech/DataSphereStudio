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


import com.webank.wedatasphere.dss.framework.workspace.bean.*;
import com.webank.wedatasphere.dss.framework.workspace.bean.vo.DSSWorkspaceRoleVO;
import com.webank.wedatasphere.dss.framework.workspace.constant.ApplicationConf;
import com.webank.wedatasphere.dss.framework.workspace.dao.DSSWorkspaceRoleMapper;
import org.apache.linkis.common.utils.Utils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class WorkspaceDBHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkspaceDBHelper.class);

    @Autowired
    private DSSWorkspaceRoleMapper dssWorkspaceRoleMapper;


    private List<DSSRole> dssRoles;

    private final Object lock = new Object();

    private List<DSSOnestopMenu> dssOnestopMenus;

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
                    dssOnestopMenus = dssWorkspaceRoleMapper.getOnestopMenus();
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
                                    findFirst().map(DSSApplicationBean::getHomepageUrl).orElse(defaultUrl);
                    String analyserUrl = dssApplicationBeans.stream().
                            filter(dssApplicationBean -> "linkis".equals(dssApplicationBean.getName().toLowerCase())).
                            findFirst().map(DSSApplicationBean::getHomepageUrl).orElse(defaultUrl);
                    dssHomepageNameMap.put("/workspace", "工作空间首页");
                    dssHomepageNameMap.put(developerUrl, "工作流开发");
                    dssHomepageNameMap.put(analyserUrl, "意书(Scriptis)");
                    dssHomepageNameMap.put(maintenanceUrl, "调度中心首页");
                }
            }
        }, 1, 1, TimeUnit.MINUTES);
    }

    public void retrieveFromDB() {
        synchronized (lock) {
            dssApplicationBeans = dssWorkspaceRoleMapper.getDSSAppConns();
            dssOnestopMenus = dssWorkspaceRoleMapper.getOnestopMenus();
            dssRoles = dssWorkspaceRoleMapper.getRoles();
        }
    }


    public List<DSSWorkspaceMenuRole> generateDefaultWorkspaceMenuRole(int workspaceId, String username) {
        List<DSSWorkspaceMenuRole> list = new ArrayList<>();
        Date date = new Date(System.currentTimeMillis());
        list.add(new DSSWorkspaceMenuRole(workspaceId, 2,1,1, date, username));
        list.add(new DSSWorkspaceMenuRole(workspaceId, 3,1,1, date, username));
        list.add(new DSSWorkspaceMenuRole(workspaceId, 28,1,1, date, username));

        list.add(new DSSWorkspaceMenuRole(workspaceId, 2,2,0, date, username));
        list.add(new DSSWorkspaceMenuRole(workspaceId, 3,2,0, date, username));
        list.add(new DSSWorkspaceMenuRole(workspaceId, 28,2,0, date, username));

        list.add(new DSSWorkspaceMenuRole(workspaceId, 2,3,1, date, username));
        list.add(new DSSWorkspaceMenuRole(workspaceId, 3,3,0, date, username));
        list.add(new DSSWorkspaceMenuRole(workspaceId, 28,3,0, date, username));

        list.add(new DSSWorkspaceMenuRole(workspaceId, 2,4,0, date, username));
        list.add(new DSSWorkspaceMenuRole(workspaceId, 3,4,1, date, username));
        list.add(new DSSWorkspaceMenuRole(workspaceId, 28,4,0, date, username));

        list.add(new DSSWorkspaceMenuRole(workspaceId, 2,5,0, date, username));
        list.add(new DSSWorkspaceMenuRole(workspaceId, 3,5,0, date, username));
        list.add(new DSSWorkspaceMenuRole(workspaceId, 28,5,0, date, username));

        list.add(new DSSWorkspaceMenuRole(workspaceId, 2,6,1, date, username));
        list.add(new DSSWorkspaceMenuRole(workspaceId, 3,6,1, date, username));
        list.add(new DSSWorkspaceMenuRole(workspaceId, 28,6,0, date, username));

        if(StringUtils.isNotBlank(ApplicationConf.ESB_APPID)){
            list.add(new DSSWorkspaceMenuRole(workspaceId, 1,1,1, date, username));
            list.add(new DSSWorkspaceMenuRole(workspaceId, 1,2,1, date, username));
            list.add(new DSSWorkspaceMenuRole(workspaceId, 1,3,0, date, username));
            list.add(new DSSWorkspaceMenuRole(workspaceId, 1,4,0, date, username));
            list.add(new DSSWorkspaceMenuRole(workspaceId, 1,5,0, date, username));
            list.add(new DSSWorkspaceMenuRole(workspaceId, 1,6,1, date, username));
        }
        return list;
    }


    public List<DSSWorkspaceHomepage> generateDefaultWorkspaceHomepage(int workspaceId, String username) {
        String defaultUrl = "/workspace";
        String maintenanceUrl = "/workspaceHome/scheduleCenter";
        String developerUrl =
                dssApplicationBeans.stream().
                        filter(dssApplicationBean -> "workflow".equals(dssApplicationBean.getName().toLowerCase())).
                        findFirst().map(DSSApplicationBean::getHomepageUrl).orElse(defaultUrl);
        String analyserUrl = dssApplicationBeans.stream().
                filter(dssApplicationBean -> "linkis".equals(dssApplicationBean.getName().toLowerCase())).
                findFirst().map(DSSApplicationBean::getHomepageUrl).orElse(defaultUrl);
        List<DSSWorkspaceHomepage> dssWorkspaceHomepages = new ArrayList<>();
        Date date = new Date(System.currentTimeMillis());
        dssWorkspaceHomepages.add(new DSSWorkspaceHomepage(workspaceId, 1, defaultUrl, date));
        dssWorkspaceHomepages.add(new DSSWorkspaceHomepage(workspaceId, 2, maintenanceUrl, date));
        dssWorkspaceHomepages.add(new DSSWorkspaceHomepage(workspaceId, 3, developerUrl, date));
        dssWorkspaceHomepages.add(new DSSWorkspaceHomepage(workspaceId, 4, analyserUrl, date));
        dssWorkspaceHomepages.add(new DSSWorkspaceHomepage(workspaceId, 5, defaultUrl, date));
        dssWorkspaceHomepages.add(new DSSWorkspaceHomepage(workspaceId, 6, defaultUrl, date));
        return dssWorkspaceHomepages;
    }


    public List<DSSWorkspaceComponentPriv> generateDefaultWorkspaceComponentPrivs(int workspaceId,
                                                                                  String username) {
        List<DSSWorkspaceComponentPriv> dssWorkspaceComponentPrivs = new ArrayList<>();
        Date updateTime = new Date(System.currentTimeMillis());
        //admin
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 1, 1, 1, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 1, 2, 1, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 1, 4, 1, updateTime, username));
        //运维
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 2, 1, 1, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 2, 2, 1, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 2, 4, 1, updateTime, username));
        //开发人员
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 3, 1, 1, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 3, 2, 1, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 3, 4, 1, updateTime, username));
        //分析人员
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 4, 1, 1, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 4, 2, 1, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 4, 4, 1, updateTime, username));
        //运营人员
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 5, 1, 1, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 5, 2, 1, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 5, 4, 1, updateTime, username));
        //领导
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 6, 1, 1, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 6, 2, 1, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 6, 4, 1, updateTime, username));
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


    public List<DSSRole> getRoles() {
        return this.dssRoles;
    }


    public List<DSSRole> getRoles(int workspaceId) {
        return this.dssRoles.stream().
                filter(dssRole -> dssRole.getWorkspaceId() == workspaceId || dssRole.getWorkspaceId() == -1).
                collect(Collectors.toList());
    }



    public String getRoleFrontName(int roleId) {
        DSSRole role = dssRoles.stream().filter(dssRole -> dssRole.getId() == roleId).findFirst().orElse(null);
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
                map(DSSRole::getId).
                orElse(-1);
    }


    public List<DSSWorkspaceRoleVO> getRoleVOs(int workspaceId) {
        return getRoles(workspaceId).stream().map(this::changeToRoleVO).collect(Collectors.toList());
    }

    private DSSWorkspaceRoleVO changeToRoleVO(DSSRole dssRole) {
        DSSWorkspaceRoleVO vo = new DSSWorkspaceRoleVO();
        vo.setRoleFrontName(dssRole.getFrontName());
        vo.setRoleId(dssRole.getId());
        vo.setRoleName(dssRole.getName());
        return vo;
    }

    public DSSOnestopMenu getMenuNameById(int menuId) {
        if (dssOnestopMenus.stream().anyMatch(dssMenu -> dssMenu.getId() == menuId)) {
            return dssOnestopMenus.stream().filter(dssMenu -> dssMenu.getId() == menuId).findFirst().get();
        } else {
            return null;
        }
    }


    public String getRoleNameById(int roleId) {
        return dssRoles.stream().filter(dssRole -> dssRole.getId() == roleId).findFirst().orElse(new DSSRole()).getName();
    }

    public DSSApplicationBean getAppConn(int appConnId) {
        return dssApplicationBeans.stream().
                filter(dssApplicationBean -> dssApplicationBean.getId() == appConnId).
                findFirst().
                orElse(null);
    }

    public DSSApplicationBean getAppConn(String appConnName) {
        return dssApplicationBeans.stream().
                filter(dssApplicationBean -> dssApplicationBean.getName() == appConnName).
                findFirst().
                orElse(null);
    }

    public List<Integer> getAppConnIds() {
        return dssApplicationBeans.stream().map(DSSApplicationBean::getId).collect(Collectors.toList());
    }

    public String getHomepageName(String homepage) {
        return this.dssHomepageNameMap.get(homepage);
    }

    public List<Integer> getAllMenuIds() {
        return dssOnestopMenus.stream().map(DSSOnestopMenu::getId).collect(Collectors.toList());
    }

}
