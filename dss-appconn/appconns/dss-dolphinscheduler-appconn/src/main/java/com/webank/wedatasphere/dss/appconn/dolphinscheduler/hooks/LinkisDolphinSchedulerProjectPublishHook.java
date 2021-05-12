package com.webank.wedatasphere.dss.appconn.dolphinscheduler.hooks;

import com.webank.wedatasphere.dss.appconn.dolphinscheduler.entity.DolphinSchedulerProject;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.utils.DolphinSchedulerUtilsScala;
import com.webank.wedatasphere.dss.appconn.schedule.core.entity.SchedulerFlow;
import com.webank.wedatasphere.dss.appconn.schedule.core.entity.SchedulerProject;
import com.webank.wedatasphere.dss.appconn.schedule.core.hooks.AbstractProjectPublishHook;
import com.webank.wedatasphere.dss.appconn.schedule.core.hooks.FlowPublishHook;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Linkis dolphin scheduler project publish hook.
 *
 * @author yuxin.yuan
 * @date 2021/04/29
 */
public class LinkisDolphinSchedulerProjectPublishHook extends AbstractProjectPublishHook {

    private static final Logger logger = LoggerFactory.getLogger(LinkisDolphinSchedulerProjectPublishHook.class);

    public LinkisDolphinSchedulerProjectPublishHook() {
        FlowPublishHook[] flowPublishHooks = {new LinkisDolphinSchedulerFlowPublishHook()};
        setFlowPublishHooks(flowPublishHooks);
    }

    @Override
    public void setFlowPublishHooks(FlowPublishHook[] flowPublishHooks) {
        super.setFlowPublishHooks(flowPublishHooks);
    }

    @Override
    public void prePublish(SchedulerProject project) throws DSSErrorException {
        //1.检查重复的节点名
        DolphinSchedulerProject publishProject = (DolphinSchedulerProject)project;
        List<SchedulerFlow> allSchedulerFlows = publishProject.getSchedulerFlows();
        List<String> repeatNode = DolphinSchedulerUtilsScala.getRepeatNodeName(getAllNodeName(allSchedulerFlows));
        if (repeatNode.size() > 0) {
            throw new DSSErrorException(80001, "重复的节点名称：" + repeatNode.toString());
        }

        // TODO: 2021/4/29 检查工程是否存在
        // 2.处理资源
        //        writeProjectResourcesToLocal(publishProject);
        //3.调用flowPublishHooks
        super.prePublish(project);
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

    /**
     * 获取所有节点的名字，免去一次递归.
     *
     * @param allSchedulerFlows the all scheduler flows
     * @return all node name
     */
    public List<String> getAllNodeName(List<SchedulerFlow> allSchedulerFlows) {
        List<String> nodeNames = new ArrayList<>();
        allSchedulerFlows.forEach(flow -> flow.getSchedulerNodes().forEach(node -> nodeNames.add(node.getName())));
        return nodeNames;
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

    //    private String getFlowSchedulePropsValue(SchedulerFlow schedulerFlow, String propsKey) {
    //        String res = null;
    //        if (schedulerFlow.getFlowScheduleProperties() == null) {
    //            return null;
    //        }
    //        Map<String, Object> map = schedulerFlow.getFlowScheduleProperties();
    //        Object value = map.get(propsKey);
    //        if (value != null && StringUtils.isNotBlank(value.toString())) {
    //            res = value.toString().replace("\"", "");
    //        }
    //
    //        return res;
    //    }

}
