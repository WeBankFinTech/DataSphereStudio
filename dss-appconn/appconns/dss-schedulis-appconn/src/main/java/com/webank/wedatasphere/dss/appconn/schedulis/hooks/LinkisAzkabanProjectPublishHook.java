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

package com.webank.wedatasphere.dss.appconn.schedulis.hooks;


import com.webank.wedatasphere.dss.appconn.schedulis.entity.AzkabanSchedulerProject;
import com.webank.wedatasphere.dss.appconn.schedule.core.entity.SchedulerFlow;
import com.webank.wedatasphere.dss.appconn.schedule.core.entity.SchedulerProject;
import com.webank.wedatasphere.dss.appconn.schedule.core.hooks.AbstractProjectPublishHook;
import com.webank.wedatasphere.dss.appconn.schedule.core.hooks.FlowPublishHook;
import com.webank.wedatasphere.dss.appconn.schedulis.utils.AzkabanUtilsScala;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by v_wbbmwan on  2019/9/18
 */

public class LinkisAzkabanProjectPublishHook extends AbstractProjectPublishHook {

    private static final Logger LOGGER = LoggerFactory.getLogger(LinkisAzkabanProjectPublishHook.class);

    public LinkisAzkabanProjectPublishHook() {
        FlowPublishHook[] flowPublishHooks = {new LinkisAzkabanFlowPublishHook()};
        setFlowPublishHooks(flowPublishHooks);
    }

    @Override
    public void setFlowPublishHooks(FlowPublishHook[] flowPublishHooks) {
        super.setFlowPublishHooks(flowPublishHooks);
    }

    @Override
    public void prePublish(SchedulerProject project) throws DSSErrorException {
        //1.检查重复的节点名
        AzkabanSchedulerProject publishProject = (AzkabanSchedulerProject) project;
        List<SchedulerFlow> allSchedulerFlows = publishProject.getSchedulerFlows();
        List<String> repeatNode = AzkabanUtilsScala.getRepeatNodeName(getAllNodeName(allSchedulerFlows));
        if (repeatNode.size() > 0) {
            throw new DSSErrorException(80001, "重复的节点名称：" + repeatNode.toString());
        }
        //删除可能未处理的zip包和文件夹
        removeProjectStoreDirAndzip(publishProject);
        // 2.处理资源
//        writeProjectResourcesToLocal(publishProject);
        //3.调用flowPublishHooks
        super.prePublish(project);
    }

    private void removeProjectStoreDirAndzip(AzkabanSchedulerProject publishProject) throws DSSErrorException {
        String storePath = publishProject.getStorePath();
        File projectDir = new File(storePath);
        try {
            if (projectDir.exists()) {
                LOGGER.info("exist project dir{} before publish ,now remove it", storePath);
                FileUtils.deleteDirectory(projectDir);
            }
            String projectZip = projectDir.getParent() + File.separator + publishProject.getName() + ".zip";
            File zipFile = new File(projectZip);
            if (zipFile.exists()) {
                LOGGER.info("exist project zip{} before publish ,now remove it", projectZip);
                zipFile.delete();
            }
        } catch (Exception e) {
            LOGGER.error("delete project dir or zip failed,reaseon:", e);
            throw new DSSErrorException(90020, e.getMessage());
        }
    }

//    private void writeProjectResourcesToLocal(AzkabanSchedulerProject publishProject) throws DSSErrorException {
//        List<Resource> resources = publishProject.getDSSProject().getProjectResources();
//        FileOutputStream os = null;
//        try {
//            String storePath = publishProject.getStorePath();
//            File projectDir = new File(storePath);
//            FileUtils.forceMkdir(projectDir);
//            File projectResourcesFile = new File(storePath, "project.properties");
//            projectResourcesFile.createNewFile();
//            if (resources == null || resources.isEmpty()) {
//                return;
//            }
//            String projectResourceStringPrefix = "project." + publishProject.getName() + ".resources=";
//            String projectResourceString = projectResourceStringPrefix + new Gson().toJson(resources) + "\n";
//            os = FileUtils.openOutputStream(projectResourcesFile);
//            os.write(projectResourceString.getBytes());
//        } catch (Exception e) {
//            LOGGER.error("write projectResources to local failed,reason:", e);
//            throw new DSSErrorException(90015, e.getMessage());
//        } finally {
//            IOUtils.closeQuietly(os);
//        }
//    }

