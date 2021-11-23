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

package com.webank.wedatasphere.dss.framework.workspace.util;


import com.webank.wedatasphere.dss.framework.workspace.bean.DSSApplicationBean;
import com.webank.wedatasphere.dss.framework.workspace.bean.DSSOnestopMenu;
import com.webank.wedatasphere.dss.framework.workspace.bean.DSSRole;
import com.webank.wedatasphere.dss.framework.workspace.bean.DSSWorkspaceComponent;
import com.webank.wedatasphere.dss.framework.workspace.bean.DSSWorkspaceComponentPriv;
import com.webank.wedatasphere.dss.framework.workspace.bean.DSSWorkspaceHomepage;
import com.webank.wedatasphere.dss.framework.workspace.bean.DSSWorkspaceMenuComponentUrl;
import com.webank.wedatasphere.dss.framework.workspace.bean.DSSWorkspaceMenuRole;
import com.webank.wedatasphere.dss.framework.workspace.bean.vo.DSSWorkspaceRoleVO;
import com.webank.wedatasphere.dss.framework.workspace.dao.DSSWorkspaceRoleMapper;
import org.apache.linkis.common.utils.Utils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * created by cooperyang on 2020/3/17
 * Description:一个数据库的helper类，这个类可以将一些数据load到内存，比如menu信息，role的信息等
 */
