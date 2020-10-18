package com.webank.wedatasphere.dss.appjoint.scheduler.airflow.client;

import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

public class RestTemplateClient {

    private static RestTemplateClient _instance = new RestTemplateClient();

    private RestTemplate restTemplate;

    private RestTemplateClient() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(15000);
        factory.setReadTimeout(5000);

        this.restTemplate = new RestTemplate(factory);
    }

    public static RestTemplateClient getInstance() {
        return _instance;
    }

    public RestTemplate getRestTemplate() {
        return restTemplate;
    }
}
