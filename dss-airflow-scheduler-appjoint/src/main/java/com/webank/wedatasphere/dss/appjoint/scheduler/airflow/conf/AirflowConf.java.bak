package com.webank.wedatasphere.dss.appjoint.scheduler.airflow.conf;


import com.google.common.collect.ImmutableMap;
import com.webank.wedatasphere.linkis.common.conf.CommonVars;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.HashMap;

/**
 * Created by Xudong Zhang on 2020/8/6.
 */
public class AirflowConf {

    public static final CommonVars<String> AIRFLOW_HOST = CommonVars.apply("wds.dss.appjoint.scheduler.airflow.host", "");
    public static final CommonVars<String> AIRFLOW_PORT = CommonVars.apply("wds.dss.appjoint.scheduler.airflow.port", "");
    //public static final CommonVars<String> AIRFLOW_HOME = CommonVars.apply("wds.dss.appjoint.scheduler.airflow.home", "");
    public static final CommonVars<String> AIRFLOW_TOKEN_KEY = CommonVars.apply("wds.dss.appjoint.scheduler.airflow.token.key", "");
    public static final CommonVars<String> AIRFLOW_TOKEN = CommonVars.apply("wds.dss.appjoint.scheduler.airflow.token", "");
    public static final CommonVars<String> AIRFLOW_RBAC = CommonVars.apply("wds.dss.appjoint.scheduler.airflow.rbac", "true");
    //public static final CommonVars<String> AIRFLOW_BG_USERNAME = CommonVars.apply("wds.dss.appjoint.scheduler.airflow.bg.username", "");
    //public static final CommonVars<String> AIRFLOW_BG_PASSWORD = CommonVars.apply("wds.dss.appjoint.scheduler.airflow.bg.password", "");

    public static final CommonVars<String> LINKIS_AIRFLOW_CLIENT_BASE_PATH = CommonVars.apply("wds.dss.appjoint.scheduler.airflow.linkis.client.base.path", "/data/airflow/airflow_home/dss_client");
    //public static final CommonVars<String> LINKIS_AIRFLOW_CLIENT_LOG_PROPERTIES_PATH = CommonVars.apply("wds.dss.appjoint.scheduler.airflow.linkis.client.log.properties.path", "");
    //public static final CommonVars<String> LINKIS_AIRFLOW_CLIENT_JAVA_CLASSPATH = CommonVars.apply("wds.dss.appjoint.scheduler.airflow.linkis.client.classpath", "");
    public static final CommonVars<String> LINKIS_AIRFLOW_CLIENT_JAVA_MAIN_CLASS = CommonVars.apply("wds.dss.appjoint.scheduler.airflow.linkis.client.main.class", "");
    public static final CommonVars<String> LINKIS_AIRFLOW_CLIENT_JVM_OPTIONS = CommonVars.apply("wds.dss.appjoint.scheduler.airflow.linkis.client.jvm.options", "-Xms64m -Xmx64m");
    public static final CommonVars<String> LINKIS_AIRFLOW_CLIENT_DEFAULT_MAIL_TO_SUFFIX = CommonVars.apply("wds.dss.appjoint.scheduler.airflow.linkis.client.default.mail.to.suffix", "nio.com");

    public static String getUrl(String endpoint) {
        return String.format("http://%s:%s/%s",
                AIRFLOW_HOST.getValue(),
                AIRFLOW_PORT.getValue(),
                getApi().get(endpoint));
    }

    public static Map<String, String> getApi() {
        return API_MAP;
    }

    public static boolean isRbac() {
        return AIRFLOW_RBAC.getValue().equalsIgnoreCase("true");
    }

    public static final Map<String, String> API_MAP = new HashMap() {
        {
            put("pause", "admin/rest_api/api?api=pause&dag_id={dag_id}");
            put("unpause", "admin/rest_api/api?api=unpause&dag_id={dag_id}");
            put("trigger", "admin/rest_api/api?api=trigger_dag&dag_id={dag_id}&exec_date={exec_date}");
            put("clear", "admin/rest_api/api?api=clear&dag_id={dag_id}&start_date={start_date}&end_date={end_date}");
            put("list_dag_runs", "admin/rest_api/api?api=list_dag_runs&dag_id={dag_id}");
            put("deploy_dag", "admin/rest_api/api?api=deploy_dag");
            put("delete_dag_file", "admin/rest_api/api?api=delete_dag_file&dag_file_path={dag_file_path}");
            put("get_dag_file_content", "admin/rest_api/api?api=get_dag_file_content&dag_file_path={dag_file_path}");
            put("get_log", "admin/rest_api/api?api=get_log&dag_id={dag_id}&task_id={task_id}&execution_date={execution_date}&metadata=null");
            put("get_dags", "admin/rest_api/api?api=get_dags");
            put("mark_failed", "admin/rest_api/api?api=mark_failed&dag_id={dag_id}&execution_date={execution_date}");
            put("run_list", "admin/rest_api/api?api=run_list&dag_id={dag_id}&page={page}&size={size}");
            put("bearer_login", "api/v1/security/login");
            put("refresh_bearer_access_token", "api/v1/security/refresh");
            put("version", "admin/rest_api/api?api=version");
            put("delete_dag", "admin/rest_api/api?api=delete_dag&dag_id={dag_id}");
        }
    };

    public static final CommonVars<String> DEFAULT_STORE_PATH = CommonVars.apply("wds.dss.appjoint.scheduler.project.store.dir", "/appcom/tmp/wds/dss");
}
