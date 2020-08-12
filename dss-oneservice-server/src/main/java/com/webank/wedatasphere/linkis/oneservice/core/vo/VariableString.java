package com.webank.wedatasphere.linkis.oneservice.core.vo;

/**
 * VariableString
 *
 * @author lidongzhang
 */
public class VariableString {
    private String path;

    public VariableString(String unparsedPath) {
        this.path = unparsedPath;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
