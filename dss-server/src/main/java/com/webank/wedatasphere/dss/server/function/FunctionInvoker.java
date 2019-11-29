/*
 * Copyright 2019 WeBank
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
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

package com.webank.wedatasphere.dss.server.function;

import com.webank.wedatasphere.dss.appjoint.AppJoint;
import com.webank.wedatasphere.dss.appjoint.exception.AppJointErrorException;
import com.webank.wedatasphere.dss.appjoint.execution.core.AppJointNode;
import com.webank.wedatasphere.dss.appjoint.service.NodeService;
import com.webank.wedatasphere.dss.appjoint.service.SecurityService;
import com.webank.wedatasphere.dss.appjoint.service.session.Session;
import com.webank.wedatasphere.dss.application.entity.Application;
import com.webank.wedatasphere.dss.application.service.ApplicationService;
import com.webank.wedatasphere.dss.common.entity.project.DWSProject;
import com.webank.wedatasphere.dss.common.entity.project.Project;
import com.webank.wedatasphere.dss.server.dao.ProjectMapper;
import org.apache.commons.math3.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Component
public class FunctionInvoker {

    @Autowired
    private ApplicationService applicationService;
    @Autowired
    private ProjectMapper projectMapper;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public Map<String,Object> nodeServiceFunction(String userName, Map<String,Object> requestBody, AppJointNode node, NodeServiceFunction function) throws AppJointErrorException {
        AppJoint appJoint = applicationService.getAppjoint(node.getNodeType());
        SecurityService securityService = appJoint.getSecurityService();
        Session session = null;
        Map<String,Object> jobContent = null;
        if(securityService != null){
            logger.info("appjoint securityService is exist,{}login",userName);
            session = securityService.login(userName);
        }
        NodeService nodeService = appJoint.getNodeService();
        if(nodeService != null){
            logger.info("appJoint NodeService is exist");
            jobContent = function.accept(nodeService,session, node, requestBody);
        }
        return jobContent;
    }

    public List<Pair<Project,String>> projectServiceAddFunction(DWSProject project, ProjectServiceAddFunction projectServiceAddFunction, List<AppJoint> appJoints) throws AppJointErrorException {
        ArrayList<Pair<Project,String>> projects = new ArrayList<>();
        for (AppJoint appJoint : appJoints) {
            Project appJointProject = null;
            Session session = null;
            if (appJoint.getSecurityService() != null) {
                logger.info("[addProject]securityService is exist,{}login...", project.getUserName());
                session = appJoint.getSecurityService().login(project.getUserName());
            }
            if (appJoint.getProjectService() != null) {
                logger.info("[addProject]projectService is exist");
                appJointProject = projectServiceAddFunction.accept(appJoint.getProjectService(), project, session);
                if(appJointProject != null) projects.add(new Pair<Project, String>(appJointProject,appJoint.getAppJointName()));
            }
        }
        return projects;
    }

    public void projectServiceFunction(DWSProject project, ProjectServiceFunction projectServiceFunction, List<AppJoint> appJoints) throws AppJointErrorException {
        for (AppJoint appJoint : appJoints) {
            Session session = null;
            if(appJoint.getSecurityService() != null){
                logger.info("securityService is exist,{}login..",project.getUserName());
                session = appJoint.getSecurityService().login(project.getUserName());
            }
            //通过projectID获取到appjointID
            Application application = applicationService.getApplication(appJoint.getAppJointName());
            Long appjointProjectID = projectMapper.getAppjointProjectID(project.getId(), application.getId());
            if(appjointProjectID != null) project.setId(appjointProjectID);
            if(appJoint.getProjectService() != null){
                logger.info("projectService is exist");
                projectServiceFunction.accept(appJoint.getProjectService(),project,session);
            }
        }
    }
}
