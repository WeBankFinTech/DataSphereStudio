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

package com.webank.wedatasphere.dss.appconn.schedulis.Action;

import com.webank.wedatasphere.dss.appconn.schedulis.sso.UserInfo;
import com.webank.wedatasphere.linkis.httpclient.request.BinaryBody;
import com.webank.wedatasphere.linkis.httpclient.request.POSTAction;
import com.webank.wedatasphere.linkis.httpclient.request.UploadAction;
import scala.Option;


import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FlowScheduleUploadAction extends POSTAction implements UploadAction, UserInfo {
    private Map<String,InputStream> inputStreams;
    private List<BinaryBody> binaryBodies;
    private Map<String,String> streamNames=new HashMap<>();

    private String url;
    private String user;
    private ArrayList<String> filePaths;

    public FlowScheduleUploadAction(List<BinaryBody> binaryBodies){
        this.filePaths=null;
        this.binaryBodies = binaryBodies;
    }

    @Override
    public String getURL() {
        return this.url;
    }

    public void  setURl(String url){
        this.url = url;
    }

    @Override
    public Map<String, String> files() {
           Map<String,String> map = new HashMap<>();

            if (null == filePaths || filePaths.size() == 0) {
               return map;
            }
            else {
                filePaths.stream().forEach(
                        filePath -> map.put("file", filePath));
            }

            return map;
    }

    @Override
    public Map<String, InputStream> inputStreams() {
        return inputStreams;
    }

    @Override
    public Map<String, String> inputStreamNames() {
        return streamNames;
    }

    @Override
    public Option<String> user() {
        return null;
    }


    @Override
    public String getRequestPayload() {
        return null;
    }

    @Override
    public void setUser(String user) {
          this.user = user;
    }

    @Override
    public String getUser() {
        return user;
    }

    @Override
    public List<BinaryBody> binaryBodies() {
        return binaryBodies;
    }
}
