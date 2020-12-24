package com.webank.wedatasphere.dss.appjoint.scheduler.airflow.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.webank.wedatasphere.dss.appjoint.scheduler.airflow.bean.response.*;
import com.webank.wedatasphere.dss.appjoint.scheduler.airflow.common.Pair;
import com.webank.wedatasphere.dss.appjoint.scheduler.airflow.conf.AirflowConf;
import com.webank.wedatasphere.dss.appjoint.scheduler.airflow.constant.AirflowConstant;
import com.webank.wedatasphere.dss.appjoint.scheduler.airflow.util.BearerTokenRenewer;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.client.support.HttpRequestWrapper;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AirflowClient {

    //private static AirflowClient _instance =  new AirflowClient();

    private final RestTemplate rest = RestTemplateClient.getInstance().getRestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final BearerTokenRenewer tokenRenewer;

    public static final int AIRFLOW_RESPONSE_PARSE_ERROR_CODE = 90021;
    public static final int AIRFLOW_RESPONSE_INTERVAL_ERROR_CODE = 90022;

    //public static AirflowClient getInstance() {
    //    return _instance;
    //}

    public AirflowClient(String username, String password) {
        this.rest.getInterceptors().add((request, body, execution) -> {
            String airflowTokenKey = AirflowConf.AIRFLOW_TOKEN_KEY.getValue();
            String airflowToken = AirflowConf.AIRFLOW_TOKEN.getValue();
            if (!airflowTokenKey.isEmpty() && !airflowToken.isEmpty()) {
                request.getHeaders().set(airflowTokenKey, airflowToken);
            }
            return execution.execute(new HttpRequestWrapper(request) {
                @Override
                public URI getURI() {
                    URI u = super.getURI();
                    String strictlyEscapedQuery = StringUtils.replace(u.getRawQuery(), "+", "%2B");
                    return UriComponentsBuilder.fromUri(u)
                            .replaceQuery(strictlyEscapedQuery)
                            .build(true).toUri();
                }
            }, body);
        });
        if (AirflowConf.isRbac()) {
            tokenRenewer = new BearerTokenRenewer(username, password);
            this.rest.getInterceptors().add((request, body, execution) -> {
                if(!request.getHeaders().containsKey("Authorization")) {
                    request.getHeaders().set("Authorization", "Bearer " + tokenRenewer.getBearerAccessToken());
                }
                return execution.execute(request, body);
            });
        } else
            tokenRenewer = null;
    }

    public BearerTokenRenewer getBearerTokenRenewer() {
        return tokenRenewer;
    }

    private String getUrl(String endpoint) {
        return String.format("http://%s:%s%s", AirflowConf.AIRFLOW_HOST.getValue(), AirflowConf.AIRFLOW_PORT.getValue(), AirflowConf.getApi().get(endpoint));
    }

    private void checkResError(AirflowResponse response) throws DSSErrorException {
        if (response == null) {
            throw new DSSErrorException(AIRFLOW_RESPONSE_INTERVAL_ERROR_CODE, "api failure");
        } else if (response.getHttp_response_code() == null || response.getHttp_response_code() < 200 || response.getHttp_response_code() >= 300) {
            throw new DSSErrorException(AIRFLOW_RESPONSE_INTERVAL_ERROR_CODE, response.getOutput());
        } else if (!response.getStatus().equals("OK")) {
            throw new DSSErrorException(AIRFLOW_RESPONSE_INTERVAL_ERROR_CODE, response.getOutput());
        }
    }

    private void checkResError(AirflowResponseAlter response) throws DSSErrorException {
        if (response == null) {
            throw new DSSErrorException(AIRFLOW_RESPONSE_INTERVAL_ERROR_CODE, "api failure");
        } else if (response.getOutput() != null && !Strings.isNullOrEmpty(response.getOutput().getStderr())) {
            throw new DSSErrorException(AIRFLOW_RESPONSE_INTERVAL_ERROR_CODE, response.getOutput().getStderr());
        } else if (!response.getStatus().equals("OK")) {
            throw new DSSErrorException(AIRFLOW_RESPONSE_INTERVAL_ERROR_CODE, response.getOutput().toString());
        }
    }

    private Object checkResError(String rawResponse) throws DSSErrorException {
        AirflowResponse airflowResponse = null;
        AirflowResponseAlter airflowResponseAlter = null;
        try {
            airflowResponse = objectMapper.readValue(rawResponse, AirflowResponse.class);
        } catch (JsonProcessingException e) {
            try {
                airflowResponseAlter = objectMapper.readValue(rawResponse, AirflowResponseAlter.class);
            } catch (JsonProcessingException e2) {
                throw new DSSErrorException(AIRFLOW_RESPONSE_PARSE_ERROR_CODE, e.toString());
            }
        }
        if (airflowResponse != null) {
            checkResError(airflowResponse);
            return airflowResponse;
        } else {
            checkResError(airflowResponseAlter);
            return airflowResponseAlter;
        }
    }

    private String getNormalStdOutput(Object parsedResponse) throws DSSErrorException {
        if (parsedResponse instanceof AirflowResponse) {
            return ((AirflowResponse) parsedResponse).getOutput();
        } else if (parsedResponse instanceof AirflowResponseAlter) {
            return ((AirflowResponseAlter) parsedResponse).getOutput().getStdout();
        } else {
            throw new DSSErrorException(AIRFLOW_RESPONSE_PARSE_ERROR_CODE, "empty airflow response");
        }
    }

    public void uploadJob(String projectName, Long flowId, String dagContent) throws DSSErrorException {
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        String response;
        params.set("force", "on");
        String scheduleName = String.format(AirflowConstant.AIRFLOW_DAG_NAME_FORMAT, projectName, flowId);
        ByteArrayResource dagFile = new ByteArrayFile(dagContent.getBytes(), String.format("%s/%s.py", projectName, scheduleName));
        params.set("dag_file", dagFile);
        response = rest.postForObject(getUrl("deploy_dag"), params, String.class);
        checkResError(response);
    }

    public void deleteDagFile(String projectName, Long flowId) throws DSSErrorException {
        String scheduleName = String.format(AirflowConstant.AIRFLOW_DAG_NAME_FORMAT, projectName, flowId);
        String dagFilePath = String.format("%s/%s.py", projectName, scheduleName);
        String response = rest.getForObject(getUrl("delete_dag_file"), String.class, dagFilePath);
        checkResError(response);
    }

    public String getDagFileContent(String projectName, Long flowId) throws DSSErrorException {
        String scheduleName = String.format(AirflowConstant.AIRFLOW_DAG_NAME_FORMAT, projectName, flowId);
        String dagFilePath = String.format("%s/%s.py", projectName, scheduleName);
        String response = rest.getForObject(getUrl("get_dag_file_content"), String.class, dagFilePath);
        Object parsedResponse = checkResError(response);
        String encodedDagFileContent = getNormalStdOutput(parsedResponse);
        if (encodedDagFileContent != null) {
            try {
             return new String(Base64.getDecoder().decode(encodedDagFileContent), StandardCharsets.UTF_8);
            } catch (Exception e) {
                throw new DSSErrorException(AIRFLOW_RESPONSE_PARSE_ERROR_CODE, "Decode dag file content failed");
            }
        }
        return null;
    }

    public void deleteDag(String scheduleName) throws DSSErrorException {
        String response = rest.getForObject(getUrl("delete_dag"), String.class, scheduleName);
        checkResError(response);
    }

    public void unpause(String scheduleName) throws DSSErrorException {
        String response = rest.getForObject(getUrl("unpause"), String.class, scheduleName);
        checkResError(response);
    }

    public void pause(String scheduleName) throws DSSErrorException {
        String response = rest.getForObject(getUrl("pause"), String.class, scheduleName);
        checkResError(response);
    }

    public void trigger(String scheduleName, String startDate) throws DSSErrorException {
        String response = rest.getForObject(getUrl("trigger"), String.class, scheduleName, startDate);
        checkResError(response);
    }

    public void clear(String scheduleName, String startDate, String endDate) throws DSSErrorException {
        String response = rest.getForObject(getUrl("clear"), String.class,
                scheduleName, startDate, endDate);
        checkResError(response);
    }

    //for test
    protected String getVersion() throws DSSErrorException {
        String response = rest.getForObject(getUrl("version"), String.class);
        Object parsedResponse = checkResError(response);
        return getNormalStdOutput(parsedResponse);
    }

    public void markFailed(String scheduleName, String executionDate) throws DSSErrorException {
        String response = rest.getForObject(getUrl("mark_failed"), String.class, scheduleName, executionDate);
        checkResError(response);
    }

    // dag run states: success, running, failed
    // TODO: filter by start/end
    public List<JobStateResponse.JobState> getJobState(String scheduleName, String startDate, String endDate) throws DSSErrorException {
        String response = rest.getForObject(getUrl("list_dag_runs"), String.class, scheduleName);
        Object parsedResponse = checkResError(response);
        String[] dagRuns = getNormalStdOutput(parsedResponse).split("\n");
        return IntStream.range(7, dagRuns.length).mapToObj(i -> {
            String[] run = dagRuns[i].split("\\|");
            return new JobStateResponse.JobState(Integer.valueOf(run[0].trim()), run[2].trim(), run[3].trim(), run[4].trim());
        }).collect(Collectors.toList());
    }

    public String getTaskLog(String scheduleName, String taskId, String executionDate) throws DSSErrorException {
        String response = rest.getForObject(getUrl("get_log"), String.class, scheduleName, taskId, executionDate);
        Object parsedResponse = checkResError(response);
        return getNormalStdOutput(parsedResponse);
    }

    public List<DagInfoResponse.DagInfo> list(List<String> flows) throws DSSErrorException {
        String response = rest.postForObject(getUrl("get_dags"), flows, String.class);
        Object parsedResponse = checkResError(response);
        String json = getNormalStdOutput(parsedResponse);
        try {
            return Arrays.asList(objectMapper.readValue(json, DagInfoResponse.DagInfo[].class));
        } catch (JsonProcessingException e) {
            throw new DSSErrorException(AIRFLOW_RESPONSE_PARSE_ERROR_CODE, e.toString());
        }
    }

    public Object getRunList(String scheduleName, Integer page, Integer size) throws DSSErrorException {
        String response = rest.getForObject(getUrl("run_list"), String.class, scheduleName, page, size);
        Object parsedResponse = checkResError(response);
        String json = getNormalStdOutput(parsedResponse);
        try {
            return objectMapper.readValue(json, RunListResponse.class);
        } catch (JsonProcessingException e) {
            throw new DSSErrorException(AIRFLOW_RESPONSE_PARSE_ERROR_CODE, e.toString());
        }
    }

    static class ByteArrayFile extends ByteArrayResource {
        private final String fileName;
        public ByteArrayFile(byte[] byteArray, String fileName) {
            super(byteArray);
            this.fileName = fileName;
        }
        public String getFilename() {
            return fileName;
        }
    }
}
