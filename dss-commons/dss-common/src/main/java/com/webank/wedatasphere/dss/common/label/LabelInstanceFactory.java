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

package com.webank.wedatasphere.dss.common.label;

import org.apache.linkis.manager.label.entity.Label;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class LabelInstanceFactory {

    private static final Logger log = LoggerFactory.getLogger(LabelInstanceFactory.class);

    private Map<String, Label<?>> labels = null;

    public LabelInstanceFactory(Map<String, Object> labelParam) {
        labels = getLabels(labelParam);
    }

    private Map<String, Label<?>> getLabels(Map<String, Object> originalLabels) {
        Map<String, Object> originalLabelsMap = LabelKeyConvertor.labelKeyConvert(originalLabels);
        return LabelBuilder.buildLabel(originalLabelsMap);
    }

    public Map<String, DSSLabel> getLabelsMap() {
        HashMap<String, DSSLabel> resultLabels = new HashMap<>();
        labels.entrySet().stream().map(item -> resultLabels.put(item.getKey(), (DSSLabel) item.getValue()));
        return resultLabels;
    }

    public List<DSSLabel> getLabelList() {
        List<DSSLabel> dssLabelList = new ArrayList<>();
        labels.entrySet().stream().map(item -> dssLabelList.add((DSSLabel) (item.getValue())));
        return dssLabelList;
    }

    public EnvDSSLabel getEnvDssLabel() {
        return (EnvDSSLabel) labels.get(EnvDSSLabel.DSS_ENV_LABEL_KEY);
    }
}
