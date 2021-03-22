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

package com.webank.wedatasphere.dss.appconn.sendemail.hook;

import java.util.Map;

/**
 * Created by v_wbtaozhong on 2018/12/25.
 */
public class EmailInfo {
    private String formId;
    private String user;
    private String dssProject;
    private String widgets;
    private String cc;
    private String to;
    private String bcc;
    private String status;
    private String priority;
    private String alertList;
    private int alertInterval = 60;
    private String requestCreatedate;
    private Map<String, String> widgetColumns;

    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getDssProject() {
        return dssProject;
    }

    public void setDssProject(String dssProject) {
        this.dssProject = dssProject;
    }

    public String getWidgets() {
        return widgets;
    }

    public void setWidgets(String widgets) {
        this.widgets = widgets;
    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getBcc() {
        return bcc;
    }

    public void setBcc(String bcc) {
        this.bcc = bcc;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getAlertList() {
        return alertList;
    }

    public void setAlertList(String alertList) {
        this.alertList = alertList;
    }

    public int getAlertInterval() {
        return alertInterval;
    }

    public void setAlertInterval(int alertInterval) {
        this.alertInterval = alertInterval;
    }

    public String getRequestCreatedate() {
        return requestCreatedate;
    }

    public void setRequestCreatedate(String requestCreatedate) {
        this.requestCreatedate = requestCreatedate;
    }

    public Map<String, String> getWidgetColumns() {
        return widgetColumns;
    }

    public void setWidgetColumns(Map<String, String> widgetColumns) {
        this.widgetColumns = widgetColumns;
    }
}
