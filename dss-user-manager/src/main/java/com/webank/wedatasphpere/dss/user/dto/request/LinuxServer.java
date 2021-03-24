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

public class LinuxServer {

    private String linuxHost; //master ip，Comma separated
    private String linuxLoginUser;
    private String linuxLoginPassword;


    public String getLinuxHost() {
        return linuxHost;
    }

    public void setLinuxHost(String linuxHosts) {
        this.linuxHost = linuxHosts;
    }

    public String getLinuxLoginUser() {
        return linuxLoginUser;
    }

    public void setLinuxLoginUser(String linuxLoginUser) {
        this.linuxLoginUser = linuxLoginUser;
    }

    public String getLinuxLoginPassword() {
        return linuxLoginPassword;
    }

    public void setLinuxLoginPassword(String linuxLoginPassword) {
        this.linuxLoginPassword = linuxLoginPassword;
    }


}
