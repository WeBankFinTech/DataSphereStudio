package com.webank.wedatasphere.dss.appjoint.scheduler.airflow.service;


import com.webank.wedatasphere.dss.appjoint.exception.AppJointErrorException;
import com.webank.wedatasphere.dss.appjoint.scheduler.airflow.constant.AirflowConstant;
import com.webank.wedatasphere.dss.appjoint.scheduler.entity.SchedulerFlow;
import com.webank.wedatasphere.dss.appjoint.service.AppJointUrlImpl;
import com.webank.wedatasphere.dss.appjoint.scheduler.airflow.entity.AirflowSchedulerProject;
import com.webank.wedatasphere.dss.appjoint.scheduler.service.SchedulerProjectService;
import com.webank.wedatasphere.dss.appjoint.service.session.Session;
import com.webank.wedatasphere.dss.common.entity.flow.Flow;
import com.webank.wedatasphere.dss.common.entity.project.Project;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by cooperyang on 2019/9/16.
 */
public final class AirflowProjectService extends AppJointUrlImpl implements SchedulerProjectService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private String projectUrl;

    @Override
    public void setBaseUrl(String baseUrl) {
        this.projectUrl = baseUrl + "/manager";
    }

    @Override
    public List<Project> fetchProjects(Session session) {
        return null;
    }

    /**
     * parameters:
     * name = value
     * description=value
     *
     * @param project
     * @param session
     * @throws AppJointErrorException
     */
    @Override
    public Project createProject(Project project, Session session) throws AppJointErrorException {
        return null;
    }

    /**
     * delete=boolean
     * project=projectName
     *
     * @param project
     * @param session
     */
    @Override
    public void deleteProject(Project project, Session session) throws AppJointErrorException {
        logger.info("Delete project {} from airflow ...", project.getName());
        AirflowSchedulerProject publishProject = (AirflowSchedulerProject) project;
        for (SchedulerFlow flow : publishProject.getSchedulerFlows()) {
            deleteFlowExtra(project, flow, session);
        }
    }

    @Override
    public void publishProject(Project project, Session session) throws AppJointErrorException {
        AirflowSchedulerProject publishProject = (AirflowSchedulerProject) project;
        String projectPath = publishProject.getStorePath();

        if (publishProject.getSchedulerFlows() == null) {
            return;
        }

        List<String> publishedFlowNames = new ArrayList<>();
        Set<String> unPublishedFlowNames = publishProject.getSchedulerFlows().stream().map(x -> x.getName()).collect(Collectors.toSet());

        for (SchedulerFlow flow : publishProject.getSchedulerFlows()) {
            logger.info(String.format("Publishing flow %s of project %s ...", flow.getName(), project.getName()));
            String scheduleName = String.format(AirflowConstant.AIRFLOW_DAG_NAME_FORMAT, project.getName(), flow.getId());
            try {
                String filePath = projectPath + File.separator + flow.getName() + File.separator + scheduleName + AirflowConstant.PYTHON_FILE_NAME_SUFFIX;
                String dagContent = new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);
                // 替换user
                logger.info("Dag file content: replace '{}' with '{}'", AirflowConstant.AIRFLOW_SUBMIT_USER_PLACEHOLDER, session.getUser());
                dagContent = dagContent
                        .replace(AirflowConstant.AIRFLOW_SUBMIT_USER_PLACEHOLDER, session.getUser());

                boolean needPublishToAirflow = true;
                String remoteDagFileContent = null;
                try {
                    remoteDagFileContent = ((AirflowSession) session).getAirflowClient().getDagFileContent(project.getName(), flow.getId());
                    if (remoteDagFileContent != null) {
                        logger.info(String.format("New dag content size for %s: %d", scheduleName, dagContent.length()));
                        logger.info(String.format("Remote dag content size for %s: %d ", scheduleName, remoteDagFileContent.length()));
                        if (dagContent.length() == remoteDagFileContent.length() && dagContent.equals(remoteDagFileContent)) {
                            needPublishToAirflow = false;
                        }
                    }
                } catch (Exception e) {
                    logger.info(String.format("Get remote dag content failed %s, will ignore: %s", scheduleName,  e.getCause()));
                }
                if (!needPublishToAirflow) {
                    logger.info(String.format("Dag content unchanged, will not publish to airflow: %s", scheduleName));
                    continue;
                }

                unPublishedFlowNames.remove(flow.getName());

                logger.info("Dag file {} will be upload to airflow", filePath);
                ((AirflowSession)session).getAirflowClient().uploadJob(project.getName(), flow.getId(), dagContent);
                logger.info("Dag file {} has been uploaded successfully", filePath);

                try {
                    logger.info(String.format("Try to delete existed airflow backend db info for dag %s", scheduleName));
                    ((AirflowSession) session).getAirflowClient().deleteDag(scheduleName); // delete dag info from airflow backend db
                    // ((AirflowSession)session).getAirflowClient().unpause(scheduleName); // 如果job之前提交过，尝试让它重新运行
                } catch (Exception e) {
                    logger.error("", e);
                }

                publishedFlowNames.add(flow.getName());
                logger.info(String.format("Successfully published flow %s of project %s ...", flow.getName(), project.getName()));

            } catch (Exception ex) {
                logger.error(String.format("Fail to publish flow %s of project %s, will stop rest flow publish", flow.getName(), project.getName()), ex);
                String publishStatus = String.format("成功发布工作流: %s。未发布工作流: %s。失败工作流: %s。原因: ",
                        String.join(",", publishedFlowNames), String.join(",", unPublishedFlowNames), flow.getName());
                throw new AppJointErrorException(90012, publishStatus + ex.getMessage(), ex);
            }
        }

        logger.info("Successfully uploaded all airflow dags from project {}", project.getName());
    }

    @Override
    public void updateProject(Project project, Session session) throws AppJointErrorException {

    }

    @Override
    public void deleteFlowExtra(Project project, Flow flow, Session session) throws AppJointErrorException {
        String scheduleName = String.format(AirflowConstant.AIRFLOW_DAG_NAME_FORMAT, project.getName(), flow.getId());
        try {
            logger.info("Will delete airflow dag {}", scheduleName);
            try {
                logger.info(String.format("Pausing airflow dag %s", scheduleName));
                ((AirflowSession) session).getAirflowClient().pause(scheduleName);
            } catch (Exception e) {
                logger.error("", e);
            }

            try {
                logger.info(String.format("Deleting airflow dag file for %s", scheduleName));
                ((AirflowSession) session).getAirflowClient().deleteDagFile(project.getName(), flow.getId());
            } catch (Exception e) {
                logger.error("", e);
            }

            try {
                logger.info(String.format("Deleting airflow backend db info for dag %s", scheduleName));
                ((AirflowSession) session).getAirflowClient().deleteDag(scheduleName); // delete dag info from airflow backend db
            } catch (Exception e) {
                logger.error("", e);
            }

            logger.info("Successfully deleted airflow dag {}", scheduleName);
        } catch (Exception e) {
            logger.error("Fail to delete airflow dag " + scheduleName + ", reason", e);
        } finally {

        }
    }
}
