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

package com.webank.wedatasphere.dss.application.entity;

/**
 * Created by chaogefeng on 2019/10/10.
 * “一个应用”的抽象，比如等等
 */
public class Application {
    private Integer id;
    private String name;
    private String url;
    private Boolean isUserNeedInit;
    private Integer level;
    private String userInitUrl;
    private Boolean existsProjectService;//有些应用可能没有project这么一个概念
    private String projectUrl;
    private String enhanceJson;
    private Boolean ifIframe;
    private String homepageUrl;
    private String redirectUrl;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean getUserNeedInit() {
        return isUserNeedInit;
    }

    public void setUserNeedInit(Boolean userNeedInit) {
        isUserNeedInit = userNeedInit;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getUserInitUrl() {
        return userInitUrl;
    }

    public void setUserInitUrl(String userInitUrl) {
        this.userInitUrl = userInitUrl;
    }

    public Boolean getExistsProjectService() {
        return existsProjectService;
    }

    public void setExistsProjectService(Boolean existsProjectService) {
        this.existsProjectService = existsProjectService;
    }

    public String getProjectUrl() {
        return projectUrl;
    }

    public void setProjectUrl(String projectUrl) {
        this.projectUrl = projectUrl;
    }

    public String getEnhanceJson() {
        return enhanceJson;
    }

    public void setEnhanceJson(String enhanceJson) {
        this.enhanceJson = enhanceJson;
    }

    public Boolean getIfIframe() {
        return ifIframe;
    }

    public String getHomepageUrl() {
        return homepageUrl;
    }

    public void setHomepageUrl(String homepageUrl) {
        this.homepageUrl = homepageUrl;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public void setIfIframe(Boolean ifIframe) {
        this.ifIframe = ifIframe;
    }


}
