package com.webank.wedatasphpere.dss.user.service;


import com.webank.wedatasphere.linkis.server.Message;
import com.webank.wedatasphpere.dss.user.dto.request.AuthorizationBody;
import org.dom4j.DocumentException;

import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 各模块的授权 继承这个类 根据需要实现自己的类。
 */
public abstract class AbsCommand implements Command {

    @Override
    public String capacity(AuthorizationBody body) {
        return Command.SUCCESS;
    }

    @Override
    public String renew(AuthorizationBody body) {
        return Command.SUCCESS;
    }

    @Override
    public String undoAuthorization(AuthorizationBody body) { return Command.SUCCESS; }

    @Override
    public String authorization(AuthorizationBody body) throws DocumentException { return Command.SUCCESS; }

    public String toMessage(String msg) {
        return this.getClass().getSimpleName() + "模块开始执行："+ msg;
    }

    protected String runShell(String scriptPath, String[] args){
        String bashCommand;
        BufferedReader br;
        try {
            bashCommand = this.getClass().getClassLoader().getResource(scriptPath).getPath();
            bashCommand = bashCommand + String.join(" ", args);
            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec(bashCommand);

            return this.getString(process);
        }
        catch (Exception e){
            e.printStackTrace();
            return e.getMessage();
        }
    }

    protected String getString(Process process) throws IOException, InterruptedException {
        BufferedReader br;
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
}
