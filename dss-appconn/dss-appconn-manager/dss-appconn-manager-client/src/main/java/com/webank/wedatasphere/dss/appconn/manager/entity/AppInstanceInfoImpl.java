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

package com.webank.wedatasphere.dss.appconn.manager.entity;

public class AppInstanceInfoImpl implements AppInstanceInfo {

    private Long id;
    private String url;
    private String homepageUri;
    private String labels;
    private String enhanceJson;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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
        return labels;
    }

    public void setLabels(String labels) {
        this.labels = labels;
    }

    @Override
    public String getEnhanceJson() {
        return enhanceJson;
    }

    public void setEnhanceJson(String enhanceJson) {
        this.enhanceJson = enhanceJson;
    }

    @Override
    public String toString() {
        return "AppInstanceInfoImpl{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", homepageUrl='" + homepageUri + '\'' +
                ", labels='" + labels + '\'' +
                ", enhanceJson='" + enhanceJson + '\'' +
                '}';
    }
}
