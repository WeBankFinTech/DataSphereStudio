package com.webank.wedatasphpere.dss.user.service.impl;

import com.webank.wedatasphpere.dss.user.dto.request.AuthorizationBody;
import com.webank.wedatasphpere.dss.user.service.AbsCommand;
import com.webank.wedatasphpere.dss.user.service.Command;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

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
          if(rst != Command.SUCCESS){
              return rst;
          }
        }

//        for(Integer i=0; i<paths.size(); i++){
//            String rst = createDir(paths.get(i), body);
//            if(rst != Command.SUCCESS){
//                return rst;
//            }
//        }
        return Command.SUCCESS;
    }

    private String createDir(String path, AuthorizationBody body) {
        String bashCommand;
        BufferedReader br;
        try {
            if(path.indexOf("hdfs:") != -1){
                path = path.replace("hdfs://", "");
                bashCommand = this.getClass().getClassLoader().getResource("./default/LinuxPath.sh").getPath();
            }else {
                path = path.replace("file://", "");
                bashCommand = this.getClass().getClassLoader().getResource("./default/LinuxPath.sh").getPath();
            }

            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec("sudo sh " + bashCommand + " " + body.getUsername() + " " + path + "/" + body.getUsername());

            br = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String inline;
            while ((inline = br.readLine()) != null) {
                if (!inline.equals("")) {
                    inline = inline.replaceAll("<", "&lt;").replaceAll(">", "&gt;");

                    System.out.println(inline);
                } else {
                    System.out.println("\n");
                }
            }
            br.close();
            br = new BufferedReader(new InputStreamReader(process.getErrorStream()));    //错误信息
            while ((inline = br.readLine()) != null) {
                if (!inline.equals(""))
                    System.out.println( inline );
                else
                    System.out.println("\n");
            }

            int status = process.waitFor();
            if (status != 0){
                System.out.println("restart go server error:"+status); ;
            }
            return Command.SUCCESS;
        }
        catch (Exception e){
            e.printStackTrace();
            return e.getMessage();
        }
    }
}
