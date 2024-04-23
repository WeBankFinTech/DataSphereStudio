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

package com.webank.wedatasphere.dss.workflow.io.export.impl;

import com.webank.wedatasphere.dss.common.utils.DSSCommonUtils;
import com.webank.wedatasphere.dss.common.utils.IoUtils;
import com.webank.wedatasphere.dss.workflow.common.entity.DSSFlow;
import com.webank.wedatasphere.dss.workflow.common.entity.DSSFlowRelation;
import com.webank.wedatasphere.dss.workflow.io.export.MetaExportService;
import com.webank.wedatasphere.dss.workflow.io.export.MetaWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MetaExportServiceImpl implements MetaExportService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    public static final String FLOW_META_FILE_NAME = ".flowmeta";
    public static final String FLOW_META_KEY = "dss_flow";
    public static final String FLOW_RELATION_META_KEY = "dss_flow_relation";

    private final String fileName = "meta.txt";

    @Override
    public void exportFlowBaseInfoNew(List<DSSFlow> allDSSFlows, List<DSSFlowRelation> allFlowRelations, String savePath) throws IOException {

        try (
                OutputStream outputStream = IoUtils.generateExportOutputStream(savePath + File.separator + FLOW_META_FILE_NAME)
        ) {
            Map<String, Object> flowMetaMap = new HashMap<>(2);
            flowMetaMap.put(FLOW_META_KEY, allDSSFlows);
            flowMetaMap.put(FLOW_RELATION_META_KEY, allFlowRelations);
            String flowMetaStr = DSSCommonUtils.COMMON_GSON.toJson(flowMetaMap);
            flowMetaStr = DSSCommonUtils.prettyJson(flowMetaStr);
            org.apache.commons.io.IOUtils.write(flowMetaStr,outputStream,"UTF-8");
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

    private OutputStream generateOutputStream(String basePath) throws IOException {
        return IoUtils.generateExportOutputStream(basePath + File.separator + fileName);
    }

    private void exportFlowBaseInfo(List<DSSFlow> DSSFlows, OutputStream outputStream) throws IOException {

        MetaWriter.of("dss_flow", DSSFlow.class).data(DSSFlows).write(outputStream);

    }

    private InputStream exportFlowBaseInfo(List<DSSFlow> DSSFlows) throws IOException {

        return MetaWriter.of("dss_flow", DSSFlow.class).data(DSSFlows).write();

    }

    private void exportFlowRelation(List<DSSFlowRelation> flowRelations, OutputStream outputStream) throws IOException {

        MetaWriter.of("dss_workflow_relation", DSSFlowRelation.class).data(flowRelations).write(outputStream);

    }

    private InputStream exportFlowRelation(List<DSSFlowRelation> flowRelations) throws IOException {

        return MetaWriter.of("dss_workflow_relation", DSSFlowRelation.class).data(flowRelations).write();

    }

}
