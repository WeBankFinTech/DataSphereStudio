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
import com.webank.wedatasphere.dss.common.label.DSSLabel;

import java.util.List;
import java.util.Map;


public interface AppInstance {

    Long getId();

    /**
     * 返回第三方 AppConn 的基础URL，例如：http://ip:port/ 。是 DSS 接下来访问这个第三方 AppConn 的基础 URL，
     * AppConn 的 Operation 都将通过这个 baseURL 去拼接实际请求的具体 URL。
     * @return 返回第三方 AppConn 的基础URL
     */
    String getBaseUrl();

    /**
     * 这里返回的是第三方 AppConn 主页的 URI，即不包含 IP 和 Port 部分的 URI 内容，如想获取完整的
     * homepage URL，请通过 getBaseUrl() + getHomepageUri()获取。
     * 请注意：DSS 允许用户在 getHomepageUri() 中携带 workspaceId 和 workspaceName 变量，DSS会在使用时自动替换，
     * 参考的格式如下：<br/>
     *   #/home?workspaceId=${workspaceId}&workspaceName=${workspaceName}
     * @return 返回第三方 AppConn 主页的 URI，例如：#/home
     */
    String getHomepageUri();

    /**
     * 与 dss_appinstance 数据库表的 enhance_json 字段对应，允许用户在数据库表中传入一个 json 格式的参数列表，
     * 第三方应用 AppConn 的 Operation 可以直接调用该方法获取  enhance_json 里面的参数列表。<br/>
     * 当然，您也可以通过该 AppConn 的 appconn.properties 设置一些所需的参数。appconn.properties 文件存放在
     * ${APPCONN_HOME}/${APPCONN_NAME}/conf 目录下
     * @return 第三方 AppConn 参数列表
     */
    Map<String, Object> getConfig();

    List<DSSLabel> getLabels();

}
