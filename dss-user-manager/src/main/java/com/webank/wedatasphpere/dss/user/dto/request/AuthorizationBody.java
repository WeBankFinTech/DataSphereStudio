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


package com.webank.wedatasphpere.dss.user.dto.request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @program: user-manager
 * @description: 施工单数据结构
 *
 * @create: 2020-08-12 14:29
 **/
public class AuthorizationBody {

    private String username;
    private String password;
    private String dssInstallDir;
    private String azkakanDir;
    private ArrayList<LinuxServer> servers;


    public ArrayList<LinuxServer> getServers() {
        return servers;
    }

    public void setServers(ArrayList<LinuxServer> servers) {
        this.servers = servers;
    }




    public String getAzkakanDir() {
        return azkakanDir;
    }

    public void setAzkakanDir(String azkakanDir) {
        this.azkakanDir = azkakanDir;
    }

    public String getDssInstallDir() {
        return dssInstallDir;
    }

    public void setDssInstallDir(String installDir) {
        this.dssInstallDir = installDir;
    }

    public List<HashMap<String, String>> getPaths() {
        return paths;
    }

    public void setPaths(List<HashMap<String, String>> paths) {
        this.paths = paths;
    }

    private List<HashMap<String,String>> paths;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDatabaseName(){
        return this.username + "_default";
    }



}
