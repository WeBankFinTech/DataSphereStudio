/*
 * Copyright 2019 WeBank
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.webank.wedatasphere.dss.orchestrator.publish.io.export.impl;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.webank.wedatasphere.dss.common.utils.DSSCommonUtils;
import com.webank.wedatasphere.dss.common.utils.IoUtils;
import com.webank.wedatasphere.dss.orchestrator.common.entity.DSSOrchestratorInfo;
import com.webank.wedatasphere.dss.orchestrator.publish.io.export.MetaExportService;
import com.webank.wedatasphere.dss.orchestrator.publish.io.export.MetaWriter;
import com.webank.wedatasphere.dss.workflow.common.entity.DSSFlow;
import com.webank.wedatasphere.dss.workflow.common.entity.DSSFlowRelation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.io.*;
import java.util.List;


@Service("orcMetaExportService")
public class MetaExportServiceImpl implements MetaExportService {

    public static final String ORCHESTRATOR_META_KEY = "dss_orchestrator";
    public static final String FLOW_META_FILE_NAME = ".flowmeta";
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private final String fileName = "meta.txt";


    @Override
    public void exportNew(DSSOrchestratorInfo dssOrchestratorInfo, String flowMetaPath) throws IOException {
        File flowMetaFile = new File(flowMetaPath + File.separator + FLOW_META_FILE_NAME);
        Gson gson = new Gson();

        if (flowMetaFile.exists()) {
            // 初始化JsonObject为null
            JsonObject jsonObject = null;
            // 先读取现有内容
            try (FileReader reader = new FileReader(flowMetaFile)) {
                jsonObject = new JsonParser().parse(reader).getAsJsonObject();
            } // try-with-resources会自动关闭reader
            // 更新JsonObject
            if (jsonObject != null) {
                jsonObject.add(ORCHESTRATOR_META_KEY, gson.toJsonTree(dssOrchestratorInfo));
            }

            // 写回修改后的内容
            try (FileWriter writer = new FileWriter(flowMetaFile)) {
                String jsonStr = gson.toJson(jsonObject);
                jsonStr = DSSCommonUtils.prettyJson(jsonStr);
                writer.write(jsonStr);
            }

            System.out.println("JSON文件已更新并保存。");
        } else {
            // 文件不存在，直接创建并写入orchestratorInfo信息
            try (FileWriter writer = new FileWriter(flowMetaFile)) {
                String jsonStr = gson.toJson(dssOrchestratorInfo);
                jsonStr = DSSCommonUtils.prettyJson(jsonStr);
                writer.write(jsonStr);
            }
        }
    }
    @Override
    public void export(DSSOrchestratorInfo dssOrchestratorInfo, String savePath) throws IOException {

        try (
                OutputStream outputStream = generateOutputStream(savePath)
        ) {
            exportOrchestratorBaseInfo(dssOrchestratorInfo, outputStream);
        }
    }

    @Override
    public void exportFlowBaseInfo(List<DSSFlow> allDSSFlows, List<DSSFlowRelation> allFlowRelations, String savePath) throws IOException {

        try (
                OutputStream outputStream = generateOutputStream(savePath)
        ) {
            exportFlowBaseInfo(allDSSFlows, outputStream);
            exportFlowRelation(allFlowRelations, outputStream);
        }

    }

    private void exportFlowBaseInfo(List<DSSFlow> DSSFlows, OutputStream outputStream) throws IOException {

        MetaWriter.of("dss_flow", DSSFlow.class).data(DSSFlows).write(outputStream);

    }

    private void exportFlowRelation(List<DSSFlowRelation> flowRelations, OutputStream outputStream) throws IOException {

        MetaWriter.of("dss_workflow_relation", DSSFlowRelation.class).data(flowRelations).write(outputStream);

    }


    private OutputStream generateOutputStream(String basePath) throws IOException {
        return IoUtils.generateExportOutputStream(basePath + File.separator + fileName);
    }


    private void exportOrchestratorBaseInfo(DSSOrchestratorInfo dssOrchestratorInfo,OutputStream outputStream) throws IOException {

        MetaWriter.of("dss_orchestrator", DSSOrchestratorInfo.class).data(dssOrchestratorInfo).write(outputStream);

    }

}
