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
public class DSSWorkspaceVisualisInfoVO extends AbstractWorkspaceComponentInfoVO{

    private static final String VISUALIS_DESC = "Visualis是一个可视化BI工具，支持拖拽式报表定义、图表联动、钻取、全局筛选、多维分析、实时查询等数据开发探索的可视化分析模式。";

    private static final String VISUALIS_BUTTON_TEXT = "进入Visualis";

    private static final String VISUALIS_ICON = "fi-visualis";

    private static final String VISUALIS_MANUAL_URL = "http://127.0.0.1:8088/wiki/scriptis/manual/feature_overview_cn.html";

    private static final String VISUALIS_TITLE = "Visualis";

    public DSSWorkspaceVisualisInfoVO() {
        this.setTitle(VISUALIS_TITLE);
        this.setButtonText(VISUALIS_BUTTON_TEXT);
        this.setDesc(VISUALIS_DESC);
        this.setIcon(VISUALIS_ICON);
        this.setUserManualUrl(VISUALIS_MANUAL_URL);
    }
    
}
