package com.webank.wedatasphere.dss.plugins.dolphinscheduler.linkis.server.entity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * linkis task execution context
 */
public class LinkisTaskExecutionContext implements Serializable {

    /**
     * 登录用户
     */
    private String username ;
    /**
     * 登录密码
     */
    private String password ;
    /**
     * 实际执行用户
     */
    private String executeUser ;
    /**
     * linkis gateway地址
     */
    private String gatewayUrl ;
    /**
     * 执行代码
     */
    private String executeCode ;
    /**
     * 前置执行代码
     */
    private String preExecuteCode ;
    /**
     * 后置执行代码
     */
    private String postExecuteCode ;
    /**
     * 运行脚本类型，如：sql、jdbc、scala、shell等
     */
    private String runType ;
    /**
     * 引擎参数。如yarn队列、spark引擎内存、cpu配置等
     */
    private Map<String,Object> startupParams = new HashMap<>();
    /**
     * 引擎label信息，引擎类型、引擎标签等信息
     */
    private Map<String,Object> labelsMap = new HashMap<>();
    /**
     * 变量替换，executeCode中的参数
     */
    private Map<String,Object> variableMap = new HashMap<>();
    /**
     * sourceMap信息，非必须。如任务名称、工作流名称等
     */
    private Map<String,Object> sourceMap = new HashMap<>();

    /**
     * linkis 外部jar的类
     */
    public String linkisOutJarClass;

    /**
     * linkis 外部jar包的位置
     */
    public String linkisOutJarPath;

    public LinkisTaskExecutionContext(String username, String password, String executeUser, String gatewayUrl, String executeCode, String runType, Map<String, Object> startupParams, Map<String, Object> labelsMap, Map<String, Object> variableMap, Map<String, Object> sourceMap, String linkisOutJarClass, String linkisOutJarPath) {
        this.username = username;
        this.password = password;
        this.executeUser = executeUser;
        this.gatewayUrl = gatewayUrl;
        this.executeCode = executeCode;
        this.runType = runType;
        this.startupParams = startupParams;
        this.labelsMap = labelsMap;
        this.variableMap = variableMap;
        this.sourceMap = sourceMap;
        this.linkisOutJarClass = linkisOutJarClass;
        this.linkisOutJarPath = linkisOutJarPath;
    }

    public LinkisTaskExecutionContext(String username, String password, String executeUser, String gatewayUrl, String executeCode, String preExecuteCode, String postExecuteCode, String runType, Map<String, Object> startupParams, Map<String, Object> labelsMap, Map<String, Object> variableMap, Map<String, Object> sourceMap, String linkisOutJarClass, String linkisOutJarPath) {
        this.username = username;
        this.password = password;
        this.executeUser = executeUser;
        this.gatewayUrl = gatewayUrl;
        this.executeCode = executeCode;
        this.preExecuteCode = preExecuteCode;
        this.postExecuteCode = postExecuteCode;
        this.runType = runType;
        this.startupParams = startupParams;
        this.labelsMap = labelsMap;
        this.variableMap = variableMap;
        this.sourceMap = sourceMap;
        this.linkisOutJarClass = linkisOutJarClass;
        this.linkisOutJarPath = linkisOutJarPath;
    }

    public LinkisTaskExecutionContext(){}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getExecuteUser() {
        return executeUser;
    }

    public void setExecuteUser(String executeUser) {
        this.executeUser = executeUser;
    }

    public String getGatewayUrl() {
        return gatewayUrl;
    }

    public void setGatewayUrl(String gatewayUrl) {
        this.gatewayUrl = gatewayUrl;
    }

    public String getExecuteCode() {
        return executeCode;
    }

    public void setExecuteCode(String executeCode) {
        this.executeCode = executeCode;
    }

    public String getRunType() {
        return runType;
    }

    public void setRunType(String runType) {
        this.runType = runType;
    }

    public Map<String, Object> getStartupParams() {
        return startupParams;
    }

    public void setStartupParams(Map<String, Object> startupParams) {
        this.startupParams = startupParams;
    }

    public Map<String, Object> getLabelsMap() {
        return labelsMap;
    }

    public void setLabelsMap(Map<String, Object> labelsMap) {
        this.labelsMap = labelsMap;
    }

    public Map<String, Object> getVariableMap() {
        return variableMap;
    }

    public void setVariableMap(Map<String, Object> variableMap) {
        this.variableMap = variableMap;
    }

    public Map<String, Object> getSourceMap() {
        return sourceMap;
    }

    public void setSourceMap(Map<String, Object> sourceMap) {
        this.sourceMap = sourceMap;
    }

    public String getLinkisOutJarClass() {
        return linkisOutJarClass;
    }

    public void setLinkisOutJarClass(String linkisOutJarClass) {
        this.linkisOutJarClass = linkisOutJarClass;
    }

    public String getLinkisOutJarPath() {
        return linkisOutJarPath;
    }

    public void setLinkisOutJarPath(String linkisOutJarPath) {
        this.linkisOutJarPath = linkisOutJarPath;
    }

    public String getPreExecuteCode() {
        return preExecuteCode;
    }

    public void setPreExecuteCode(String preExecuteCode) {
        this.preExecuteCode = preExecuteCode;
    }

    public String getPostExecuteCode() {
        return postExecuteCode;
    }

    public void setPostExecuteCode(String postExecuteCode) {
        this.postExecuteCode = postExecuteCode;
    }

    @Override
    public String toString() {
        return "LinkisTaskExecuionContext{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", executeUser='" + executeUser + '\'' +
                ", gatewayUrl='" + gatewayUrl + '\'' +
                ", executeCode='" + executeCode + '\'' +
                ", preExecuteCode='" + preExecuteCode + '\'' +
                ", postExecuteCode='" + postExecuteCode + '\'' +
                ", runType='" + runType + '\'' +
                ", startupParams=" + startupParams +
                ", labelsMap=" + labelsMap +
                ", variableMap=" + variableMap +
                ", sourceMap=" + sourceMap +
                ", linkisOutJarClass='" + linkisOutJarClass + '\'' +
                ", linkisOutJarPath='" + linkisOutJarPath + '\'' +
                '}';
    }
}
