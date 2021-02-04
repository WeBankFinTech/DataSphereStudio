package com.webank.wedatasphpere.dss.user.service;


import com.webank.wedatasphpere.dss.user.dto.request.AuthorizationBody;
import org.dom4j.DocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * 各模块的授权 继承这个类 根据需要实现自己的类。
 */
public abstract class AbsCommand implements Command {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

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
    public String authorization(AuthorizationBody body) throws IOException,DocumentException { return Command.SUCCESS; }
    public String toMessage(String msg) {
        return this.getClass().getSimpleName() + "模块开始执行："+ msg;
    }

    protected String runShell(String scriptPath, String[] args) throws Exception {
        String bashCommand;
        try {
            bashCommand = "sh " + scriptPath + " " + String.join(" ", args);
            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec(bashCommand);

            return this.getString(process);
        }
        catch (Exception e){
            logger.error(scriptPath, e);
            return e.getMessage();
        }
    }

    protected String getString(Process process) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));

        String inline;
        while ((inline = br.readLine()) != null) {
            if (!inline.equals("")) {
                inline = inline.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
                logger.info("shell info:"+inline);
            } else {
                logger.info("\n");
            }
        }
        br.close();
        br = new BufferedReader(new InputStreamReader(process.getErrorStream()));    //错误信息
        while ((inline = br.readLine()) != null) {
            if (!inline.equals(""))
                logger.error("shell error:"+inline);
            else
                logger.error("\n");
//            throw new Exception(inline);
        }

        int status = process.waitFor();
        if (status != 0){
            logger.error("shell error: "+status);
        }
        return Command.SUCCESS;
    }

    protected String getResource(String path){
        try {
            URL url = this.getClass().getClassLoader().getResource(path);
            return url.getPath();
        }catch (Exception e){
            logger.error("File does not exist " + path, e);
        }
        return null;
    }
}
