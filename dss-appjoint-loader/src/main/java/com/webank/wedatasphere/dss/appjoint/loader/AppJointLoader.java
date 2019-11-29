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

package com.webank.wedatasphere.dss.appjoint.loader;

import com.webank.wedatasphere.dss.appjoint.AppJoint;
import com.webank.wedatasphere.linkis.common.exception.ErrorException;

import java.util.Map;
/**
 * created by cooperyang on 2019/11/8
 * Description: AppJointLoader 是为了上层模块，如server，entrance等模块能够获取到指定的AppJoint
 */
public interface AppJointLoader {


    String DIR_NAME = "appjoints";

    String APPJOINT_DIR_NAME = "dss-appjoints";

    String PROPERTIES_NAME = "appjoint.properties";

    String CONF_NAME = "conf";

    String LIB_NAME = "lib";

    String JAR_SUF_NAME = ".jar";

    String FILE_SCHEMA = "file://";

    /**
     * 获取一个appjoint,需要通过baseUrl,name 和 params进行获取
     * @param baseUrl appjoint代理的外部系统的url
     * @return 一个appjoint
     */

    AppJoint getAppJoint(String baseUrl, String appJointName, Map<String, Object> params) throws Exception;


    static AppJointLoader getAppJointLoader(){
        return AppJointLoaderFactory.getAppJointLoader();
    }


}
