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

package com.webank.wedatasphere.dss.common.entity.label;

/**
 * created by cooperyang on 2021/1/12
 * Description: 环境的顺序标识
 */
public class EnvSequential implements Sequential<EnvSequential>{


    private EnvEnum envEnum;


    public EnvSequential(EnvEnum envEnum){
        super();
        this.envEnum = envEnum;
    }

    public EnvEnum getEnvEnum() {
        return envEnum;
    }

    @Override
    public boolean isNextSequential(EnvSequential sequential) {
        return sequential.envEnum.index == this.envEnum.index + 1;
    }

    @Override
    public boolean isPreviousSequential(EnvSequential sequential) {
        return sequential.envEnum.index == this.envEnum.index - 1;
    }

    @Override
    public EnvSequential getNextSequential() {
        return new EnvSequential(EnvEnum.getNext(this.envEnum));
    }

    @Override
    public EnvSequential getPreviousSequential() {
        return new EnvSequential(EnvEnum.getPrevious(this.envEnum));
    }
}
