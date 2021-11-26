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

package com.webank.wedatasphere.dss.standard.app.development.ref;


public class CommonResponseRef extends com.webank.wedatasphere.dss.standard.common.entity.ref.CommonResponseRef {

    protected Long orcId;
    protected String content;
    protected String name;
    protected  boolean result;

    public boolean getResult() {
        return this.result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public CommonResponseRef(String responseBody, int status) {
        super(responseBody, status);
    }

    public CommonResponseRef(){
        super("",0);
    }

    public void setOrcId(Long orcId) {
        this.orcId = orcId;
    }

    public Long getOrcId() {
        return orcId;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
