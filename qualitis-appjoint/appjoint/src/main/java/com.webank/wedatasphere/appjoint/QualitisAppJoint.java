package com.webank.wedatasphere.appjoint;

import com.webank.wedatasphere.project.QualitisProjectServiceImpl;
import com.webank.wedatasphere.dss.appjoint.AppJoint;
import com.webank.wedatasphere.dss.appjoint.exception.AppJointErrorException;
import com.webank.wedatasphere.dss.appjoint.execution.NodeExecution;
import com.webank.wedatasphere.dss.appjoint.service.ProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author howeye
 */
public class QualitisAppJoint implements AppJoint {

    QualitisNodeExecution nodeExecution = null;
    QualitisProjectServiceImpl projectService = null;

    private static final Logger LOGGER = LoggerFactory.getLogger(QualitisAppJoint.class);

    @Override
    public String getAppJointName() {
        return "Qualitis";
    }

    @Override
    public void init(String s, Map<String, Object> map) throws AppJointErrorException {
        nodeExecution = new QualitisNodeExecution();
        nodeExecution.setBaseUrl(s);
        nodeExecution.init(map);

        projectService = new QualitisProjectServiceImpl();
        projectService.setBaseUrl(s);
        projectService.init(map);
    }

    @Override
    public NodeExecution getNodeExecution() {
        if (nodeExecution != null) {
            return nodeExecution;
        }
        LOGGER.error("NodeExecution is not inited, return null");
        return null;
    }

    @Override
    public ProjectService getProjectService() {
        if (projectService != null) {
            return projectService;
        }
        LOGGER.error("ProjectService is not inited, return null");
        return null;
    }
}
