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
import com.webank.wedatasphere.dss.standard.common.exception.NoSuchAppInstanceException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class AppDescImpl implements AppDesc {

    private static final Logger LOG = LoggerFactory.getLogger(AppDescImpl.class);

    private String appName;
    private List<AppInstance> appInstances = new ArrayList<>();

    @Override
    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    @Override
    public List<AppInstance> getAppInstances() {
        return appInstances;
    }

    @Override
    public List<AppInstance> getAppInstancesByLabels(List<DSSLabel> labels) throws NoSuchAppInstanceException{
        // 1. 通过用户自定义的比较器完成的排序，返回最前一个appInstance
        // 2. 返回与labels完全匹配的appInstance
        AppInstance targetAppInstance = null;
        int similarity = 0;
        int maxSimilarity = 0;
        for(AppInstance appInstance: appInstances) {
            similarity = 0;
            List<DSSLabel> targetLabels = appInstance.getLabels();
            for(DSSLabel label: targetLabels) {
                for(DSSLabel userLabel: labels) {
                    // if user's label equal the target label, then the similarity increase.
                    if (isEqualLabel(userLabel,label)) {
                        similarity++;
                    }
                    // if current similarity bigger than the maxSimilarity.
                    if (similarity > maxSimilarity) {
                        maxSimilarity = similarity;
                        targetAppInstance = appInstance;
                    }
                }
            }
        }
        // if all the labels is different form the target, then return null.
        if(maxSimilarity <= 0) {
            LOG.error("{} has no such AppInstance match the labels: {}.", appName, labels);
            throw new NoSuchAppInstanceException(60002, "No such AppInstance machs the labels.");
        }
        return Collections.singletonList(targetAppInstance);
    }

    /**
     * 判断两个label是否相等
     */
    public boolean isEqualLabel(DSSLabel userLabel, DSSLabel label){
        String userEnv = userLabel.getValue().get(userLabel.getLabelKey());
        String instanceEnv = label.getValue().get(label.getLabelKey());
        return StringUtils.isNotBlank(userEnv) && userEnv.equalsIgnoreCase(instanceEnv);
    }

    public void addAppInstance(AppInstance appInstance) {
        appInstances.add(appInstance);
    }

    public void removeAppInstance(AppInstance appInstance) {
        appInstances.remove(appInstance);
    }
}
