package com.webank.wedatasphere.dss.appconn.dolphinscheduler.constant;

public class Constant {
    public final static String LINKIS_FLOW_VARIABLE_KEY = "flow.variable.";
    public final static String PROPERTIES_SUFFIX = ".properties";
    public final static String LINKIS_JOB_RESOURCES_KEY = "resources=";
    public final static String TOKEN_FILE_NAME = "token.properties";
    public final static String JOB_TYPE = "type";
    public final static String LINKIS_TYPE = "linkistype";
    public final static String JOB_COMMAND = "command";
    public final static String FLOW_CONTEXT_ID = "wds.linkis.flow.contextID=";


    public final static String AIRFLOW_JOB_SUFFIX = ".job";
    public final static String PROXY_USER = "proxy.user";
    public final static String AIRFLOW_PROPERTIES_SUFFIX = ".properties";
    public final static String ZAKABAN_DEPENDENCIES_KEY = "dependencies";
    public final static String PYTHON_FILE_NAME_SUFFIX = ".py";
    public final static String AIRFLOW_CONTEXT_RUN_ID = "run.id";
    public final static String AIRFLOW_CONTEXT_TASK_INSTANCE_KEY_STR = "task_instance_key_str";
    public final static String AIRFLOW_FLOW_ID = "flow.id";
    public final static String AIRFLOW_FLOW_NAME = "flow.name";
    public final static String AIRFLOW_PROJECT_NAME = "project.name";
    public final static String AIRFLOW_SUBMIT_USER = "submit.user";
    public final static String AIRFLOW_USER_TO_PROXY = "user.to.proxy";
    public final static String AIRFLOW_SUBMIT_USER_PLACEHOLDER = "${airflow.submit.user}";

    public final static String FLOW_DUMMY_START_NODE_SUFFIX = "_dummy_start";
    public final static String FLOW_DUMMY_END_NODE_SUFFIX = "_dummy_end";
    public final static String AIRFLOW_TASK_PREFIX = "task_";
    public final static String AIRFLOW_DAG_NAME_FORMAT = "dag__%s__%d"; // project_name + flow_id (不能用flow_name，因为可以被修改)

    public final static String FLOW_PROPERTIES_KEY_AIRFLOW_START_TIME = "dag.start.time";
    public final static String FLOW_PROPERTIES_KEY_AIRFLOW_END_TIME = "dag.end.time";
    public final static String FLOW_PROPERTIES_KEY_AIRFLOW_SCHEDULE_INTERVAL = "dag.schedule.interval";

    /**
     * dolphin scheduler result code.
     */
    public final static int DS_RESULT_CODE_SUCCESS = 0;
    public final static int DS_RESULT_CODE_PROJECT_ALREADY_EXISTS = 10019;
}
