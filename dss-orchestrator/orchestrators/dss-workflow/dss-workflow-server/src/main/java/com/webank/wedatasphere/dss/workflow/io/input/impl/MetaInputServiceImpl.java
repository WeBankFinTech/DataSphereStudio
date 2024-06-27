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

package com.webank.wedatasphere.dss.workflow.io.input.impl;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.webank.wedatasphere.dss.common.utils.IoUtils;
import com.webank.wedatasphere.dss.workflow.common.entity.DSSFlow;
import com.webank.wedatasphere.dss.workflow.common.entity.DSSFlowRelation;
import com.webank.wedatasphere.dss.workflow.io.input.MetaInputService;
import com.webank.wedatasphere.dss.workflow.io.input.MetaReader;
import org.apache.linkis.protocol.util.ImmutablePair;
import org.springframework.stereotype.Service;

import java.io.*;
import java.lang.reflect.Type;
import java.util.List;

import static com.webank.wedatasphere.dss.workflow.io.export.impl.MetaExportServiceImpl.*;


@Service
public class MetaInputServiceImpl implements MetaInputService {

    // TODO: 2020/3/13 防止表结构发生改变的version 字段的添加

    private final String fileName = "meta.txt";

    @Override
    public ImmutablePair<List<DSSFlow>,List<DSSFlowRelation>> inputFlowNew(String flowMetaPath) throws IOException {
        File flowMetaFile = new File(flowMetaPath + File.separator + FLOW_META_FILE_NAME);
        JsonParser jsonParser = new JsonParser();
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(flowMetaFile)) {
            Type flowListType = new TypeToken<List<DSSFlow>>() {}.getType();
            Type flowRelationListType = new TypeToken<List<DSSFlowRelation>>() {}.getType();
            JsonObject jsonObject = jsonParser.parse(reader).getAsJsonObject();
            List<DSSFlow> flows = gson.fromJson(jsonObject.get(FLOW_META_KEY), flowListType);
            List<DSSFlowRelation> flowRelations = gson.fromJson(jsonObject.get(FLOW_RELATION_META_KEY), flowRelationListType);
            return new ImmutablePair<>(flows, flowRelations);
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

    @Override
    public List<DSSFlowRelation> inputFlowRelation_for_0_x(String basePath) throws IOException {
        try (InputStream inputStream = generateInputstream(basePath)) {
            return MetaReader.of("dss_flow_relation", DSSFlowRelation.class).read(inputStream);
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
