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
public enum RequireEnum {

    YES(1, "是"),

    NO(0, "否");

    private Integer index;
    private String name;

    RequireEnum(Integer index, String name) {
        this.index = index;
        this.name = name;
    }

    public static RequireEnum getEnum(Integer index) {
        if (index == null) {
            return NO;
        }
        for (RequireEnum statusEnum : values()) {
            if (statusEnum.getIndex().equals(index)) {
                return statusEnum;
            }
        }
        return NO;
    }

    public Integer getIndex() {
        return this.index;
    }
    
    public String getName() {
        return this.name;
    }
}
