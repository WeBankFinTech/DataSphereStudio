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

package com.webank.wedatasphere.dss.framework.appconn.entity;

import com.webank.wedatasphere.dss.appconn.manager.entity.AppInstanceInfo;

import java.io.Serializable;


public class AppInstanceBean implements AppInstanceInfo, Serializable {

    private static final long serialVersionUID=1L;

    private Long id;
    private Long appConnId;
    private String label;
    private String url;
    private String enhanceJson;
    private String homepageUri;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAppConnId() {
        return appConnId;
    }

    public void setAppConnId(Long appConnId) {
        this.appConnId = appConnId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String getEnhanceJson() {
        return enhanceJson;
    }

    public void setEnhanceJson(String enhanceJson) {
        this.enhanceJson = enhanceJson;
    }

    @Override
    public String getHomepageUri() {
        return homepageUri;
    }

    public void setHomepageUri(String homepageUri) {
        this.homepageUri = homepageUri;
    }

    @Override
    public String getLabels() {
        return label;
    }

    @Override
    public String toString() {
        return "AppInstanceBean{" +
                "id=" + id +
                ", appConnId=" + appConnId +
                ", label='" + label + '\'' +
                ", url='" + url + '\'' +
                ", enhanceJson='" + enhanceJson + '\'' +
                ", homepageUri='" + homepageUri + '\'' +
                '}';
    }
}