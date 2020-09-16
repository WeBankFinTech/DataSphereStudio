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
public enum StatusEnum {

    DISABLE(0, "停止"),

    ENABLE(1, "运行中");

    private Integer index;
    private String name;

    StatusEnum(Integer index, String name) {
        this.index = index;
        this.name = name;
    }

    public static StatusEnum getEnum(Integer index) {
        if (index == null) {
            return null;
        }
        for (StatusEnum statusEnum : values()) {
            if (statusEnum.getIndex().equals(index)) {
                return statusEnum;
            }
        }
        return null;
    }

    public Integer getIndex() {
        return this.index;
    }
    
    public String getName() {
        return this.name;
    }
}
