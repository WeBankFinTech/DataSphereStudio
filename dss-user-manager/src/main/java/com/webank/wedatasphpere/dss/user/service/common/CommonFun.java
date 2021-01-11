package com.webank.wedatasphpere.dss.user.service.common;

import com.webank.wedatasphpere.dss.user.service.Command;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.logging.Logger;

/**
 * @author anlexander
 * @date 2021/1/8
 */
public class CommonFun {
    static Logger log = Logger.getLogger("CommonFun");
    public static String process(Process process) {
        BufferedReader br; br = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String inline;
        try{
            while ((inline = br.readLine()) != null) {
                if (!inline.equals("")) {
                    inline = inline.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
                    log.info(inline);
                } else {
                    log.info("\n");
                }
            }
            br.close();
            br = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            while ((inline = br.readLine()) != null) {
                if (!inline.equals("")) {
                    log.info(inline);
                } else {
                    log.info("\n");
                }

            }
            if (process.waitFor() != 0) {
                log.info("restart go server error:" + process.waitFor());
            }

        }catch (Exception e){
            e.printStackTrace();
            return e.getMessage();
        }
        return  Command.SUCCESS;

    }
}
