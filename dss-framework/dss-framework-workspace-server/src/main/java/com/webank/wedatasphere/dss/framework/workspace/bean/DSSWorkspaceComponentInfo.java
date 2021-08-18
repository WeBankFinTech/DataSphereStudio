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

package com.webank.wedatasphere.dss.framework.workspace.bean;


import java.util.List;


public class DSSWorkspaceComponentInfo {
    private String title;

    private String icon;

    private String desc;

    private String buttonText;
/*
    private List<ComponentStatistic> statistics;*/

    private String componentUrl;

    private String userManualUrl;

    private String indicatorUrl;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getButtonText() {
        return buttonText;
    }

    public void setButtonText(String buttonText) {
        this.buttonText = buttonText;
    }

/*
    public List<ComponentStatistic> getStatistics() {
        return statistics;
    }

    public void setStatistics(List<ComponentStatistic> statistics) {
        this.statistics = statistics;
    }
*/

    public String getComponentUrl() {
        return componentUrl;
    }

    public void setComponentUrl(String componentUrl) {
        this.componentUrl = componentUrl;
    }

    public String getUserManualUrl() {
        return userManualUrl;
    }

    public void setUserManualUrl(String userManualUrl) {
        this.userManualUrl = userManualUrl;
    }

    public String getIndicatorUrl() {
        return indicatorUrl;
    }

    public void setIndicatorUrl(String indicatorUrl) {
        this.indicatorUrl = indicatorUrl;
    }
}
