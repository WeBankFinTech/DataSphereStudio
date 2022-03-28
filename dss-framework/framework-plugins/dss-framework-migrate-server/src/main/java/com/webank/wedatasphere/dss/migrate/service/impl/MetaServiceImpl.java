package com.webank.wedatasphere.dss.migrate.service.impl;

import com.webank.wedatasphere.dss.common.entity.project.DSSProject;
import com.webank.wedatasphere.dss.common.utils.IoUtils;
import com.webank.wedatasphere.dss.framework.project.entity.DSSProjectDO;
import com.webank.wedatasphere.dss.migrate.service.MetaService;
import com.webank.wedatasphere.dss.orchestrator.common.entity.DSSOrchestratorInfo;
import com.webank.wedatasphere.dss.workflow.common.entity.DSSFlow;
import com.webank.wedatasphere.dss.workflow.common.entity.DSSFlowRelation;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

@Service
public class MetaServiceImpl implements MetaService {

    private static final String FILE_NAME = "meta.txt";

    @Override
    public DSSProjectDO readProject(String basePath) throws IOException {
        try (InputStream inputStream = generateInputstream(basePath)) {
            List<DSSProjectDO> dwsProjectList = MetaReader.of("dss_project", DSSProjectDO.class).read(inputStream);
            return dwsProjectList.get(0);
        }

    }

    @Override
    public List<DSSFlow> readFlow(String basePath) throws IOException {
        try (InputStream inputStream = generateInputstream(basePath)) {
            return MetaReader.of("dss_flow", DSSFlow.class).read(inputStream);
        }
    }

    @Override
    public List<DSSOrchestratorInfo> readOrchestrator(String basePath) throws IOException {
        try (InputStream inputStream = generateInputstream(basePath)) {
            return MetaReader.of("dss_orchestrator", DSSOrchestratorInfo.class).read(inputStream);
        }
    }

    @Override
    public List<DSSFlowRelation> readFlowRelation(String basePath) throws IOException {
        try (InputStream inputStream = generateInputstream(basePath)) {
            return MetaReader.of("dss_flow_relation", DSSFlowRelation.class).read(inputStream);
        }
    }


    @Override
    public void writeOrchestratorInfo(DSSOrchestratorInfo orchestratorInfo, String orcPath) throws IOException {
        export(orchestratorInfo,orcPath);

    }


    public void export(DSSOrchestratorInfo dssOrchestratorInfo, String savePath) throws IOException {

        try (
                OutputStream outputStream = generateOutputStream(savePath)
        ) {
            exportOrchestratorBaseInfo(dssOrchestratorInfo, outputStream);
        }
    }

    private void exportOrchestratorBaseInfo(DSSOrchestratorInfo dssOrchestratorInfo,OutputStream outputStream) throws IOException {

        MetaWriter.of("dss_orchestrator", DSSOrchestratorInfo.class).data(dssOrchestratorInfo).write(outputStream);

    }


    private InputStream generateInputstream(String basePath) throws IOException {
        return IoUtils.generateInputInputStream(basePath + File.separator + FILE_NAME);
    }

    private final String fileName = "meta.txt";


    @Override
    public void exportFlowBaseInfo(List<DSSFlow> allDSSFlows, List<DSSFlowRelation> allFlowRelations, String savePath) throws IOException {

        try (
                OutputStream outputStream = generateOutputStream(savePath)
        ) {
            exportFlowBaseInfo(allDSSFlows, outputStream);
            exportFlowRelation(allFlowRelations, outputStream);
        }
    }

    private OutputStream generateOutputStream(String basePath) throws IOException {
        return IoUtils.generateExportOutputStream(basePath + File.separator + fileName);
    }

    private void exportFlowBaseInfo(List<DSSFlow> DSSFlows, OutputStream outputStream) throws IOException {

        MetaWriter.of("dss_flow", DSSFlow.class).data(DSSFlows).write(outputStream);

    }

    private void exportFlowRelation(List<DSSFlowRelation> flowRelations, OutputStream outputStream) throws IOException {

        MetaWriter.of("dss_flow_relation", DSSFlowRelation.class).data(flowRelations).write(outputStream);

    }
}
