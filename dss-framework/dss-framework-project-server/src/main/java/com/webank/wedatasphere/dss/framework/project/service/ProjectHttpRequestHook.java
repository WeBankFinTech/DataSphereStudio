package com.webank.wedatasphere.dss.framework.project.service;

import com.webank.wedatasphere.dss.framework.project.entity.request.ProjectCreateRequest;
import com.webank.wedatasphere.dss.framework.project.entity.request.ProjectDeleteRequest;
import com.webank.wedatasphere.dss.framework.project.entity.request.ProjectModifyRequest;
import com.webank.wedatasphere.dss.framework.project.entity.request.ProjectQueryRequest;
import com.webank.wedatasphere.dss.framework.project.entity.vo.DSSProjectVo;
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
     * @return 前置操作失败，请返回 Message，否则返回 null
     */
    Message beforeGetAllProjects(HttpServletRequest request, ProjectQueryRequest projectRequest);

    /**
     * 如果前置操作失败，则返回 Message，否则返回 null即可
     * @param request
     * @param projectCreateRequest
     * @return 前置操作失败，请返回 Message，否则返回 null
     */
    Message beforeCreateProject(HttpServletRequest request, ProjectCreateRequest projectCreateRequest);

    /**
     * 创建成功后的后置操作。
     * 请注意：我们忽略了创建失败的后置操作。
     * @param request
     * @param projectCreateRequest
     * @param dssProjectVo
     */
    default void afterCreateProject(HttpServletRequest request, ProjectCreateRequest projectCreateRequest, DSSProjectVo dssProjectVo){
    }

    /**
     * 如果前置操作失败，则返回 Message，否则返回 null即可
     * @param request
     * @param projectModifyRequest
     * @return 前置操作失败，请返回 Message，否则返回 null
     */
    Message beforeModifyProject(HttpServletRequest request, ProjectModifyRequest projectModifyRequest);

    /**
     * 更新成功后的后置操作。
     * 请注意：我们忽略了更新失败的后置操作。
     * @param request
     * @param projectModifyRequest
     */
    default void afterModifyProject(HttpServletRequest request, ProjectModifyRequest projectModifyRequest) {
    }
    /**
     * 如果前置操作失败，请返回 Message，否则返回 null即可
     * @param request
     * @param projectDeleteRequest
     * @return 前置操作失败，请返回 Message，否则返回 null
     */
    Message beforeDeleteProject(HttpServletRequest request, ProjectDeleteRequest projectDeleteRequest);

    /**
     * 更新成功后的后置操作。
     * 请注意：我们忽略了更新失败的后置操作。
     * @param request
     * @param projectDeleteRequest
     */
    default void afterDeleteProject(HttpServletRequest request, ProjectDeleteRequest projectDeleteRequest) {
    }
    /**
     * 如果前置操作失败，则返回 Message，否则返回 null即可
     * @param request
     * @param projectRequest
     * @return 前置操作失败，请返回 Message，否则返回 null
     */
    Message beforeGetDeletedProject(HttpServletRequest request, ProjectQueryRequest projectRequest);
}
