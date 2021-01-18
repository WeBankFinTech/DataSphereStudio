package com.webank.wedatasphpere.dss.user.service.impl;

import com.webank.wedatasphpere.dss.user.dto.request.AuthorizationBody;
import com.webank.wedatasphpere.dss.user.service.AbsCommand;
import com.webank.wedatasphpere.dss.user.service.Command;

import java.util.HashMap;
import java.util.List;

/**
 * @program: luban-authorization
 * @description: 创建用户空间
 * @author: luxl@chinatelecom.cn
 * @create: 2020-08-13 13:39
 **/

public class WorkspaceCommand extends AbsCommand {

    @Override
    public String authorization(AuthorizationBody body) {
        List<HashMap<String,String>> paths = body.getPaths();
        for(HashMap<String,String> map : paths){
          String path = map.get("value");
          String rst = createDir(path, body);
          if(rst.equals(Command.SUCCESS)){
              return rst;
          }
        }
        return Command.SUCCESS;
    }

    private String createDir(String path, AuthorizationBody body) {
        String bashCommand;

        if(path.contains("hdfs:")){
            path = path.replace("hdfs://", "") + "/" + body.getUsername();
            bashCommand = getResource("default/HdfsPath.sh");
        }else {
            path = path.replace("file://", "") + "/" + body.getUsername();
            bashCommand = getResource("default/LinuxPath.sh");
        }
        String[] args = {body.getUsername(), path};
        return this.runShell(bashCommand, args);
    }
}
