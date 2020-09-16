/*
 * Copyright (C) 2006-2016 AiJia All rights reserved
 * Author: chongchuanbing
 * Date: 2020-07-14
 * Description:StatusEnum.java
 */
package com.webank.wedatasphere.linkis.oneservice.core.constant;

/**
 * @author chongchuanbing
 */
public enum ParamTypeEnum {

    STRING(1, "String"),

    NUMBER(2, "Number"),

    DATE(3, "Date");

    private Integer index;
    private String name;

    ParamTypeEnum(Integer index, String name) {
        this.index = index;
        this.name = name;
    }

    public static ParamTypeEnum getEnum(Integer index) {
        if (index == null) {
            return STRING;
        }
        for (ParamTypeEnum statusEnum : values()) {
            if (statusEnum.getIndex().equals(index)) {
                return statusEnum;
            }
        }
        return STRING;
    }

    public Integer getIndex() {
        return this.index;
    }
    
    public String getName() {
        return this.name;
    }
}
