package com.webank.wedatasphere.linkis.oneservice.core.vo;

import java.util.LinkedHashMap;

/**
 * MessageExtVo
 *
 * @author lidongzhang
 */
public class MessageExtVo extends LinkedHashMap<String, Object> {
    public static MessageExtVo build(MessageVo messageVo) {
        MessageExtVo messageExtVo = new MessageExtVo();

        messageExtVo.put("status", messageVo.getStatus());
        messageExtVo.put("message", messageVo.getMessage());
        messageExtVo.put("data", messageVo.getData());

        return messageExtVo;
    }

    public MessageExtVo setMessage(String message) {
        put("message", message);
        return this;
    }
}
