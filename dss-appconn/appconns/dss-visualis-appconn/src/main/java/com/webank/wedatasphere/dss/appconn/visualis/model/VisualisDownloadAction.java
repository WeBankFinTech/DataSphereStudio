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

package com.webank.wedatasphere.dss.appconn.visualis.model;

import com.webank.wedatasphere.dss.appconn.visualis.model.VisualisGetAction;
import org.apache.linkis.httpclient.request.DownloadAction;

import java.io.InputStream;

public class VisualisDownloadAction extends VisualisGetAction implements DownloadAction {

    private InputStream inputStream;


    public InputStream getInputStream() {
        return inputStream;
    }


    @Override
    public void write(InputStream inputStream) {
        this.inputStream = inputStream;
    }
}
