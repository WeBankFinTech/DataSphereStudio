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

package com.webank.wedatasphere.dss.orchestrator.converter.standard.operation;

import com.webank.wedatasphere.dss.orchestrator.converter.standard.ref.ConversionRequestRef;
import com.webank.wedatasphere.dss.orchestrator.converter.standard.service.ConversionService;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.service.Operation;


public interface ConversionOperation<K extends ConversionRequestRef, V extends ResponseRef> extends Operation<K, V> {

    void setConversionService(ConversionService conversionService);

    V convert(K ref);

    void init();

}
