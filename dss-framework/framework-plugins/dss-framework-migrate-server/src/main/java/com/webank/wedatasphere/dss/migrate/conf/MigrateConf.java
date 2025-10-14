package com.webank.wedatasphere.dss.migrate.conf;

import org.apache.linkis.common.conf.CommonVars;

public class MigrateConf {

    public static final String ORC_SERVER_NAME =
            CommonVars.apply("wds.dss.framework.migrate.orc.name", "dss-framework-orchestrator-server-dev").getValue();

    public static final String WORKFLOW_SERVER_NAME =
            CommonVars.apply("wds.dss.workflow.server.name", "dss-workflow-server-dev").getValue();
}
