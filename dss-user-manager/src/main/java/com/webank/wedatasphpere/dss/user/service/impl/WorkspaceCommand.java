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
    public String authorization(AuthorizationBody body) throws Exception {
        List<HashMap<String,String>> paths = body.getPaths();
        String result = "";
        Boolean isSuccess = true;
        for(HashMap<String,String> map : paths){
          String path = map.get("value");
          String rst = createDir(path, body);
          result += rst;
          if(!rst.equals(Command.SUCCESS)){
              isSuccess = false;
          }
        }
        if(isSuccess){
            return Command.SUCCESS;
        }
        logger.error(result);
        return result;
    }

    private String createDir(String path, AuthorizationBody body) throws Exception {
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
