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

package com.webank.wedatasphere.dss.common.entity.flow;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by enjoyyin on 2019/5/14.
 */
public class DSSJSONFlow extends DSSFlow {
    private String json;

    private List<DSSJSONFlow> children;

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    @Override
    public void setChildren(List<? extends Flow> children) {
        this.children = children.stream().map(f ->(DSSJSONFlow)f).collect(Collectors.toList());
    }

    @Override
    public List<DSSJSONFlow> getChildren() {
        return children;
    }
}
