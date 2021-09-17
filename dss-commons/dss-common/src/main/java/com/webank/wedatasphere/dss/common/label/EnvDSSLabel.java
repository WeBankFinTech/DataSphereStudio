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

import com.webank.wedatasphere.linkis.manager.label.builder.factory.LabelBuilderFactoryContext;
import com.webank.wedatasphere.linkis.manager.label.entity.Feature;
import com.webank.wedatasphere.linkis.manager.label.entity.annon.ValueSerialNum;
import java.util.HashMap;
import java.util.Map;

/**
 * Used to mark the env of this micro-services.
 */
public class EnvDSSLabel extends DSSLabel {

    public static final String DSS_ENV_LABEL_KEY = "DSSEnv";

    public EnvDSSLabel(String env) {
        setLabelKey(DSS_ENV_LABEL_KEY);
        setEnv(env);
    }

    public EnvDSSLabel() {
        setLabelKey(DSS_ENV_LABEL_KEY);
    }

    @Override
    public Feature getFeature() {
        return Feature.CORE;
    }

    public String getEnv() {
        if (null == getValue()) {
            return null;
        }
        return getValue().get(DSS_ENV_LABEL_KEY);
    }

    @ValueSerialNum(0)
    public void setEnv(String env) {
        if (null == getValue()) {
            setValue(new HashMap<>(1));
        }
        getValue().put(DSS_ENV_LABEL_KEY, env);
    }

    public static void main(String[] args) {
        Map<String, Object> map = new HashMap<>();
        map.put(DSS_ENV_LABEL_KEY, "dev");
        EnvDSSLabel label = (EnvDSSLabel) LabelBuilderFactoryContext.getLabelBuilderFactory().getLabels(map).get(0);
        System.out.println("label: " + label.getEnv());
    }

}
