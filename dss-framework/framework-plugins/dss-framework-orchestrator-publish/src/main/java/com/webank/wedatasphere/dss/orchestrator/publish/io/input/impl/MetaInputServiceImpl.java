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

package com.webank.wedatasphere.dss.orchestrator.publish.io.input.impl;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.webank.wedatasphere.dss.common.exception.DSSRuntimeException;
import com.webank.wedatasphere.dss.common.utils.IoUtils;
import com.webank.wedatasphere.dss.orchestrator.common.entity.DSSOrchestratorInfo;
import com.webank.wedatasphere.dss.orchestrator.publish.io.input.MetaInputService;
import com.webank.wedatasphere.dss.orchestrator.publish.io.input.MetaReader;

import com.webank.wedatasphere.dss.workflow.common.entity.DSSFlow;
import com.webank.wedatasphere.dss.workflow.common.entity.DSSFlowRelation;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;

import static com.webank.wedatasphere.dss.orchestrator.publish.io.export.impl.MetaExportServiceImpl.*;


@Service("orcMetaInputService")
public class MetaInputServiceImpl implements MetaInputService {

    // TODO: 2020/3/13 防止表结构发生改变的version 字段的添加

    private final String fileName = "meta.txt";

    @Override
    public DSSOrchestratorInfo importOrchestratorNew( String flowMetaPath)  {
        File flowMetaFile = new File(flowMetaPath + File.separator + FLOW_META_FILE_NAME);
        JsonParser jsonParser = new JsonParser();
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(flowMetaFile)) {
            JsonObject jsonObject = jsonParser.parse(reader).getAsJsonObject();
            return gson.fromJson(jsonObject.get(ORCHESTRATOR_META_KEY), DSSOrchestratorInfo.class);
        }catch (IOException e){
            throw new DSSRuntimeException(100000, "read flowMeta file failed,path" + flowMetaPath, e);
        }
    }
    @Override
    public List<DSSOrchestratorInfo> importOrchestrator(String basePath) throws IOException {
        try (InputStream inputStream = generateInputstream(basePath)) {
            return MetaReader.of("dss_orchestrator", DSSOrchestratorInfo.class).read(inputStream);
        }
    }

    @Override
    public List<DSSFlow> inputFlow(String basePath) throws IOException {
        try (InputStream inputStream = generateInputstream(basePath)) {
            return MetaReader.of("dss_flow", DSSFlow.class).read(inputStream);
        }
    }

    @Override
    public List<DSSFlowRelation> inputFlowRelation(String basePath) throws IOException {
        try (InputStream inputStream = generateInputstream(basePath)) {
            return MetaReader.of("dss_workflow_relation", DSSFlowRelation.class).read(inputStream);
        }
    }

    /**
     * 获取inputStream
     *
     * @param basePath
     * @return
     * @throws FileNotFoundException
     */
    private InputStream generateInputstream(String basePath) throws IOException {
        return IoUtils.generateInputInputStream(basePath + File.separator + fileName);
    }


}
