package com.webank.wedatasphere.dss.git.common.protocol;

public class GitSearchLine {
    private Integer number;
    private String line;

    public GitSearchLine(Integer number, String line) {
        this.number = number;
        this.line = line;
    }

    public GitSearchLine() {
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }
}
