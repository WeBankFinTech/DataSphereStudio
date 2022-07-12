package com.webank.wedatasphere.dss.migrate.service;

import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.framework.project.entity.vo.DSSProjectVo;
import com.webank.wedatasphere.dss.orchestrator.common.entity.DSSOrchestratorInfo;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.workflow.common.entity.DSSFlow;

public interface MigrateService {


    void migrate(String userName, String inputZipPath, Workspace workspace) throws Exception;

    long importOrcToOrchestrator(String resourceId, String version, DSSProjectVo project,
                                        String username, String label, Workspace workspace, DSSOrchestratorInfo dssOrchestratorInfo);

    DSSOrchestratorInfo buildOrchestratorInfo(DSSFlow dssFlow, DSSProjectVo dssProject, Long workspaceId) throws DSSErrorException;

    String queryOrcUUIDByName(Long workspaceId,Long projectId,String orcName) throws DSSErrorException;
}
