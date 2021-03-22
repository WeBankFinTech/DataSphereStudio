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
public class DSSWorkspaceScriptisInfoVO extends AbstractWorkspaceComponentInfoVO {

    private static final String SCRIPTIS_DESC = "Scriptis是微众银行微数域(WeDataSphere)打造的一站式交互式数据探索分析工具，以任意桥(Linkis)做为内核，提供多种计算存储引擎(如Spark、Hive、TiSpark等)、Hive数据库管理功能、资源(如Yarn资源、服务器资源)管理、应用管理和各种用户资源(如UDF、变量等)管理的能力。";

    private static final String SCRIPTIS_BUTTON_TEXT = "进入Scriptis";

    private static final String SCRIPTIS_ICON = "fi-scriptis";

    private static final String SCRIPTIS_MANUAL_URL = "http://127.0.0.1:8088/wiki/scriptis/manual/feature_overview_cn.html";

    private static final String SCRIPTIS_TITLE = "Scriptis";

    public DSSWorkspaceScriptisInfoVO() {
        this.setTitle(SCRIPTIS_TITLE);
        this.setButtonText(SCRIPTIS_BUTTON_TEXT);
        this.setDesc(SCRIPTIS_DESC);
        this.setIcon(SCRIPTIS_ICON);
        this.setUserManualUrl(SCRIPTIS_MANUAL_URL);
    }
}
