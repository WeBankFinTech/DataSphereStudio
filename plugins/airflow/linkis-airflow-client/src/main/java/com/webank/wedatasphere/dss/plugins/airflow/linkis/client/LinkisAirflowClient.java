package com.webank.wedatasphere.dss.plugins.airflow.linkis.client;


/*
import com.webank.wedatasphere.linkis.common.utils.Utils;
import com.webank.wedatasphere.linkis.httpclient.dws.authentication.StaticAuthenticationStrategy;
import com.webank.wedatasphere.linkis.httpclient.dws.config.DWSClientConfig;
import com.webank.wedatasphere.linkis.httpclient.dws.config.DWSClientConfigBuilder;
import com.webank.wedatasphere.linkis.ujes.client.UJESClient;
import com.webank.wedatasphere.linkis.ujes.client.UJESClientImpl;
import com.webank.wedatasphere.linkis.ujes.client.request.JobExecuteAction;
import com.webank.wedatasphere.linkis.ujes.client.request.ResultSetAction;
import com.webank.wedatasphere.linkis.ujes.client.response.JobExecuteResult;
import com.webank.wedatasphere.linkis.ujes.client.response.JobInfoResult;
import com.webank.wedatasphere.linkis.ujes.client.response.JobProgressResult;
import com.webank.wedatasphere.linkis.ujes.client.response.JobStatusResult;

 */
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sun.misc.Signal;
import sun.misc.SignalHandler;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class LinkisAirflowClient {

    private static Logger log = LogManager.getLogger(LinkisAirflowClient.class);

    private static AirflowDssJobType airflowDssJobType;

    public static final String DEFAULT_PROPERTY_FILE_NAME = "linkis.properties";
    public static final String DEFAULT_CONFIG_DIR = "conf";
    public static final String CHARSET_NAME = "utf-8";

    public static Properties parseConfigFromLinkisPropertiesFile(String filePath) {
        Properties config = new Properties();
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        try {
            inputStream = new FileInputStream(filePath);
            inputStreamReader = new InputStreamReader(inputStream, CHARSET_NAME);
            config.load(inputStreamReader);
        } catch (Exception e) {
            log.error("Can't load " + filePath, e);
        } finally {
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(inputStreamReader);
        }
        return config;
    }

    public static Map<String, String> parseKVMapFromArgs(String[] args) {
        Map<String, String> ret = new HashMap<>();
        for (String arg: args) {
            String[] kvArr = arg.split("=", 2);
            if (kvArr.length == 2) {
                ret.put(kvArr[0], kvArr[1]);
            }
        }
        return ret;
    }

    // https://www.zhihu.com/question/55869263
    public static void addJobCancelHandlers() {
        SignalHandler signalHandler = new MySignalHandler();

        Signal.handle(new Signal("HUP"), signalHandler); // kill -1 PID
        Signal.handle(new Signal("INT"), signalHandler); // kill -2 PID
        // already used by VM or OS: SIGQUIT
        // Signal.handle(new Signal("QUIT"), signalHandler); // kill -3 PID
        Signal.handle(new Signal("ABRT"), signalHandler); // kill -6 PID
        // already used by VM or OS: SIGKILL
        // Signal.handle(new Signal("KILL"), signalHandler); // kill -9 PID
        Signal.handle(new Signal("ALRM"), signalHandler); // kill -14 PID
        Signal.handle(new Signal("TERM"), signalHandler); // kill -15 PID
    }

    public static void main(String[] args) throws Exception {

        log.info("Linkis Airflow Client Started...");

        Map<String, String> kvMap = parseKVMapFromArgs(args);
        log.info("KV map parsed from command lines...");
        for (Map.Entry<String, String> entry: kvMap.entrySet()) {
            log.info(String.format("%s=%s", entry.getKey(), entry.getValue()));
        }

        String propertyFile = System.getProperties().getOrDefault("wds.linkis.configuration", DEFAULT_PROPERTY_FILE_NAME).toString();
        java.net.URL configFileURL = Thread.currentThread().getContextClassLoader().getResource(propertyFile);
        String linkisConfigFilePath = DEFAULT_CONFIG_DIR + File.separator + DEFAULT_PROPERTY_FILE_NAME;
        if (configFileURL != null && new File(configFileURL.getPath()).exists()) {
            linkisConfigFilePath = configFileURL.getPath();
        }
        Properties linkisProperties = parseConfigFromLinkisPropertiesFile(linkisConfigFilePath);
        log.info("Linkis properties path: " + linkisConfigFilePath);
        for (Map.Entry<Object, Object> entry: linkisProperties.entrySet()) {
            String k = entry.getKey().toString();
            String v = entry.getValue().toString();
            if (!kvMap.containsKey(k)) {
                kvMap.put(k, v);
            }
        }
        log.info("Linkis properties loaded.");

        airflowDssJobType = new AirflowDssJobType(kvMap, log);

        /*
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run()
            {
                log.info("Shutdown hook ran!");
                try {
                    airflowDssJobType.cancel();
                } catch (Exception e) {
                    log.error("Cancel job " + airflowDssJobType.getJob().getJobName() + " failed!", e);
                }
            }
        });
         */

        addJobCancelHandlers();

        log.info("Start to run airflow dss jobs...");
        airflowDssJobType.run();

        /*

        // 1. To do the configuration, an instance of DWSClientConfig should be obtained from DWSClientBuilder.
        DWSClientConfig clientConfig = ((DWSClientConfigBuilder) (DWSClientConfigBuilder.newBuilder()
                .addUJESServerUrl("http://${ip}:${port}")  //Specify the ServerUrl，The address of Willink gateway, i.e. http://{ip}:{port}
                .connectionTimeout(30000)   //connectionTimeOut: The connection timeout of the client
                .discoveryEnabled(true).discoveryFrequency(1, TimeUnit.MINUTES)  //Enable service discovery. Once enabled, newly started Gateway will be auto-dicovered.
                .loadbalancerEnabled(true)  // Enable load balancing. Cannot be enabled alone without service discovery enabled.
                .maxConnectionSize(5)   //Max connection size, aka the max concurrent threshold
                .retryEnabled(false).readTimeout(30000)   //whether to retry after failure
                .setAuthenticationStrategy(new StaticAuthenticationStrategy())   //AuthenticationStrategy, The authentication strategy of Linkis
                .setAuthTokenKey("${username}").setAuthTokenValue("${password}")))  //The authentication key，usually the username;The authentication value，usually the password
                .setDWSVersion("v1").build();  //The version of Linkis background protocol, currently v1

        // 2. Create a UJESClient from DWSClientConfig
        UJESClient client = new UJESClientImpl(clientConfig);

        // 3. Begin to execute the code
        JobExecuteResult jobExecuteResult = client.execute(JobExecuteAction.builder()
                .setCreator("LinkisClient-Test")  //creator. The name of the system which holds the UJES client, used for system level isolation.
                .addExecuteCode("show tables")   //ExecutionCode. The code which is requested to be executed
                .setEngineType((JobExecuteAction.EngineType)(JobExecuteAction.EngineType$.MODULE$.HIVE())) // The engine type expected by the client, i.e. Spark, Hive, etc...
                .setUser("johnnwang")   //User, The user who makes this request；Used for user level multi-tenant isolation
                .build());
        System.out.println("execId: " + jobExecuteResult.getExecID() + ", taskId: " + jobExecuteResult.taskID());

        // 4. Synch the status of script execution
        JobStatusResult status = client.status(jobExecuteResult);
        while(!status.isCompleted()) {
            // 5. Synch the status of script execution
            JobProgressResult progress = client.progress(jobExecuteResult);
            Utils.sleepQuietly(500);
            status = client.status(jobExecuteResult);
        }

        // 6. Synch the job information of script execution
        JobInfoResult jobInfo = client.getJobInfo(jobExecuteResult);
        // 7. Fetch the list of result sets(Multiple result sets will be generated if a user submitted multiple SQL at once)
        String resultSet = jobInfo.getResultSetList(client)[0];
        // 8. Fetch detailed result set content with a particular result set info
        Object fileContents = client.resultSet(ResultSetAction.builder().setPath(resultSet).setUser(jobExecuteResult.getUser()).build()).getFileContent();
        System.out.println("fileContents: " + fileContents);
        IOUtils.closeQuietly(client);

         */
    }

    @SuppressWarnings("restriction")
    public static class MySignalHandler implements SignalHandler {

        @Override
        public void handle(Signal signal) {
            // 信号量名称
            String name = signal.getName();
            // 信号量数值
            int number = signal.getNumber();
            // 当前进程名
            String currentThreadName = Thread.currentThread().getName();
            System.out.println("[Thread:"+currentThreadName + "] received signal: " + name + " == kill -" + number);
            if(name.equals("TERM") || name.equals("INT") || name.equals("HUP") || name.equals("ABRT")){
                try {
                    airflowDssJobType.cancel();
                } catch (Exception e) {
                    log.error("Cancel job " + airflowDssJobType.getJob().getJobName() + " failed!", e);
                }
                System.exit(1);
            }
        }
    }
}
