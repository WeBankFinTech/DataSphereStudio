package com.webank.wedatasphere.dss.appconn.dolphinscheduler.operation;

import com.webank.wedatasphere.dss.appconn.dolphinscheduler.DolphinSchedulerAppConn;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.conf.DolphinSchedulerConf;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.ref.DolphinSchedulerDataResponseRef;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.sso.DolphinSchedulerTokenManager;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.utils.DolphinSchedulerHttpUtils;
import com.webank.wedatasphere.dss.common.utils.MapUtils;
import com.webank.wedatasphere.dss.standard.app.structure.AbstractStructureOperation;
import com.webank.wedatasphere.dss.standard.app.structure.project.ref.DSSProjectPrivilege;
import com.webank.wedatasphere.dss.standard.app.structure.project.ref.ProjectUpdateRequestRef;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
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
        BiConsumer<DSSProjectPrivilege, BiConsumer<List<Long>, String>> dealPrivilege = (privilege, authedProjectIdsConsumer) -> {
            if(privilege == null || CollectionUtils.isEmpty(privilege.getReleaseUsers())) {
                return;
            }
            privilege.getReleaseUsers().forEach(releaseUser -> {
                int userId = DolphinSchedulerTokenManager.getDolphinSchedulerTokenManager(getBaseUrl()).getUserId(releaseUser);
                List<Long> authedProjectIds = getAuthedProjectIds(userId);
                int originSize = authedProjectIds.size();
                authedProjectIdsConsumer.accept(authedProjectIds, releaseUser);
                if(originSize != authedProjectIds.size()) {
                    grantTo(userId, authedProjectIds);
                }
            });
        };
        // 如果待授权用户不存在，则新建
        dealPrivilege.accept(requestRef.getAddedDSSProjectPrivilege(), (authedProjectIds, releaseUser) -> {
            if (!authedProjectIds.contains(requestRef.getRefProjectId())) {
                logger.info("try to grant access permission on project {} to user {}.", requestRef.getProjectName(), releaseUser);
                authedProjectIds.add(requestRef.getRefProjectId());
            }
        });
        // 被去掉的已授权用户，需清除
        dealPrivilege.accept(requestRef.getRemovedDSSProjectPrivilege(), (authedProjectIds, releaseUser) -> {
            if(authedProjectIds.contains(requestRef.getRefProjectId())) {
                logger.info("try to un-grant access permission on project {} to user {}.", requestRef.getProjectName(), releaseUser);
                authedProjectIds.remove(requestRef.getRefProjectId());
            }
        });
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
