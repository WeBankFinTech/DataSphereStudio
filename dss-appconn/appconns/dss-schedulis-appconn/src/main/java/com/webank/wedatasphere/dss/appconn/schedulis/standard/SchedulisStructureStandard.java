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

package com.webank.wedatasphere.dss.appconn.schedulis.standard;

import com.webank.wedatasphere.dss.appconn.core.AppConn;
import com.webank.wedatasphere.dss.appconn.schedule.core.standard.SchedulerStructureStandard;
import com.webank.wedatasphere.dss.appconn.schedulis.service.*;
import com.webank.wedatasphere.dss.standard.app.development.publish.RefSchedulerService;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectService;
import com.webank.wedatasphere.dss.standard.app.structure.role.RoleService;
import com.webank.wedatasphere.dss.standard.app.structure.status.AppStatusService;
import com.webank.wedatasphere.dss.standard.common.desc.AppDesc;
import com.webank.wedatasphere.dss.standard.common.exception.AppStandardErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * created by cooperyang on 2020/11/11
 * Description: Schedulis的工程集成规范跑，单例的
 */
public class SchedulisStructureStandard implements SchedulerStructureStandard {


    private static final Logger LOGGER = LoggerFactory.getLogger(SchedulisStructureStandard.class);

    private volatile static SchedulisStructureStandard instance;

    private AppConn appConn;




    private SchedulisStructureStandard(AppConn appConn){
        try {
            this.appConn = appConn;
            init();
        } catch (AppStandardErrorException e) {
            LOGGER.error("Failed to init {}", this.getClass().getSimpleName(), e);
        }
    }

    public static SchedulisStructureStandard getInstance(AppConn appConn){
        if(instance == null){
            synchronized (SchedulisStructureStandard.class){
                if (instance == null){
                    instance = new SchedulisStructureStandard(appConn);
                }
            }
        }
        return instance;
    }


    @Override
    public RoleService getRoleService() {
        AppDesc appDesc = appConn.getAppDesc();
        return (RoleService) ServiceBuilder.getInstance().
                getSchedulisStructureService(appDesc.getAppInstances().get(0), appDesc, this, SchedulisRoleService.class);
    }

    @Override
    public ProjectService getProjectService() {
        AppDesc appDesc = appConn.getAppDesc();
        return (ProjectService) ServiceBuilder.getInstance().
                getSchedulisStructureService(appDesc.getAppInstances().get(0), appDesc, this, SchedulisProjectService.class);
    }

    @Override
    public AppStatusService getAppStateService() {
        AppDesc appDesc = appConn.getAppDesc();
        return (AppStatusService) ServiceBuilder.getInstance().
                getSchedulisStructureService(null, appDesc, this, SchedulisRoleService.class);
    }

    @Override
    public String getStandardName() {
        return null;
    }

    @Override
    public int getGrade() {
        return 0;
    }

    @Override
    public boolean isNecessary() {
        return false;
    }

    @Override
    public AppDesc getAppDesc() {
        return this.appConn.getAppDesc();
    }

    @Override
    public void setAppDesc(AppDesc appDesc) {
        //do nothing
    }

    @Override
    public void init() throws AppStandardErrorException {

    }

    @Override
    public void close() throws IOException {

    }


    @Override
    public RefSchedulerService getSchedulerService() {
        return new SchedulisRefSchedulerService();
    }


    public SchedulerInfoService getSchedulerInfoService(){
        return SchedulerInfoService.getInstance(this.getAppDesc());
    }

    public ScheduleFlowService getScheduleFlowService(){
        return ScheduleFlowService.getInstance(this.getAppDesc());
    }



}