    @Override
    public void postPublish(SchedulerProject project) throws DSSErrorException {
        // TODO: 2019/9/26  1.删除zip包和文件夹
        AzkabanSchedulerProject publishProject = (AzkabanSchedulerProject) project;
        String storePath = publishProject.getStorePath();
        String projectName = publishProject.getName();
        try {
            File file = new File(storePath);
            new File(file.getParent() + File.separator + projectName + ".zip").delete();
        } catch (Exception e) {
            LOGGER.error("删除发布后的zip包失败", e);
            throw new DSSErrorException(90001, "发布后删除工程目录失败");
        }
        // TODO: 2019/9/26  flowPublishHooks ...
//        //3.调用flowPublishHooks   更改了调度设置逻辑，这个调用先注释掉
//        AbstractSchedulerProject schedulerProject = (AbstractSchedulerProject) project;

//        List<SchedulerFlow> schedulerFlows = schedulerProject.getSchedulerFlows();
//        for (SchedulerFlow flow : schedulerFlows) {
//            if (flow.getRootFlow()) {
//                String scheduleTime = getFlowSchedulePropsValue(flow, SchedulerAppJointConstant.FLOW_SCHEDULE_TIME);
//                String alarmEmails = getFlowSchedulePropsValue(flow, SchedulerAppJointConstant.FLOW_SCHEDULE_ALARM_EMAILS);
//                String alarmLevel = getFlowSchedulePropsValue(flow, SchedulerAppJointConstant.FLOW_SCHEDULE_ALARM_LEVEL);
//                LOGGER.info("设置工作流调度参数为："+scheduleTime+","+alarmEmails+","+alarmLevel);
//                try {
//                    Session session = schedulerAppJoint.getSecurityService().login(userName);
//                    String scheduleId = sendFlowScheduleParams(projectName, flow.getName(), scheduleTime, schedulerAppJoint, session);
//                    if (scheduleId != null) {
//                        if (StringUtils.isNotEmpty(alarmEmails) && StringUtils.isNotEmpty(alarmLevel)) {
//                            sendFlowScheduleAlarmParams(scheduleId, flow.getName(), alarmEmails, alarmLevel, schedulerAppJoint, session);
//
//                        }
//                    }
//                } catch (AppJointErrorException e) {
//                    throw new DSSErrorException(90075, "设置调度和告警失败, 原因:" + e.getMessage());
//                }
//            }
//        }
        super.postPublish(project);
    }

//    private String sendFlowScheduleParams(String projectName, String flowName, String cronExpression, SchedulerAppJoint schedulerAppJoint, Session session) throws AppJointErrorException {
//        String scheduleId = null;
//        List<NameValuePair> params = new ArrayList<>();
//        if(cronExpression!= null) {
//            params.add(new BasicNameValuePair("ajax", "scheduleCronFlow"));
//            params.add(new BasicNameValuePair("projectName", projectName));
//            params.add(new BasicNameValuePair("flow", flowName));
//            params.add(new BasicNameValuePair("cronExpression", cronExpression));
//            AzkabanProjectService projectService = (AzkabanProjectService) schedulerAppJoint.getProjectService();
//            String baseUrl = projectService.getAzkabanBaseUrl();
//            String scheduleUrl = baseUrl + "/schedule";
//            HttpPost httpPost = new HttpPost(scheduleUrl);
//            /*httpPost.addHeader(HTTP.CONTENT_ENCODING, "UTF-8");*/
//            CookieStore cookieStore = new BasicCookieStore();
//            cookieStore.addCookie(session.getCookies()[0]);
//            HttpEntity entity = EntityBuilder.create().
//                    setContentType(ContentType.create("application/x-www-form-urlencoded", Consts.UTF_8))
//                    .setParameters(params).build();
//            httpPost.setEntity(entity);
//            CloseableHttpClient httpClient = null;
//            CloseableHttpResponse response = null;
//            try {
//                httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
//                response = httpClient.execute(httpPost);
//                HttpEntity ent = response.getEntity();
//                String entStr = IOUtils.toString(ent.getContent(), "utf-8");
//                LOGGER.info("设置工程调度 {}, azkaban 返回的信息是 {}", projectName + "-" + flowName, entStr);
//                String message = AzkabanUtils.handleAzkabanEntity(entStr);
//                if (!"success".equals(message)) {
//                    throw new AppJointErrorException(90077, "设置调度失败, 原因:" + message);
//                } else {
//                    int  id = (int)Double.parseDouble(AzkabanUtils.getValueFromEntity(entStr, "scheduleId"));
//                    scheduleId = Integer.toString(id);
//                    LOGGER.info("设置调度成功，工作流名为：{}, 调度ID:{} ", flowName, scheduleId);
//                }
//            } catch (Exception e) {
//                LOGGER.error("设置调度失败:", e);
//                throw new AppJointErrorException(90009, e.getMessage(), e);
//            } finally {
//                IOUtils.closeQuietly(response);
//                IOUtils.closeQuietly(httpClient);
//            }
//        }else{
//            LOGGER.info("设置工作流调度参数时间参数为空！");
//        }
//        return scheduleId;
//
//    }

//    private void sendFlowScheduleAlarmParams(String scheduleId, String flowName, String slaEmails, String alarmLevel, SchedulerAppJoint schedulerAppJoint, Session session) throws AppJointErrorException {
//
//        List<NameValuePair> params = new ArrayList<>();
//        params.add(new BasicNameValuePair("ajax", "setSla"));
//        params.add(new BasicNameValuePair("scheduleId", scheduleId));
//        params.add(new BasicNameValuePair("slaEmails", slaEmails));
//        String alarmParams = ",FAILURE EMAILS," + alarmLevel;
//        params.add(new BasicNameValuePair("finishSettings[0]", alarmParams));
//        AzkabanProjectService projectService = (AzkabanProjectService) schedulerAppJoint.getProjectService();
//        String baseUrl = projectService.getAzkabanBaseUrl();
//        String scheduleUrl = baseUrl + "/schedule";
//        HttpPost httpPost = new HttpPost(scheduleUrl);
//        /*httpPost.addHeader(HTTP.CONTENT_ENCODING, "UTF-8");*/
//        CookieStore cookieStore = new BasicCookieStore();
//        cookieStore.addCookie(session.getCookies()[0]);
//        HttpEntity entity = EntityBuilder.create().
//                setContentType(ContentType.create("application/x-www-form-urlencoded", Consts.UTF_8))
//                .setParameters(params).build();
//        httpPost.setEntity(entity);
//        CloseableHttpClient httpClient = null;
//        CloseableHttpResponse response = null;
//        try {
//            httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
//            response = httpClient.execute(httpPost);
//            HttpEntity ent = response.getEntity();
//            String entStr = IOUtils.toString(ent.getContent(), "utf-8");
//            LOGGER.info("设置工作流告警{}, azkaban 返回的信息是 {}", scheduleId + "-" + flowName, entStr);
//            String message = AzkabanUtils.handleAzkabanEntity(entStr);
//            if (!"success".equals(message)) {
//                throw new AppJointErrorException(90078, "设置调度告警失败, 原因:" + message);
//            } else {
//                LOGGER.info("设置调度告警成功，工作流名为： " + flowName);
//            }
//        } catch (Exception e) {
//            LOGGER.error("设置调度告警失败:", e);
//            throw new AppJointErrorException(90079, e.getMessage(), e);
//        } finally {
//            IOUtils.closeQuietly(response);
//            IOUtils.closeQuietly(httpClient);
//        }
//
//    }

    private String getFlowSchedulePropsValue(SchedulerFlow schedulerFlow, String propsKey) {
        String res = null;
        if (schedulerFlow.getFlowScheduleProperties()== null) {
            return null;
        }
        Map<String, Object> map = schedulerFlow.getFlowScheduleProperties();
            Object value = map.get(propsKey);
            if (value != null && StringUtils.isNotBlank(value.toString())) {
                   res = value.toString().replace("\"","");
               }

        return res;
    }

    /**
     * 直接从allFlows 中获取所有节点的名字，免去一次递归
     *
     * @param allSchedulerFlows
     * @return
     */
    public List<String> getAllNodeName(List<SchedulerFlow> allSchedulerFlows) {
        List<String> nodeNames = new ArrayList<>();
        allSchedulerFlows.forEach(flow -> flow.getSchedulerNodes().forEach(node -> nodeNames.add(node.getName())));
        return nodeNames;
    }


}
