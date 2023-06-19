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

package com.webank.wedatasphere.dss.standard.common.desc;

import com.google.common.base.Objects;
import com.webank.wedatasphere.dss.common.label.DSSLabel;

import java.util.List;
import java.util.Map;


public class AppInstanceImpl implements AppInstance {

    private Long id;
    private String baseUrl;
    private String homepageUri;
    private Map<String, Object> config;
    private List<DSSLabel> labels;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getBaseUrl() {
        return baseUrl;
    }

    @Override
    public String getHomepageUri() {
        return homepageUri;
    }

    public void setHomepageUri(String homepageUri) {
        this.homepageUri = homepageUri;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public Map<String, Object> getConfig() {
        return config;
    }

    public void setConfig(Map<String, Object> config) {
        this.config = config;
    }

    @Override
    public List<DSSLabel> getLabels() {
        return labels;
    }

    public void setLabels(List<DSSLabel> labels) {
        this.labels = labels;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AppInstanceImpl that = (AppInstanceImpl) o;
        return Objects.equal(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "AppInstanceImpl{" +
                "id=" + id +
                ", baseUrl='" + baseUrl + '\'' +
                ", homepageUri='" + homepageUri + '\'' +
                ", config=" + config +
                ", labels=" + labels +
                '}';
    }
}