@Component
public class WorkspaceDBHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkspaceDBHelper.class);

    @Autowired
    private DSSWorkspaceRoleMapper dssWorkspaceRoleMapper;


    private List<DSSRole> dssRoles;

    private final Object lock = new Object();

    private List<DSSOnestopMenu> dssOnestopMenus;

    private List<DSSWorkspaceMenuComponentUrl> dssWorkspaceMenuComponentUrls;

    private List<DSSWorkspaceComponent> dssWorkspaceComponents;

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
                    dssWorkspaceMenuComponentUrls = dssWorkspaceRoleMapper.getMenuComponentUrl();
                }
            }
        }, 0, 1, TimeUnit.MINUTES);

        Utils.defaultScheduler().scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                synchronized (lock) {
                    dssWorkspaceComponents = dssWorkspaceRoleMapper.getComponents();
                }
            }
        }, 0, 1, TimeUnit.MINUTES);

        Utils.defaultScheduler().scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                synchronized (lock) {
                    dssApplicationBeans = dssWorkspaceRoleMapper.getDSSApplications();
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
            dssApplicationBeans = dssWorkspaceRoleMapper.getDSSApplications();
            dssWorkspaceComponents = dssWorkspaceRoleMapper.getComponents();
            dssWorkspaceMenuComponentUrls = dssWorkspaceRoleMapper.getMenuComponentUrl();
            dssOnestopMenus = dssWorkspaceRoleMapper.getOnestopMenus();
            dssRoles = dssWorkspaceRoleMapper.getRoles();
        }
    }


    public List<DSSWorkspaceMenuRole> generateDefaultWorkspaceMenuRole(int workspaceId, String username) {
        List<DSSWorkspaceMenuRole> list = new ArrayList<>();
        Date date = new Date(System.currentTimeMillis());
        //管理员的权限,全部可以见
        // list.add(new DSSWorkspaceMenuRole(workspaceId, 1,1,1, date, username));
        list.add(new DSSWorkspaceMenuRole(workspaceId, 1, 1, 1, date, username));
        list.add(new DSSWorkspaceMenuRole(workspaceId, 2, 1, 1, date, username));
        list.add(new DSSWorkspaceMenuRole(workspaceId, 3, 1, 1, date, username));
        list.add(new DSSWorkspaceMenuRole(workspaceId, 4, 1, 1, date, username));
        list.add(new DSSWorkspaceMenuRole(workspaceId, 5, 1, 1, date, username));
        list.add(new DSSWorkspaceMenuRole(workspaceId, 6, 1, 1, date, username));
        list.add(new DSSWorkspaceMenuRole(workspaceId, 7, 1, 1, date, username));
        //运维用户
        list.add(new DSSWorkspaceMenuRole(workspaceId, 1, 2, 0, date, username));
        list.add(new DSSWorkspaceMenuRole(workspaceId, 2, 2, 1, date, username));
        list.add(new DSSWorkspaceMenuRole(workspaceId, 3, 2, 0, date, username));
        list.add(new DSSWorkspaceMenuRole(workspaceId, 4, 2, 0, date, username));
        list.add(new DSSWorkspaceMenuRole(workspaceId, 5, 2, 1, date, username));
        list.add(new DSSWorkspaceMenuRole(workspaceId, 6, 2, 1, date, username));
        list.add(new DSSWorkspaceMenuRole(workspaceId, 7, 2, 1, date, username));
        //开发用户
        list.add(new DSSWorkspaceMenuRole(workspaceId, 1, 3, 1, date, username));
        list.add(new DSSWorkspaceMenuRole(workspaceId, 2, 3, 1, date, username));
        list.add(new DSSWorkspaceMenuRole(workspaceId, 3, 3, 0, date, username));
        list.add(new DSSWorkspaceMenuRole(workspaceId, 4, 3, 0, date, username));
        list.add(new DSSWorkspaceMenuRole(workspaceId, 5, 3, 1, date, username));
        list.add(new DSSWorkspaceMenuRole(workspaceId, 6, 3, 1, date, username));
        list.add(new DSSWorkspaceMenuRole(workspaceId, 7, 3, 0, date, username));

        //分析用户
        list.add(new DSSWorkspaceMenuRole(workspaceId, 1, 4, 1, date, username));
        list.add(new DSSWorkspaceMenuRole(workspaceId, 2, 4, 0, date, username));
        list.add(new DSSWorkspaceMenuRole(workspaceId, 3, 4, 0, date, username));
        list.add(new DSSWorkspaceMenuRole(workspaceId, 4, 4, 1, date, username));
        list.add(new DSSWorkspaceMenuRole(workspaceId, 5, 4, 0, date, username));
        list.add(new DSSWorkspaceMenuRole(workspaceId, 6, 4, 0, date, username));
        list.add(new DSSWorkspaceMenuRole(workspaceId, 7, 4, 0, date, username));

        //运营用户
        list.add(new DSSWorkspaceMenuRole(workspaceId, 1, 5, 0, date, username));
        list.add(new DSSWorkspaceMenuRole(workspaceId, 2, 5, 0, date, username));
        list.add(new DSSWorkspaceMenuRole(workspaceId, 3, 5, 0, date, username));
        list.add(new DSSWorkspaceMenuRole(workspaceId, 4, 5, 1, date, username));
        list.add(new DSSWorkspaceMenuRole(workspaceId, 5, 5, 0, date, username));
        list.add(new DSSWorkspaceMenuRole(workspaceId, 6, 5, 0, date, username));
        list.add(new DSSWorkspaceMenuRole(workspaceId, 7, 5, 0, date, username));

        //数据服务
        list.add(new DSSWorkspaceMenuRole(workspaceId, 1, 6, 0, date, username));
        list.add(new DSSWorkspaceMenuRole(workspaceId, 2, 6, 0, date, username));
        list.add(new DSSWorkspaceMenuRole(workspaceId, 3, 6, 1, date, username));
        list.add(new DSSWorkspaceMenuRole(workspaceId, 4, 6, 0, date, username));
        list.add(new DSSWorkspaceMenuRole(workspaceId, 5, 6, 0, date, username));
        list.add(new DSSWorkspaceMenuRole(workspaceId, 6, 6, 0, date, username));
        list.add(new DSSWorkspaceMenuRole(workspaceId, 7, 6, 0, date, username));

        //访客
        list.add(new DSSWorkspaceMenuRole(workspaceId, 1, 7, 0, date, username));
        list.add(new DSSWorkspaceMenuRole(workspaceId, 2, 7, 0, date, username));
        list.add(new DSSWorkspaceMenuRole(workspaceId, 3, 7, 0, date, username));
        list.add(new DSSWorkspaceMenuRole(workspaceId, 4, 7, 0, date, username));
        list.add(new DSSWorkspaceMenuRole(workspaceId, 5, 7, 0, date, username));
        list.add(new DSSWorkspaceMenuRole(workspaceId, 6, 7, 0, date, username));
        list.add(new DSSWorkspaceMenuRole(workspaceId, 7, 7, 0, date, username));
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

      //管理员
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 1, 1, 1, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 1, 2, 1, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 1, 3, 1, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 1, 4, 1, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 1, 5, 1, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 1, 6, 1, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 1, 7, 1, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 1, 8, 1, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 1, 9, 1, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 1, 10, 1, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 1, 11, 1, updateTime, username));
        //运维

        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 2, 1, 0, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 2, 2, 1, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 2, 3, 1, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 2, 4, 0, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 2, 5, 0, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 2, 6, 0, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 2, 7, 0, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 2, 8, 0, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 2, 9, 1, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 2, 10, 1, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 2, 11, 0, updateTime, username));

        //开发
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 3, 1, 1, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 3, 2, 1, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 3, 3, 1, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 3, 4, 0, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 3, 5, 0, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 3, 6, 0, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 3, 7, 0, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 3, 8, 0, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 3, 9, 1, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 3, 10, 1, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 3, 11, 0, updateTime, username));

        //分析
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 4, 1, 0, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 4, 2, 0, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 4, 3, 0, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 4, 4, 0, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 4, 5, 1, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 4, 6, 1, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 4, 7, 1, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 4, 8, 1, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 4, 9, 0, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 4, 10, 0, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 4, 11, 0, updateTime, username));
        //运营
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 5, 1, 0, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 5, 2, 0, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 5, 3, 0, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 5, 4, 0, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 5, 5, 1, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 5, 6, 1, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 5, 7, 1, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 5, 8, 1, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 5, 9, 0, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 5, 10, 0, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 5, 11, 0, updateTime, username));

        //数据服务
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 6, 1, 0, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 6, 2, 0, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 6, 3, 0, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 6, 4, 0, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 6, 5, 1, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 6, 6, 0, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 6, 7, 0, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 6, 8, 0, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 6, 9, 0, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 6, 10, 0, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 6, 11, 0, updateTime, username));
        //访客
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 7, 1, 0, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 7, 2, 0, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 7, 3, 0, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 7, 4, 0, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 7, 5, 0, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 7, 6, 0, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 7, 7, 0, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 7, 8, 0, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 5, 9, 0, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 5, 10, 0, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 5, 11, 0, updateTime, username));


      /*  //admin
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 1, 1, 1, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 1, 2, 1, updateTime, username));
        //dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 1, 3, 1, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 1, 4, 1, updateTime, username));
        //运维
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 2, 1, 1, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 2, 2, 1, updateTime, username));
        //dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 2, 3, 1, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 2, 4, 1, updateTime, username));
        //开发人员
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 3, 1, 1, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 3, 2, 1, updateTime, username));
        //dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 3, 3, 1, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 3, 4, 1, updateTime, username));
        //分析人员
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 4, 1, 1, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 4, 2, 1, updateTime, username));
        //dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 4, 3, 1, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 4, 4, 1, updateTime, username));
        //运营人员
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 5, 1, 1, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 5, 2, 1, updateTime, username));
        //dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 5, 3, 1, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 5, 4, 1, updateTime, username));
        //领导
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 6, 1, 1, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 6, 2, 1, updateTime, username));
        //dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 6, 3, 1, updateTime, username));
        dssWorkspaceComponentPrivs.add(new DSSWorkspaceComponentPriv(workspaceId, 6, 4, 1, updateTime, username));*/

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


    public List<String> getComponentUrlsById(int componentId) {
        Optional<DSSWorkspaceMenuComponentUrl> optional = dssWorkspaceMenuComponentUrls.
                stream().filter(obj -> obj.getMenuId() == componentId).
                findFirst();
        if (optional.isPresent()) {
            int applicationId = optional.get().getDssApplicationId();
            String homepageUrl = dssWorkspaceRoleMapper.getEntryUrl(applicationId);
            return Collections.singletonList(homepageUrl);
        } else {
            return null;
        }
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

    public DSSApplicationBean getComponent(int componentId) {
        return dssApplicationBeans.stream().
                filter(dssApplicationBean -> dssApplicationBean.getId() == componentId).
                findFirst().
                orElse(null);
    }

    public String getHomepageName(String homepage) {
        return this.dssHomepageNameMap.get(homepage);
    }

    public List<Integer> getAllMenuIds() {
        return dssOnestopMenus.stream().map(DSSOnestopMenu::getId).collect(Collectors.toList());
    }

    public List<Integer> getAllComponentIds() {
        return dssWorkspaceComponents.stream().map(DSSWorkspaceComponent::getId).collect(Collectors.toList());
    }

}
