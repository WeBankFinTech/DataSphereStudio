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
package com.webank.wedatasphere.dss.apiservice.core.vo;

import java.util.Date;
import java.util.List;

/**
 * @author jinyangrao
 */
public class ApiVersionVo {

    private Long id;
    private Long apiId;
    private String version;
    private String bmlResourceId;
    private String bmlVersion;
    private String source;
    private String creator;
    private Date createTime;
    private String metadataInfo;
    private String authId;


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    private int status;

    /**
     * one version data corresponds to multiple param data
     * */
    private List<ParamVo> paramVos;

    public List<ParamVo> getParamVos() {
        return paramVos;
    }

    public String getMetadataInfo() {
        return metadataInfo;
    }

    public String getAuthId() {
        return authId;
    }

    public void setAuthId(String authId) {
        this.authId = authId;
    }

    public void setMetadataInfo(String metadataInfo) {
        this.metadataInfo = metadataInfo;
    }

    public void setParamVos(List<ParamVo> paramVos) {
        this.paramVos = paramVos;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getApiId() {
        return apiId;
    }

    public void setApiId(Long apiId) {
        this.apiId = apiId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getBmlResourceId() {
        return bmlResourceId;
    }

    public void setBmlResourceId(String bmlResourceId) {
        this.bmlResourceId = bmlResourceId;
    }

    public String getBmlVersion() {
        return bmlVersion;
    }

    public void setBmlVersion(String bmlVersion) {
        this.bmlVersion = bmlVersion;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }


}
