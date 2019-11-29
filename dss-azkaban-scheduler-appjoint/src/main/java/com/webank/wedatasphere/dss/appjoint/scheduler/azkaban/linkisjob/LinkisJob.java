package com.webank.wedatasphere.dss.appjoint.scheduler.azkaban.linkisjob;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by cooperyang on 2019/11/1.
 */
public class LinkisJob {
    private String name;
    private String type;
    private String linkistype;
    private String proxyUser;
    private String dependencies;
    private Map<String, String> conf;
    private String command;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLinkistype() {
        return linkistype;
    }

    public void setLinkistype(String linkistype) {
        this.linkistype = linkistype;
    }

    public String getProxyUser() {
        return proxyUser;
    }

    public void setProxyUser(String proxyUser) {
        this.proxyUser = proxyUser;
    }

    public String getDependencies() {
        return dependencies;
    }

    public void setDependencies(String dependencies) {
        this.dependencies = dependencies;
    }

    public Map<String, String> getConf() {
        return conf;
    }

    public void setConf(Map<String, String> conf) {
        this.conf = conf;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

}
