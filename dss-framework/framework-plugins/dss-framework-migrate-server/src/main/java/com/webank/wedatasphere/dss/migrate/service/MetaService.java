package com.webank.wedatasphere.dss.migrate.service;


import com.webank.wedatasphere.dss.framework.project.entity.DSSProjectDO;
import com.webank.wedatasphere.dss.orchestrator.common.entity.DSSOrchestratorInfo;
import com.webank.wedatasphere.dss.workflow.common.entity.DSSFlow;
import com.webank.wedatasphere.dss.workflow.common.entity.DSSFlowRelation;

import java.io.IOException;
import java.util.List;

public interface MetaService {

    DSSProjectDO readProject(String basePath) throws IOException;

    List<DSSFlow> readFlow(String basePath) throws IOException;

    List<DSSOrchestratorInfo> readOrchestrator(String basePath) throws IOException;

    List<DSSFlowRelation> readFlowRelation(String basePath) throws IOException;

    void writeOrchestratorInfo(DSSOrchestratorInfo orchestratorInfo, String orcPath) throws IOException;

    void exportFlowBaseInfo(List<DSSFlow> allDSSFlows, List<DSSFlowRelation> allFlowRelations, String savePath) throws IOException;


}
