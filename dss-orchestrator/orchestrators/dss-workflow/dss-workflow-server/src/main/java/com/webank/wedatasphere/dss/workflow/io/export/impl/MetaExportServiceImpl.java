/*
 *
 *  * Copyright 2019 WeBank
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.webank.wedatasphere.dss.workflow.io.export.impl;

import com.webank.wedatasphere.dss.common.utils.IoUtils;
import com.webank.wedatasphere.dss.workflow.common.entity.DSSFlow;
import com.webank.wedatasphere.dss.workflow.common.entity.DSSFlowRelation;
import com.webank.wedatasphere.dss.workflow.io.export.MetaExportService;
import com.webank.wedatasphere.dss.workflow.io.export.MetaWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.io.*;
import java.util.List;

@Service
public class MetaExportServiceImpl implements MetaExportService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());


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

    private InputStream exportFlowBaseInfo(List<DSSFlow> DSSFlows) throws IOException {

        return MetaWriter.of("dss_flow", DSSFlow.class).data(DSSFlows).write();

    }

    private void exportFlowRelation(List<DSSFlowRelation> flowRelations, OutputStream outputStream) throws IOException {

        MetaWriter.of("dss_flow_relation", DSSFlowRelation.class).data(flowRelations).write(outputStream);

    }

    private InputStream exportFlowRelation(List<DSSFlowRelation> flowRelations) throws IOException {

        return MetaWriter.of("dss_flow_relation", DSSFlowRelation.class).data(flowRelations).write();

    }

}
