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

package com.webank.wedatasphere.dss.orchestrator.core.type;

/**
 * created by cooperyang on 2020/12/15
 * Description:
 */
public enum OrchestratorKindEnum {

    /**
     * 编排模式的多种类型
     */
    WORKFLOW(1, "workflow","工作流"),
    SINGLE_TASK(2, "singleTask", "单任务"),
    COMBINED(3,"combined", "组合编排");

    private OrchestratorKindEnum(int index, String name, String chName){
        this.index = index;
        this.name = name;
        this.chName = chName;
    }

    private int index;

    private String name;

    private String chName;

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    public String getChName() {
        return chName;
    }

    public static OrchestratorKindEnum getType(int index){
        for (OrchestratorKindEnum type : values()) {
            if (type.getIndex() == index) {
                return type;
            }
        }
        return WORKFLOW;
    }

}
