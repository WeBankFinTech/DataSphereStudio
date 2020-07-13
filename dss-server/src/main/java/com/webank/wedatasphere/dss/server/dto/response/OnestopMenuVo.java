/*
 * Copyright 2019 WeBank
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
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

package com.webank.wedatasphere.dss.server.dto.response;

import java.util.List;

/**
 * Created by schumiyi on 2020/6/24
 */
public class OnestopMenuVo {
    private Long id;
    private String title;
    private Integer order;
    private List<OnestopMenuAppInstanceVo> appInstances;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public List<OnestopMenuAppInstanceVo> getAppInstances() {
        return appInstances;
    }

    public void setAppInstances(List<OnestopMenuAppInstanceVo> appInstances) {
        this.appInstances = appInstances;
    }
}
