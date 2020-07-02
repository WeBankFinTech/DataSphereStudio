package com.webank.wedatasphere.project;


import com.google.gson.Gson;
import com.webank.wedatasphere.Md5Utils;
import com.webank.wedatasphere.dss.appjoint.exception.AppJointErrorException;
import com.webank.wedatasphere.dss.appjoint.service.AppJointUrlImpl;
import com.webank.wedatasphere.dss.appjoint.service.ProjectService;
import com.webank.wedatasphere.dss.appjoint.service.session.Session;
import com.webank.wedatasphere.dss.common.entity.project.DSSProject;
import com.webank.wedatasphere.dss.common.entity.project.Project;
import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * @author howeye
 */
public class QualitisProjectServiceImpl extends AppJointUrlImpl implements ProjectService {

    private static final String CREATE_PROJECT_PATH = "/qualitis/outer/api/v1/project/workflow";
    private static final String DELETE_PROJECT_PATH = "/qualitis/outer/api/v1/project/workflow/delete";
    private static final String UPDATE_PROJECT_PATH = "/qualitis/outer/api/v1/project/workflow";
    private static final String AUTO_ADD_USER_PATH = "/qualitis/outer/api/v1/user/{username}";

    private String appId = "linkis_id";
    private String appToken = "***REMOVED***";

    private String host = "localhost";
    private Integer port = 8090;

    private static final String FAILURE_CODE = "400";

    private static final Logger LOGGER = LoggerFactory.getLogger(QualitisProjectServiceImpl.class);

    public void init(Map<String, Object> map) throws AppJointErrorException {
        appId = (String) map.get("qualitis_appId");
        appToken = (String) map.get("qualitis_appToken");
        host = getHost();
        port = getPort();
    }

    @Override
    public List<Project> fetchProjects(Session session) throws AppJointErrorException {
        return null;
    }

    @Override
    public Project createProject(Project project, Session session) throws AppJointErrorException {
        try {
            QualitisAddProjectRequest qualitisAddProjectRequest = new QualitisAddProjectRequest();

            DSSProject DSSProject = (DSSProject) project;
            qualitisAddProjectRequest.setProjectName(project.getName());
            qualitisAddProjectRequest.setDescription(project.getDescription());
            qualitisAddProjectRequest.setUsername(DSSProject.getUserName());

            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<Object> entity = generateEntity(qualitisAddProjectRequest);

            Map<String, Object> response = createProjectReal(restTemplate, entity);

            String responseStatus = (String) response.get("code");
            if (FAILURE_CODE.equals(responseStatus)) {
                // Send request to auto create qualitis user
                autoAddUser(restTemplate, DSSProject.getUserName());

                // restart to create project
                response = createProjectReal(restTemplate, entity);
            }

            if (!checkResponse(response)) {
                String message = (String) response.get("message");
                String errorMessage = String.format("Error! Can not add project, exception: %s", message);
                LOGGER.error(errorMessage);
                throw new AppJointErrorException(500, errorMessage);
            }

            Long projectId = Long.valueOf(String.valueOf(((Map<String, Object>) ((Map<String, Object>) response.get("data")).get("project_detail")).get("project_id")));
            project.setId(projectId);

            LOGGER.info("Succeed to add project to qualitis, project_id: {}", project.getId());
            return project;
        } catch (Exception e) {
            LOGGER.error("Failed to add project to qualitis, caused by: {}", e.getMessage(), e);
            throw new AppJointErrorException(500, e.getMessage());
        }
    }

    private void autoAddUser(RestTemplate restTemplate, String username) throws Exception {
        String path = AUTO_ADD_USER_PATH.replace("{username}", username);
        URI url = buildUrI(getHost(), getPort(), path, appId, appToken, RandomStringUtils.randomNumeric(5), String.valueOf(System.currentTimeMillis()));

        HttpHeaders headers = new HttpHeaders();
        HttpEntity entity = new HttpEntity(headers);

        LOGGER.info("Start to add user in qualitis. url: {}, method: {}", url, HttpMethod.POST);
        Map<String, Object> response = restTemplate.exchange(url, org.springframework.http.HttpMethod.POST, entity, Map.class).getBody();
        LOGGER.info("Succeed to add user in qualitis. response: {}", response);

        if (response == null) {
            String errorMessage = "Error! Can not add user, response is null";
            LOGGER.error(errorMessage);
            throw new AppJointErrorException(500, errorMessage);
        }

        if (!checkResponse(response)) {
            String message = (String) response.get("message");
            String errorMessage = String.format("Error! Can not add user, exception: %s", message);
            LOGGER.error(errorMessage);
            throw new AppJointErrorException(500, errorMessage);
        }
    }

    private Map<String, Object> createProjectReal(RestTemplate restTemplate, HttpEntity<Object> entity) throws Exception {
        URI url = buildUrI(getHost(), getPort(), CREATE_PROJECT_PATH, appId, appToken, RandomStringUtils.randomNumeric(5), String.valueOf(System.currentTimeMillis()));
        LOGGER.info("Start to add project to qualitis. url: {}, method: {}, body: {}", url, HttpMethod.PUT, entity);
        Map<String, Object> response = restTemplate.exchange(url, org.springframework.http.HttpMethod.PUT, entity, Map.class).getBody();
        LOGGER.info("Succeed to add project to qualitis. response: {}", response);

        if (response == null) {
            String errorMessage = "Error! Can not add project, response is null";
            LOGGER.error(errorMessage);
            throw new AppJointErrorException(500, errorMessage);
        }

        return response;
    }

