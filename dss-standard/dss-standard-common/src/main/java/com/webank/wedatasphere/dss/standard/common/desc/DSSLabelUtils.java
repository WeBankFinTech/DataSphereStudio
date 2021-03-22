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

package com.webank.wedatasphere.dss.standard.common.desc;


import com.webank.wedatasphere.dss.common.entity.DSSLabel;

import java.util.List;

/**
 * @author allenlliu
 * @date 2020/12/8 17:51
 */
public class DSSLabelUtils {

    public static boolean belongToDev(List<DSSLabel> dssLabels) {
        DSSLabel devLabel = dssLabels
                .stream()
                .filter(dssLabel -> dssLabel.getLabel().equalsIgnoreCase(EnvironmentLabelEnum.DEV.getName()))
                .findAny()
                .orElse(null);
        return devLabel != null;
    }

    public static boolean belongToTest(List<DSSLabel> dssLabels) {
        DSSLabel testLabel = dssLabels
                .stream()
                .filter(dssLabel -> dssLabel.getLabel().equalsIgnoreCase(EnvironmentLabelEnum.TEST.getName()))
                .findAny()
                .orElse(null);
        return testLabel != null;
    }

    public static boolean belongToProd(List<DSSLabel> dssLabels) {
        DSSLabel prodLabel = dssLabels
                .stream()
                .filter(dssLabel -> dssLabel.getLabel().equalsIgnoreCase(EnvironmentLabelEnum.PROD.getName()))
                .findAny()
                .orElse(null);
        return prodLabel != null;
    }

    public static DSSLabel findEnvironmentLabel(List<DSSLabel> dssLabels) {
        for (DSSLabel dssLabel : dssLabels) {
            if (dssLabel instanceof EnvironmentLabel) {
                return dssLabel;
            }
        }
        return null;
    }

}
