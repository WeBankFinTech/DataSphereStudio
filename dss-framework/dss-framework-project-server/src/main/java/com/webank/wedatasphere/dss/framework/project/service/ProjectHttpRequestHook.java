package com.webank.wedatasphere.dss.framework.project.service;

import com.webank.wedatasphere.dss.framework.project.entity.request.ProjectCreateRequest;
import com.webank.wedatasphere.dss.framework.project.entity.request.ProjectDeleteRequest;
import com.webank.wedatasphere.dss.framework.project.entity.request.ProjectModifyRequest;
import com.webank.wedatasphere.dss.framework.project.entity.request.ProjectQueryRequest;
import org.apache.linkis.server.Message;

import javax.servlet.http.HttpServletRequest;

/**
 * @author enjoyyin
 * @date 2022-09-21
 * @since 0.5.0
 */
public interface ProjectHttpRequestHook {

    /**
     * 如果前置操作失败，则返回 Message，否则返回 null即可
     * @param request
     * @param projectRequest
     * @return
     */
    Message beforeGetAllProjects(HttpServletRequest request, ProjectQueryRequest projectRequest);

    /**
     * 如果前置操作失败，则返回 Message，否则返回 null即可
     * @param request
     * @param projectCreateRequest
     * @return
     */
    Message beforeCreateProject(HttpServletRequest request, ProjectCreateRequest projectCreateRequest);

    /**
     * 如果前置操作失败，则返回 Message，否则返回 null即可
     * @param request
     * @param projectModifyRequest
     * @return
     */
    Message beforeModifyProject(HttpServletRequest request, ProjectModifyRequest projectModifyRequest);

    /**
     * 如果前置操作失败，则返回 Message，否则返回 null即可
     * @param request
     * @param projectDeleteRequest
     * @return
     */
    Message beforeDeleteProject(HttpServletRequest request, ProjectDeleteRequest projectDeleteRequest);

    /**
     * 如果前置操作失败，则返回 Message，否则返回 null即可
     * @param request
     * @param projectRequest
     * @return
     */
    Message beforeGetDeletedProject(HttpServletRequest request, ProjectQueryRequest projectRequest);
}