    @Override
    public void deleteProject(Project project, Session session) throws AppJointErrorException {
        try {
            QualitisDeleteProjectRequest qualitisDeleteProjectRequest = new QualitisDeleteProjectRequest();

            DSSProject DSSProject = (DSSProject) project;
            qualitisDeleteProjectRequest.setProjectId(project.getId());
            qualitisDeleteProjectRequest.setUsername(DSSProject.getUserName());

            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<Object> entity = generateEntity(qualitisDeleteProjectRequest);

            URI url = buildUrI(getHost(), getPort(), DELETE_PROJECT_PATH, appId, appToken, RandomStringUtils.randomNumeric(5), String.valueOf(System.currentTimeMillis()));
            LOGGER.info("Start to delete project in qualitis. url: {}, method: {}, body: {}", url, javax.ws.rs.HttpMethod.POST, entity);
            Map<String, Object> response = restTemplate.postForObject(url, entity, Map.class);
            LOGGER.info("Succeed to delete project in qualitis. response: {}", response);

            if (response == null) {
                String errorMessage = "Error! Can not delete project in qualitis, response is null";
                LOGGER.error(errorMessage);
                throw new AppJointErrorException(500, errorMessage);
            }

            if (!checkResponse(response)) {
                String message = (String) response.get("message");
                String errorMessage = String.format("Error! Can not delete project in qualitis, exception: %s", message);
                LOGGER.error(errorMessage);
                throw new AppJointErrorException(500, errorMessage);
            }

            LOGGER.info("Succeed to delete project in qualitis, project_id: {}", project.getId());
        } catch (Exception e) {
            LOGGER.error("Failed to delete project in qualitis, caused by: {}", e.getMessage(), e);
            throw new AppJointErrorException(500, e.getMessage());
        }
    }

    public Project getProject(Project project) throws AppJointErrorException {
        return null;
    }

    @Override
    public void updateProject(Project project, Session session) throws AppJointErrorException {
        try {
            QualitisUpdateProjectRequest qualitisUpdateProjectRequest = new QualitisUpdateProjectRequest();

            DSSProject DSSProject = (DSSProject) project;
            qualitisUpdateProjectRequest.setProjectId(project.getId());
            qualitisUpdateProjectRequest.setProjectName(project.getName());
            qualitisUpdateProjectRequest.setDescription(project.getDescription());
            qualitisUpdateProjectRequest.setUsername(DSSProject.getUserName());

            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<Object> entity = generateEntity(qualitisUpdateProjectRequest);

            URI url = buildUrI(getHost(), getPort(), UPDATE_PROJECT_PATH, appId, appToken, RandomStringUtils.randomNumeric(5), String.valueOf(System.currentTimeMillis()));
            LOGGER.info("Start to update project in qualitis. url: {}, method: {}, body: {}", url, javax.ws.rs.HttpMethod.POST, entity);
            Map<String, Object> response = restTemplate.postForObject(url, entity, Map.class);
            LOGGER.info("Succeed to update project in qualitis. response: {}", response);

            if (response == null) {
                String errorMessage = "Error! Can not update project in qualitis, response is null";
                LOGGER.error(errorMessage);
                throw new AppJointErrorException(500, errorMessage);
            }

            if (!checkResponse(response)) {
                String message = (String) response.get("message");
                String errorMessage = String.format("Error! Can not update project in qualitis, exception: %s", message);
                LOGGER.error(errorMessage);
                throw new AppJointErrorException(500, errorMessage);
            }

            LOGGER.info("Succeed to update project in qualitis, project_id: {}", project.getId());
        } catch (Exception e) {
            LOGGER.error("Failed to update project in qualitis, caused by: {}", e.getMessage(), e);
            throw new AppJointErrorException(500, e.getMessage());
        }
    }

    private Boolean checkResponse(Map<String, Object> response) {
        String responseStatus = (String) response.get("code");
        return "200".equals(responseStatus);
    }

    private HttpEntity<Object> generateEntity(Object submitRequest) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Gson gson = new Gson();
        return new HttpEntity<Object>(gson.toJson(submitRequest), headers);
    }

    private URI buildUrI(String host, int port, String path, String appId, String appToken,
                         String nonce, String timestamp) throws Exception {
        String signature = getSignature(appId, appToken, nonce, timestamp);
        String urlStr = "http://" + host + ":" + port;
        URI uri = UriBuilder.fromUri(urlStr)
                .path(path)
                .queryParam("app_id", appId)
                .queryParam("nonce", nonce)
                .queryParam("timestamp", timestamp)
                .queryParam("signature", signature)
                .build();
        return uri;
    }

    private String getSignature(String appId, String appToken, String nonce, String timestamp)
            throws Exception {
        return Md5Utils.getMd5L32(Md5Utils.getMd5L32(appId + nonce + timestamp) + appToken);
    }

    private Integer getPort() {
        String baseUrl = getBaseUrl();
        return UriBuilder.fromUri(baseUrl).build().getPort();
    }

    private String getHost() {
        String baseUrl = getBaseUrl();
        return UriBuilder.fromUri(baseUrl).build().getHost();
    }
}