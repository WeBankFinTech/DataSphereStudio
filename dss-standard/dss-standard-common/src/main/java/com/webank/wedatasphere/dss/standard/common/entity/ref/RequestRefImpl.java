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

package com.webank.wedatasphere.dss.standard.common.entity.ref;

import com.webank.wedatasphere.dss.common.label.DSSLabel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RequestRefImpl implements RequestRef {

    private Map<String, Object> parameters = new HashMap<>();
    protected String name;
    protected String type;
    protected List<DSSLabel> dssLabels;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public Object getParameter(String key) {
        return parameters.get(key);
    }

    @Override
    public void setParameter(String key, Object value) {
        parameters.put(key, value);
    }

    @Override
    public Map<String, Object> getParameters() {
        return parameters;
    }

    @Override
    public List<DSSLabel> getDSSLabels() {
        return dssLabels;
    }

}
