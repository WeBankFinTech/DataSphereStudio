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

package com.webank.wedatasphere.dss.appconn.visualis;

import com.google.common.collect.Lists;
import com.webank.wedatasphere.dss.appconn.core.AppConn;
import com.webank.wedatasphere.dss.appconn.visualis.execution.VisualisExecutionService;
import com.webank.wedatasphere.dss.appconn.visualis.publish.VisualisRefExportService;
import com.webank.wedatasphere.dss.appconn.visualis.publish.VisualisRefImportService;
import com.webank.wedatasphere.dss.appconn.visualis.ref.VisualisCRUDService;
import com.webank.wedatasphere.dss.appconn.visualis.ref.VisualisQueryService;
import com.webank.wedatasphere.dss.appconn.visualis.ref.VisualisVisibleService;
import com.webank.wedatasphere.dss.standard.app.development.AbstractLabelDevelopmentIntegrationStandard;
import com.webank.wedatasphere.dss.standard.app.development.RefOperationService;
import com.webank.wedatasphere.dss.standard.common.desc.AppDesc;
import com.webank.wedatasphere.dss.standard.common.exception.AppStandardErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class VisualisDevelopmentIntegrationStandard extends AbstractLabelDevelopmentIntegrationStandard {
    private static final Logger LOGGER = LoggerFactory.getLogger(VisualisDevelopmentIntegrationStandard.class);

    private AppConn appConn;

    public VisualisDevelopmentIntegrationStandard(AppConn appConn){
        try {
            this.appConn = appConn;
            init();
        } catch (AppStandardErrorException e) {
            LOGGER.error("Failed to init {}", this.getClass().getSimpleName(), e);
        }
    }

    @Override
    protected List<RefOperationService> getRefOperationService() {
        return Lists.newArrayList(
                new VisualisCRUDService(),
                new VisualisExecutionService(),
                new VisualisQueryService(),
                new VisualisRefExportService(),
                new VisualisRefImportService(),
                new VisualisVisibleService());
    }

    @Override
    public AppDesc getAppDesc() {
        return this.appConn.getAppDesc();
    }

    @Override
    public void setAppDesc(AppDesc appDesc) {
        //do nothing
    }

    @Override
    public void init() throws AppStandardErrorException {

    }

    @Override
    public void close() throws IOException {

    }
}
