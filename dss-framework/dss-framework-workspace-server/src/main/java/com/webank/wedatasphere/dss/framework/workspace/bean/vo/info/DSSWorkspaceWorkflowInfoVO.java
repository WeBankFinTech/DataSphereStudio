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

package com.webank.wedatasphere.dss.framework.workspace.bean.vo.info;

/**
 * created by cooperyang on 2020/3/17
 * Description:
 */
public class DSSWorkspaceWorkflowInfoVO extends AbstractWorkspaceComponentInfoVO{

    //private static final String WORKFLOW_DESC = "DSS系统工作流开发是支持以可视化编辑方式进行工作流编排，" +
            //"同时支持工作流实时执行并能够一键发布到WTSS进行定时调度的组件，工作流开发已经支持Scriptis、Visualis以及Qualitis等子系统的接入。";

    private static final String WORKFLOW_DESC = "DataSphere Studio workflow development is a component which supports workflow editing " +
            "through visual editing, and supports real-time execution of workflows and releasing workflows to WTSS by one-click scheduling as well. " +
            "Now it has supported the connection of subsystems such as Scriptis, Visualis, and Qualitis etc. ";

    private static final String WORKFLOW_BUTTON_TEXT = "Enter Workflow Development";

    private static final String WORKFLOW_ICON = "fi-workflow";

    private static final String WORKFLOW_MANUAL_URL = "http://127.0.0.1:8088/wiki/scriptis/manual/feature_overview_cn.html";

    private static final String WORKFLOW_TITLE = "Workflow Development";

    public DSSWorkspaceWorkflowInfoVO() {
        this.setTitle(WORKFLOW_TITLE);
        this.setButtonText(WORKFLOW_BUTTON_TEXT);
        this.setDesc(WORKFLOW_DESC);
        this.setIcon(WORKFLOW_ICON);
        this.setUserManualUrl(WORKFLOW_MANUAL_URL);
    }
    
}
