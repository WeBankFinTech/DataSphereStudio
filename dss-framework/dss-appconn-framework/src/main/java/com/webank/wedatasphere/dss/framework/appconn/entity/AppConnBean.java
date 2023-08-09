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

import com.webank.wedatasphere.dss.appconn.manager.entity.AppConnInfo;
import com.webank.wedatasphere.dss.common.entity.Resource;

import java.io.Serializable;
import java.util.List;



public class AppConnBean implements AppConnInfo, Serializable {

    private static final long serialVersionUID=1L;
    private Long id;
    private String appConnName;
    private String isUserNeedInit;
    private String level;
    private Boolean ifIframe;
    private Boolean isExternal;
    private Boolean isMicroApp;

    // todo:目前通过这两个字段在classpath下加载类。
    // 未来这两个字段的作用是在bml和默认应用的。
    private String reference;
    private String resource;
    private Resource resourceObj;
    private String className;

    private List<AppInstanceBean> appInstanceBeans;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getAppConnName() {
        return appConnName;
    }

    public void setAppConnName(String appConnName) {
        this.appConnName = appConnName;
    }

    public String getIsUserNeedInit() {
        return isUserNeedInit;
    }

    public void setIsUserNeedInit(String isUserNeedInit) {
        this.isUserNeedInit = isUserNeedInit;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public Boolean getIfIframe() {
        return ifIframe;
    }

    public void setIfIframe(Boolean ifIframe) {
        this.ifIframe = ifIframe;
    }

    public Boolean getExternal() {
        return isExternal;
    }

    public void setExternal(Boolean external) {
        isExternal = external;
    }

    public Boolean getIsMicroApp() {
        return isMicroApp;
    }

    public void setIsMicroApp(Boolean isMicroApp) {
        this.isMicroApp = isMicroApp;
    }

    @Override
    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public List<AppInstanceBean> getAppInstanceBeans() {
        return appInstanceBeans;
    }

    public void setAppInstanceBeans(List<AppInstanceBean> appInstanceBeans) {
        this.appInstanceBeans = appInstanceBeans;
    }

    @Override
    public String getClassName() {
        return className;
    }

    @Override
    public Resource getAppConnResource() {
        return resourceObj;
    }

    public void setAppConnResource(Resource resource) {
        resourceObj = resource;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    @Override
    public String toString() {
        return "AppConnBean{" +
                "id=" + id +
                ", appConnName='" + appConnName + '\'' +
                ", className='" + className + '\'' +
                '}';
    }
}