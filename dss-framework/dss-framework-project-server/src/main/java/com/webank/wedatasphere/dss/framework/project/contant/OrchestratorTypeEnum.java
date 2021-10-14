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

package com.webank.wedatasphere.dss.framework.project.contant;

import com.webank.wedatasphere.dss.orchestrator.core.type.OrchestratorKindEnum;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;

public enum OrchestratorTypeEnum {

    WORKFLOW("'pom_work_flow'", 1, "工作流"),
    SINGLE_TASK("'pom_single_task'",2,  "单任务"),
    COMBINED("'pom_consist_orchestrator'",3,  "组合编排");

    private String key;
    private Integer type;
    private String name;

    OrchestratorTypeEnum(String key, Integer type, String name) {
        this.key = key;
        this.type = type;
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static Integer getTypeByKey(String key){
        return Arrays.stream(OrchestratorTypeEnum.values())
                .filter(a -> a.getKey().equals(key))
                .map(OrchestratorTypeEnum::getType)
                .findFirst()
                .orElse(0);
    }

    public static String getKeyByOrcType(String type) {
        int index = Arrays.stream(OrchestratorKindEnum.values())
                .filter(a -> a.getName().equals(type))
                .map(OrchestratorKindEnum::getIndex)
                .findFirst()
                .orElse(0);
        String key = Arrays.stream(OrchestratorTypeEnum.values())
                .filter(a -> a.getType().equals(index))
                .map(OrchestratorTypeEnum::getKey)
                .findFirst()
                .orElse(null);
        return StringUtils.isNotBlank(key) ? key.replace("'", "") : null;
    }



}
