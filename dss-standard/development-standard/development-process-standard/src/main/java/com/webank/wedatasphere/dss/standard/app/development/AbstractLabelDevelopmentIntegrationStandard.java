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

package com.webank.wedatasphere.dss.standard.app.development;

import com.google.common.collect.Lists;
import com.webank.wedatasphere.dss.standard.app.development.process.DevProcessService;
import com.webank.wedatasphere.dss.standard.app.development.process.ProcessService;
import com.webank.wedatasphere.dss.standard.app.development.process.ProdProcessService;
import com.webank.wedatasphere.dss.standard.app.development.process.TestProcessService;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;
import com.webank.wedatasphere.dss.common.entity.DSSLabel;
import com.webank.wedatasphere.dss.standard.common.desc.DSSLabelUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractLabelDevelopmentIntegrationStandard extends AbstractDevelopmentIntegrationStandard {



    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractLabelDevelopmentIntegrationStandard.class);


    List<ProcessService> processServices = Lists.newArrayList();

    @Override
    public List<ProcessService> getProcessServices() {
        if (CollectionUtils.isNotEmpty(processServices)) {
            return processServices;
        }
        for (AppInstance appInstance : getAppDesc().getAppInstances()) {
            if (DSSLabelUtils.belongToDev(appInstance.getLabels())) {
                DevProcessService devProcessService = new DevProcessService(getRefOperationService());
                devProcessService.setAppDesc(getAppDesc());
                devProcessService.setLabels(appInstance.getLabels());
                devProcessService.setAppStandard(this);
                devProcessService.setAppInstance(appInstance);
                processServices.add(devProcessService);
            } else if (DSSLabelUtils.belongToTest(appInstance.getLabels())) {
                TestProcessService testProcessService = new TestProcessService(getRefOperationService());
                testProcessService.setAppDesc(getAppDesc());
                testProcessService.setLabels(appInstance.getLabels());
                testProcessService.setAppStandard(this);
                testProcessService.setAppInstance(appInstance);
                processServices.add(testProcessService);
            } else if (DSSLabelUtils.belongToProd(appInstance.getLabels())) {
                ProdProcessService prodProcessService = new ProdProcessService(getRefOperationService());
                prodProcessService.setAppDesc(getAppDesc());
                prodProcessService.setLabels(appInstance.getLabels());
                prodProcessService.setAppStandard(this);
                prodProcessService.setAppInstance(appInstance);
                processServices.add(prodProcessService);
            }
        }
        return processServices;
    }

    @Override
    public ProcessService getProcessService(List<DSSLabel> dssLabels) {

        List<String> dssLabelsStr = dssLabels.stream()
                .map(DSSLabel::getLabel)
                .collect(Collectors.toList());
        for (ProcessService processService : getProcessServices()) {
            List<String> tmpDssLabels = processService
                    .getLabels()
                    .stream()
                    .map(DSSLabel::getLabel)
                    .collect(Collectors.toList());
            if (!Collections.disjoint(dssLabelsStr, tmpDssLabels)){
                return processService;
            }
        }
        LOGGER.error("Failed to get process Service for labels : {}, standard : {}", dssLabels, this.getClass().getName());
        return null;
    }



}
