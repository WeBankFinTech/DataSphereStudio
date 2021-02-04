package com.webank.wedatasphpere.dss.user.service.impl;

import com.webank.wedatasphpere.dss.user.dto.request.AuthorizationBody;
import com.webank.wedatasphpere.dss.user.dto.request.LinuxServer;
import com.webank.wedatasphpere.dss.user.service.AbsCommand;

import java.util.ArrayList;

/**
 * @program: luban-authorization
 * @description: 创建用户空间
 * @author: luxl@chinatelecom.cn
 * @create: 2020-08-13 13:39
 **/

public class LinuxUserCommand extends AbsCommand {

    @Override
    public String authorization(AuthorizationBody body) throws Exception {

//        String hosts = body.getLinuxHosts();
//        String linuxPassword = body.getLinuxLoginPassword();
//        String linuxUserName = body.getLinuxLoginUser();
        ArrayList<LinuxServer> linuxServers = body.getServers();
        logger.info("服务器ip"+linuxServers.toString());
        StringBuffer stringBuffer = new StringBuffer();
        for(LinuxServer linuxServer:linuxServers){
            String hosts = linuxServer.getLinuxHost();
            String linuxPassword = linuxServer.getLinuxLoginPassword();
            String linuxUserName = linuxServer.getLinuxLoginUser();
            stringBuffer.append(hosts).append("#").append(linuxUserName).append("#").append(linuxPassword).append(",");
        }
        stringBuffer.deleteCharAt(stringBuffer.lastIndexOf(","));
        String addUserName = body.getUsername();
        String addUserPassword = body.getPassword();
        String bashCommand = this.getClass().getClassLoader().getResource("default/CreateLinuxUser.sh").getPath();
        String[] args = {stringBuffer.toString(),addUserName,addUserPassword};

        return this.runShell(bashCommand, args);
    }

}
