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

package com.webank.wedatasphere.dss.data.governance.conf;


import org.apache.linkis.common.conf.CommonVars;

public interface GovernanceConf {
    CommonVars<String> ATLAS_REST_ADDRESS = CommonVars.apply("atlas.rest.address");
    CommonVars<String> ATLAS_USERNAME = CommonVars.apply("atlas.username");
    CommonVars<String> ATLAS_PASSWORD = CommonVars.apply("atlas.password");
    //CommonVars<String> METASTORE_DATASOURCE_PASSWORD = CommonVars.apply("atlas.client.readTimeoutMSecs");
    //CommonVars<String> METASTORE_DATASOURCE_PASSWORD = CommonVars.apply("atlas.client.connectTimeoutMSecs");
    CommonVars<String>ATLAS_ROOT_INDICATOR = CommonVars.apply("atlas.root.indicator");
    CommonVars<String>ATLAS_ROOT_MEASURE = CommonVars.apply("atlas.root.measure");
    CommonVars<String>ATLAS_ROOT_DIMENSION = CommonVars.apply("atlas.root.dimension");
    CommonVars<String>ATLAS_ROOT_LAYER = CommonVars.apply("atlas.root.layer");
    CommonVars<String>ATLAS_ROOT_THEME = CommonVars.apply("atlas.root.theme");

    CommonVars<String>ATLAS_ROOT_LABEL = CommonVars.apply("atlas.root.label");
    CommonVars<String>ATLAS_ROOT_COLLECTION = CommonVars.apply("atlas.root.collection");


    CommonVars<String> METASTORE_DATASOURCE_DRIVER = CommonVars.apply("metastore.datasource.driver", "com.mysql.jdbc.Driver");
    CommonVars<String> METASTORE_DATASOURCE_URL = CommonVars.apply("metastore.datasource.url");
    CommonVars<String> METASTORE_DATASOURCE_USERNAME = CommonVars.apply("metastore.datasource.username");
    CommonVars<String> METASTORE_DATASOURCE_PASSWORD = CommonVars.apply("metastore.datasource.password");
}
