package com.webank.wedatasphere.linkis.oneservice.core.vo;

import java.util.Map;

/**
 * 接口调用request
 *
 * @author lidongzhang
 */
public class QueryRequest {
    /**
     * 模块名，也是登录用户名、密码
     */
    private String moduleName;

    /**
     * 调用参数
     */
    private Map<String, Object> params;

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }
}
