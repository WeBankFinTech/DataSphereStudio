package com.webank.wedatasphere.dss.orchestrator.server.job;

import com.webank.wedatasphere.dss.orchestrator.core.service.BMLService;
import com.webank.wedatasphere.dss.orchestrator.db.dao.OrchestratorCopyJobMapper;
import com.webank.wedatasphere.dss.orchestrator.db.dao.OrchestratorMapper;
import com.webank.wedatasphere.dss.orchestrator.publish.ExportDSSOrchestratorPlugin;
import com.webank.wedatasphere.dss.orchestrator.publish.ImportDSSOrchestratorPlugin;
import com.webank.wedatasphere.dss.orchestrator.server.service.OrchestratorFrameworkService;
import com.webank.wedatasphere.dss.workflow.common.parser.WorkFlowParser;
import org.springframework.beans.factory.annotation.Autowired;

public class OrchestratorCopyEnv {

    @Autowired
    private OrchestratorCopyJobMapper orchestratorCopyJobMapper;

    @Autowired
    private OrchestratorMapper orchestratorMapper;

    @Autowired
    private ExportService exportService;

    @Autowired
    private BMLService bmlService;

    @Autowired
    private WorkFlowParser workFlowParser;

    @Autowired
    private OrchestratorFrameworkService orchestratorFrameworkService;

    @Autowired
    private ExportDSSOrchestratorPlugin exportDSSOrchestratorPlugin;

    @Autowired
    private ImportDSSOrchestratorPlugin importDSSOrchestratorPlugin;

    public OrchestratorCopyJobMapper getOrchestratorCopyJobMapper() {
        return orchestratorCopyJobMapper;
    }

    public void setOrchestratorCopyJobMapper(OrchestratorCopyJobMapper orchestratorCopyJobMapper) {
        this.orchestratorCopyJobMapper = orchestratorCopyJobMapper;
    }

    public ExportService getExportService() {
        return exportService;
    }

    public void setExportService(ExportService exportService) {
        this.exportService = exportService;
    }

    public OrchestratorMapper getOrchestratorMapper() {
        return orchestratorMapper;
    }

    public void setOrchestratorMapper(OrchestratorMapper orchestratorMapper) {
        this.orchestratorMapper = orchestratorMapper;
    }

    public BMLService getBmlService() {
        return bmlService;
    }

    public void setBmlService(BMLService bmlService) {
        this.bmlService = bmlService;
    }

    public WorkFlowParser getWorkFlowParser() {
        return workFlowParser;
    }

    public void setWorkFlowParser(WorkFlowParser workFlowParser) {
        this.workFlowParser = workFlowParser;
    }

    public OrchestratorFrameworkService getOrchestratorFrameworkService() {
        return orchestratorFrameworkService;
    }

    public void setOrchestratorFrameworkService(OrchestratorFrameworkService orchestratorFrameworkService) {
        this.orchestratorFrameworkService = orchestratorFrameworkService;
    }

    public ExportDSSOrchestratorPlugin getExportDSSOrchestratorPlugin() {
        return exportDSSOrchestratorPlugin;
    }

    public void setExportDSSOrchestratorPlugin(ExportDSSOrchestratorPlugin exportDSSOrchestratorPlugin) {
        this.exportDSSOrchestratorPlugin = exportDSSOrchestratorPlugin;
    }

    public ImportDSSOrchestratorPlugin getImportDSSOrchestratorPlugin() {
        return importDSSOrchestratorPlugin;
    }

    public void setImportDSSOrchestratorPlugin(ImportDSSOrchestratorPlugin importDSSOrchestratorPlugin) {
        this.importDSSOrchestratorPlugin = importDSSOrchestratorPlugin;
    }
}
