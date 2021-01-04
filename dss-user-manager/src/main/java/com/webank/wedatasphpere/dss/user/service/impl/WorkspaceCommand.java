package com.webank.wedatasphpere.dss.user.service.impl;

import com.webank.wedatasphpere.dss.user.conf.DSSUserManagerConfig;
import com.webank.wedatasphpere.dss.user.dto.request.AuthorizationBody;
import com.webank.wedatasphpere.dss.user.service.AbsCommand;
import com.webank.wedatasphpere.dss.user.service.Command;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * @program: luban-authorization
 * @description: 创建用户空间
 * @author: luxl@chinatelecom.cn
 * @create: 2020-08-13 13:39
 **/

public class WorkspaceCommand extends AbsCommand {

    @Override
    public String authorization(AuthorizationBody body) {
        String rootPath = DSSUserManagerConfig.LOCAL_USER_ROOT_PATH;
        System.out.println(rootPath);
        String bashCommand;
        BufferedReader br = null;
        BufferedWriter wr = null;
        try {
            if(rootPath.indexOf("hdfs:") == -1){
                bashCommand = this.getClass().getClassLoader().getResource("./default/LinuxPath.sh").getPath();
            }else {
                bashCommand = this.getClass().getClassLoader().getResource("default/LinuxPath.sh").getPath();
            }

            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec("sudo sh " + bashCommand + " " + body.getUsername() + " " + "/Users/test22");

            br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            wr = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));

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
                return "restart go server error";
            }
            return Command.SUCCESS;
        }
        catch (Exception e){
            e.printStackTrace();
            return e.getMessage();
        }
    }
}
