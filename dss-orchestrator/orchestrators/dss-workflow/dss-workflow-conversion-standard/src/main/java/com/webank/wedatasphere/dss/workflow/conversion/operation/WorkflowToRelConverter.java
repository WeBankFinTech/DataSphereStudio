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

package com.webank.wedatasphere.dss.workflow.conversion.operation;

import com.webank.wedatasphere.dss.workflow.conversion.entity.ConvertedRel;
import com.webank.wedatasphere.dss.workflow.conversion.entity.PreConversionRel;
import com.webank.wedatasphere.dss.workflow.core.order.Order;


public interface WorkflowToRelConverter extends Order {

    /**
     * Try to convert DSS workflow to external app ref.
     * Warn: The implementation should resolve the loop of children workflow's by yourself.
     * @param rel The DSS workflow wait to convert
     * @return Converted Rel
     */
    ConvertedRel convertToRel(PreConversionRel rel);

}
