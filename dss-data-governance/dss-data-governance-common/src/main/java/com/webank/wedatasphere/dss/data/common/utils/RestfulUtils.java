package com.webank.wedatasphere.dss.data.common.utils;

import org.apache.linkis.server.Message;
import org.apache.commons.math3.util.Pair;

import javax.ws.rs.core.Response;
import java.util.Arrays;

/**
 * created by cooperyang on 2020/10/19
 * Description:
 */
public class RestfulUtils {

    public static Response dealError(String reason){
        Message message = Message.error(reason);
        return Message.messageToResponse(message);
    }

    public static Response dealOk(String msg){
        Message message = Message.ok(msg);
        return Message.messageToResponse(message);
    }



    @SafeVarargs
    public static Response dealOk(String msg, Pair<String, Object>... data){
        Message message = Message.ok(msg);
        Arrays.stream(data).forEach(p -> message.data(p.getKey(), p.getValue()));
        return Message.messageToResponse(message);
    }


}
