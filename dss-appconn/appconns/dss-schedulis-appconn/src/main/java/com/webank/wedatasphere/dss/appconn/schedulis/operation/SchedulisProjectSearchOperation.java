package com.webank.wedatasphere.dss.appconn.schedulis.operation;

import com.google.gson.reflect.TypeToken;
import com.webank.wedatasphere.dss.appconn.schedulis.SchedulisAppConn;
import com.webank.wedatasphere.dss.appconn.schedulis.utils.SchedulisHttpUtils;
import com.webank.wedatasphere.dss.common.utils.DSSCommonUtils;
import com.webank.wedatasphere.dss.standard.app.structure.AbstractStructureOperation;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectSearchOperation;
import com.webank.wedatasphere.dss.standard.app.structure.project.ref.ProjectResponseRef;
import com.webank.wedatasphere.dss.standard.app.structure.project.ref.RefProjectContentRequestRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import org.apache.commons.lang3.BooleanUtils;

import java.util.HashMap;
import java.util.Map;

public class SchedulisProjectSearchOperation
        extends AbstractStructureOperation<RefProjectContentRequestRef.RefProjectContentRequestRefImpl, ProjectResponseRef>
        implements ProjectSearchOperation<RefProjectContentRequestRef.RefProjectContentRequestRefImpl> {

    private String queryUrl;

    @Override
    public ProjectResponseRef searchProject(RefProjectContentRequestRef.RefProjectContentRequestRefImpl requestRef) throws ExternalOperationFailedException {
        logger.info("begin to search Schedulis project , projectName is {}.", requestRef.getProjectName());

        Map<String, Object> params = new HashMap<>(2);
        params.put("project", requestRef.getProjectName());
        params.put("ajax", "fetchprojectflows");
        try {
            logger.info("request url from Schedulis is: {}.", queryUrl);
            String responseBody = SchedulisHttpUtils.getHttpGetResult(queryUrl, params, ssoRequestOperation, requestRef.getWorkspace());
            logger.info("responseBody from Schedulis is: {}.", responseBody);
            Map<String,Object> map = DSSCommonUtils.COMMON_GSON.fromJson(responseBody, new TypeToken<Map<String,Object>>(){}.getType());
            String errorInfo = (String) map.get("error");
            Boolean projectActive = (Boolean)map.get("projectActive");

            if (BooleanUtils.isFalse(projectActive)) { //项目是删除状态
                errorInfo += "（项目名称 "+requestRef.getProjectName()+" 在schedulis已被删除，请在schedulis中重新创建同名项目）";
                return ProjectResponseRef.newExternalBuilder().setErrorMsg(errorInfo).success();
            } else if (errorInfo != null){
                if (errorInfo.contains("Project " + requestRef.getProjectName() + " doesn't exist")){
                    errorInfo += "（项目名称 "+requestRef.getProjectName()+" 在schedulis不存在，请在schedulis中创建同名项目）";
                    return ProjectResponseRef.newExternalBuilder().setErrorMsg(errorInfo).success();
                } else if (errorInfo.contains("Permission denied. Need READ access")) {
                    errorInfo += "（在schedulis中已存在相同项目名称 "+requestRef.getProjectName()+" ，但当前登录用户没有权限操作该项目）";
                    return ProjectResponseRef.newExternalBuilder().setRefProjectId(DSSCommonUtils.parseToLong(map.get("projectId"))).setErrorMsg(errorInfo).success();
                } else {
                    //接口调用返回其他错误，如网络错误
                    return ProjectResponseRef.newExternalBuilder().error(errorInfo);
                }
            }
            return ProjectResponseRef.newExternalBuilder().setRefProjectId(DSSCommonUtils.parseToLong(map.get("projectId"))).success();
        } catch (Exception e) {
            throw new ExternalOperationFailedException(90117, "Failed to search Schedulis project name!", e);
        }
    }

    @Override
    protected String getAppConnName() {
        return SchedulisAppConn.SCHEDULIS_APPCONN_NAME;
    }

    @Override
    public void init() {
        super.init();
        queryUrl = mergeBaseUrl("manager");
    }

}
