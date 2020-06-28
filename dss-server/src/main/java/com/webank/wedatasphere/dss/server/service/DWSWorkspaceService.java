package com.webank.wedatasphere.dss.server.service;

import com.webank.wedatasphere.dss.server.dto.response.HomepageDemoMenuVo;
import com.webank.wedatasphere.dss.server.dto.response.HomepageVideoVo;
import com.webank.wedatasphere.dss.server.dto.response.OnestopMenuVo;
import com.webank.wedatasphere.dss.server.entity.DWSWorkspace;
import com.webank.wedatasphere.dss.server.dto.response.WorkspaceDepartmentVo;

import java.util.List;

/**
 * Created by schumiyi on 2020/6/22
 */
public interface DWSWorkspaceService {
    List<DWSWorkspace> getWorkspaces();

    Long addWorkspace(String userName, String name, String department, String label, String description);

    boolean existWorkspaceName(String name);

    List<WorkspaceDepartmentVo> getWorkSpaceDepartments();

    List<HomepageDemoMenuVo> getHomepageDemos(boolean isChinese);

    List<HomepageVideoVo> getHomepageVideos(boolean isChinese);

    List<OnestopMenuVo> getWorkspaceManagements(Long workspaceId, String username, boolean isChinese);

    List<OnestopMenuVo> getWorkspaceApplications(Long workspaceId, String username, boolean isChinese);
}
