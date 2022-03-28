package com.webank.wedatasphere.dss.appconn.dolphinscheduler.operation;

import com.webank.wedatasphere.dss.appconn.dolphinscheduler.DolphinSchedulerAppConn;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.conf.DolphinSchedulerConf;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.ref.DolphinSchedulerDataResponseRef;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.ref.DolphinSchedulerPageInfoResponseRef;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.sso.DolphinSchedulerTokenManager;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.utils.DolphinSchedulerHttpUtils;
import com.webank.wedatasphere.dss.common.utils.MapUtils;
import com.webank.wedatasphere.dss.standard.app.structure.AbstractStructureOperation;
import com.webank.wedatasphere.dss.standard.app.structure.project.ref.ProjectUpdateRequestRef;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DolphinSchedulerProjectGrantOperation
        extends AbstractStructureOperation<ProjectUpdateRequestRef.ProjectUpdateRequestRefImpl, ResponseRef> {

    private String grantProjectUrl;

    private String authedProjectUrl;

    @Override
    protected String getAppConnName() {
        return DolphinSchedulerAppConn.DOLPHINSCHEDULER_APPCONN_NAME;
    }

    @Override
    public void init() {
        super.init();
        String baseUrl = DolphinSchedulerHttpUtils.getDolphinSchedulerBaseUrl(getBaseUrl());
        this.grantProjectUrl = mergeUrl(baseUrl, "users/grant-project");
        this.authedProjectUrl = mergeUrl(baseUrl, "projects/authed-project");
    }


    /**
     * Grant project.
     *
     * @throws ExternalOperationFailedException
     *             the external operation failed exception
     */
    public ResponseRef grantProject(ProjectUpdateRequestRef.ProjectUpdateRequestRefImpl requestRef)
        throws ExternalOperationFailedException {
        // 如果待授权用户不存在，则新建
        requestRef.getReleaseUsers().forEach(releaseUser -> {
            int userId = DolphinSchedulerTokenManager.getDolphinSchedulerTokenManager(getBaseUrl()).getUserId(releaseUser);
            List<Long> authedProjectIds = getAuthedProjectIds(userId);
            if(!authedProjectIds.contains(requestRef.getRefProjectId())) {
                authedProjectIds.add(requestRef.getRefProjectId());
                logger.info("try to grant access permission on project {} to user {}.", requestRef.getProjectName(), releaseUser);
                grantTo(userId, authedProjectIds);
            }
        });
        // 被去掉的已授权用户，需清除

        return ResponseRef.newExternalBuilder().success();
    }

    private void grantTo(int userId, List<Long> authProjectIds) {
        Map<String, Object> formData = MapUtils.newCommonMap("userId", userId, "projectIds", StringUtils.join(authProjectIds, ","));
        DolphinSchedulerHttpUtils.getHttpPostResult(ssoRequestOperation, grantProjectUrl, DolphinSchedulerConf.DS_ADMIN_USER.getValue(), formData);
    }

    private List<Long> getAuthedProjectIds(int userId) throws ExternalOperationFailedException {
        String url = this.authedProjectUrl + "?userId=" + userId;
        DolphinSchedulerDataResponseRef responseRef = DolphinSchedulerHttpUtils.getHttpGetResult(ssoRequestOperation, url, DolphinSchedulerConf.DS_ADMIN_USER.getValue());
        List<Map<String, Object>> projectList = responseRef.getData();
        logger.info("the authed project is: {}", projectList);
        return projectList.stream().map(project -> DolphinSchedulerHttpUtils.parseToLong(project.get("id"))).collect(Collectors.toList());
    }
}
