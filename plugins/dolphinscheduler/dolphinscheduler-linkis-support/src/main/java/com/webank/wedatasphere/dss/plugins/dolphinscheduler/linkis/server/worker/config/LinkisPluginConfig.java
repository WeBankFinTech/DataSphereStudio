package com.webank.wedatasphere.dss.plugins.dolphinscheduler.linkis.server.worker.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource(value = "linkis-plugin.properties")
public class LinkisPluginConfig {

    @Value("${linkis.engine.support:false}")
    private boolean linkisEngineSupport;

    @Value("${linkis.gateway.url:http://127.0.0.1:8001}")
    private String linkisGatewayUrl;

    @Value("${linkis.deploy.user:hadoop}")
    private String linkisDeployUser;

    @Value("${linkis.client.common.tokenKey:username}")
    private String linkisClientTokenKey;

    @Value("${linkis.client.common.tokenValue:password}")
    private String linkisClientTokenValue;

    @Value("${linkis.engine.default.yarnqueue:default}")
    private String linkisEngineDefaultYarnQueue;

    @Value("${linkis.engine.use.creater:dolphin}")
    private String linkisEngineUseCreater;

    @Value("${linkis.engine.hive.type:hive-2.3.7}")
    private String linkisEngineHiveType;

    @Value("${linkis.engine.spark.type:spark-2.4.3}")
    private String linkisEngineSparkType;

    @Value("${linkis.engine.jdbc.type:jdbc-4}")
    private String linkisEngineJdbcType;

    @Value("${linkis.engine.shell.type:shell-1}")
    private String linkisEngineShellType;

    @Value("${linkis.engine.flink.type:flink-1.12.2}")
    private String linkisEngineFlinkType;

    @Value("${linkis.engine.python.type:python-python2}")
    private String linkisEnginePythonType;

    @Value("${linkis.sql.default.limit:5000}")
    private int linkisSqlDefaultLimit;

    @Value("${worker.linkis.job.name:dolphin-scheduler}")
    private String linkisJobName;

    @Value("${worker.linkis.plugin.scheduler.class:default}")
    private String linkisPluginSchedulerClass;

    @Value("${worker.linkis.plugin.scheduler.path:default}")
    private String linkisPluginSchedulerPath;

    public boolean isLinkisEngineSupport() {
        return linkisEngineSupport;
    }

    public void setLinkisEngineSupport(boolean linkisEngineSupport) {
        this.linkisEngineSupport = linkisEngineSupport;
    }

    public String getLinkisGatewayUrl() {
        return linkisGatewayUrl;
    }

    public void setLinkisGatewayUrl(String linkisGatewayUrl) {
        this.linkisGatewayUrl = linkisGatewayUrl;
    }

    public String getLinkisDeployUser() {
        return linkisDeployUser;
    }

    public void setLinkisDeployUser(String linkisDeployUser) {
        this.linkisDeployUser = linkisDeployUser;
    }

    public String getLinkisClientTokenKey() {
        return linkisClientTokenKey;
    }

    public void setLinkisClientTokenKey(String linkisClientTokenKey) {
        this.linkisClientTokenKey = linkisClientTokenKey;
    }

    public String getLinkisClientTokenValue() {
        return linkisClientTokenValue;
    }

    public void setLinkisClientTokenValue(String linkisClientTokenValue) {
        this.linkisClientTokenValue = linkisClientTokenValue;
    }

    public String getLinkisEngineDefaultYarnQueue() {
        return linkisEngineDefaultYarnQueue;
    }

    public void setLinkisEngineDefaultYarnQueue(String linkisEngineDefaultYarnQueue) {
        this.linkisEngineDefaultYarnQueue = linkisEngineDefaultYarnQueue;
    }

    public String getLinkisEngineUseCreater() {
        return linkisEngineUseCreater;
    }

    public void setLinkisEngineUseCreater(String linkisEngineUseCreater) {
        this.linkisEngineUseCreater = linkisEngineUseCreater;
    }

    public String getLinkisEngineHiveType() {
        return linkisEngineHiveType;
    }

    public void setLinkisEngineHiveType(String linkisEngineHiveType) {
        this.linkisEngineHiveType = linkisEngineHiveType;
    }

    public String getLinkisEngineSparkType() {
        return linkisEngineSparkType;
    }

    public void setLinkisEngineSparkType(String linkisEngineSparkType) {
        this.linkisEngineSparkType = linkisEngineSparkType;
    }

    public String getLinkisEngineJdbcType() {
        return linkisEngineJdbcType;
    }

    public void setLinkisEngineJdbcType(String linkisEngineJdbcType) {
        this.linkisEngineJdbcType = linkisEngineJdbcType;
    }

    public String getLinkisEngineFlinkType() {
        return linkisEngineFlinkType;
    }

    public void setLinkisEngineFlinkType(String linkisEngineFlinkType) {
        this.linkisEngineFlinkType = linkisEngineFlinkType;
    }

    public String getLinkisJobName() {
        return linkisJobName;
    }

    public void setLinkisJobName(String linkisJobName) {
        this.linkisJobName = linkisJobName;
    }

    public String getLinkisPluginSchedulerClass() {
        return linkisPluginSchedulerClass;
    }

    public void setLinkisPluginSchedulerClass(String linkisPluginSchedulerClass) {
        this.linkisPluginSchedulerClass = linkisPluginSchedulerClass;
    }

    public String getLinkisPluginSchedulerPath() {
        return linkisPluginSchedulerPath;
    }

    public void setLinkisPluginSchedulerPath(String linkisPluginSchedulerPath) {
        this.linkisPluginSchedulerPath = linkisPluginSchedulerPath;
    }

    public int getLinkisSqlDefaultLimit() {
        return linkisSqlDefaultLimit;
    }

    public void setLinkisSqlDefaultLimit(int linkisSqlDefaultLimit) {
        this.linkisSqlDefaultLimit = linkisSqlDefaultLimit;
    }

    public String getLinkisEngineShellType() {
        return linkisEngineShellType;
    }

    public void setLinkisEngineShellType(String linkisEngineShellType) {
        this.linkisEngineShellType = linkisEngineShellType;
    }

    public String getLinkisEnginePythonType() {
        return linkisEnginePythonType;
    }

    public void setLinkisEnginePythonType(String linkisEnginePythonType) {
        this.linkisEnginePythonType = linkisEnginePythonType;
    }
}
