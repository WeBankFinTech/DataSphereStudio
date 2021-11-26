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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: jinyangrao on 2021/7/16
 * @description:
 */
public class LabelKeyConvertor {

    private static final Logger log = LoggerFactory.getLogger(LabelKeyConvertor.class);


    public static String ROUTE_LABEL_KEY = "route";

    private static final Map<String, String> convertedlabels = new HashMap<>();

    static {
        convertedlabels.put(ROUTE_LABEL_KEY, EnvDSSLabel.DSS_ENV_LABEL_KEY);
    }

    private static String getRealLabelKey(String originalLabelKey) {
        return convertedlabels.get(originalLabelKey);
    }

    public static Map<String, Object> labelKeyConvert(Map<String, Object> labels) {
        Map<String, Object> finalLabels = new HashMap<>();
        for (Map.Entry<String, Object> item : labels.entrySet()) {
            String realLabelKey = LabelKeyConvertor.getRealLabelKey(item.getKey());
            finalLabels.put(realLabelKey, item.getValue());
        }
        log.info("Get labels map: {}", finalLabels);
        return finalLabels;
    }
}
