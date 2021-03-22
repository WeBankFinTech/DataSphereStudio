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

package com.webank.wedatasphere.dss.common.entity;

import com.google.common.base.Objects;
import com.webank.wedatasphere.dss.common.entity.label.EnvEnum;
import com.webank.wedatasphere.dss.common.entity.label.EnvSequential;
import com.webank.wedatasphere.dss.common.entity.label.EnvSequentialLabel;

/**
 * Created by enjoyyin on 2020/9/9.
 * DSSLabel主要是用来标识微服务所处的环境信息标签
 * 如dev标识开发中心
 * prod标识生产中心
 * 标签的流转通过labelService模块进行提供
 * 按照多接口的设计方式,
 */
public class DSSLabel extends EnvSequentialLabel {


    protected String label;

    public DSSLabel(){
        //默认是dev
        super(new EnvSequential(EnvEnum.DEV));
    }

    public DSSLabel(String label){
        super(new EnvSequential(EnvEnum.valueOf(label.toUpperCase())));
    }



    public String getLabel() {
        return this.envSequential.getEnvEnum().getName();
    }


    public void setLabel(String label) {
        this.label = label;
        //为了兼容老代码,只能将其set
        this.envSequential = new EnvSequential(EnvEnum.valueOf(label.toUpperCase()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DSSLabel dssLabel = (DSSLabel) o;
        return Objects.equal(label, dssLabel.label);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(label);
    }
}
