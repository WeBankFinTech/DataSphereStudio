/*
 * Copyright (C) 2006-2016 AiJia All rights reserved
 * Author: chongchuanbing
 * Date: 2020/7/14
 * Description:TestParamVo.java
 */
package com.webank.wedatasphere.linkis.oneservice.core.vo;

/**
 * @author chongchuanbing
 */
public class TestParamVo extends ParamVo {
    
    private String typeStr;
    
    private String requireStr;
    
    private String testValue;

    public String getTypeStr() {
        return typeStr;
    }

    public void setTypeStr(String typeStr) {
        this.typeStr = typeStr;
    }

    public String getRequireStr() {
        return requireStr;
    }

    public void setRequireStr(String requireStr) {
        this.requireStr = requireStr;
    }

    public String getTestValue() {
        return testValue;
    }

    public void setTestValue(String testValue) {
        this.testValue = testValue;
    }
}
