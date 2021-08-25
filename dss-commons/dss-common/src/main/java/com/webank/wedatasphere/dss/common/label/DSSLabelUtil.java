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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 生成DSSLabel工具类
 */
public class DSSLabelUtil {
    /**
     * 临时构建标准labe map
     */
    private static Map<String, Object> consltructLabelMap(String label) {
        HashMap<String, Object> labelMap = new HashMap<>();
        labelMap.put(LabelKeyConvertor.ROUTE_LABEL_KEY, label);
        return labelMap;
    }

    /**
     * 生成DSSLabel list
     * @param label
     * @return
     */
    public static List<DSSLabel> createLabelList(String label){
        Map<String, Object> labelParam = consltructLabelMap(label);
        LabelInstanceFactory labelInstanceFactory = new LabelInstanceFactory(labelParam);
        List<DSSLabel> dssLabelList = labelInstanceFactory.getLabelList();
        return dssLabelList;
    }

    /**
     * 生成单一 DSSLabel
     * @param label
     * @return
     */
    public static EnvDSSLabel createLabel(String label){
        Map<String, Object> labelParam = consltructLabelMap(label);
        LabelInstanceFactory labelInstanceFactory = new LabelInstanceFactory(labelParam);
        EnvDSSLabel envDSSLabel = labelInstanceFactory.getEnvDssLabel();
        return envDSSLabel;
    }

    public static void main(String[] args) {
        List<DSSLabel> labelList = DSSLabelUtil.createLabelList("dev");
        System.out.println(labelList.size());
    }

}
