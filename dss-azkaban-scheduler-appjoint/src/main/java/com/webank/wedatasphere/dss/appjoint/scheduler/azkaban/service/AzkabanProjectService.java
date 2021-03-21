package com.webank.wedatasphere.dss.appjoint.scheduler.azkaban.service;


import com.webank.wedatasphere.dss.appjoint.exception.AppJointErrorException;
import com.webank.wedatasphere.dss.appjoint.service.AppJointUrlImpl;
import com.webank.wedatasphere.dss.appjoint.scheduler.azkaban.entity.AzkabanSchedulerProject;
import com.webank.wedatasphere.dss.appjoint.scheduler.service.SchedulerProjectService;
import com.webank.wedatasphere.dss.appjoint.service.session.Session;
import com.webank.wedatasphere.dss.common.entity.project.Project;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.appjoint.scheduler.azkaban.util.AzkabanUtils;
import com.webank.wedatasphere.dss.common.utils.DSSExceptionUtils;
import com.webank.wedatasphere.dss.common.utils.ZipHelper;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Created by cooperyang on 2019/9/16.
 */
public final class AzkabanProjectService extends AppJointUrlImpl implements SchedulerProjectService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private String projectUrl;

    @Override
    public void setBaseUrl(String baseUrl) {
        this.projectUrl = baseUrl + "/manager";
    }

    @Override
    public List<Project> fetchProjects(Session session) {
        return null;
    }

    /**
     * parameters:
     * name = value
     * description=value
     *
     * @param project
     * @param session
     * @throws AppJointErrorException
     */
    @Override
    public Project createProject(Project project, Session session) throws AppJointErrorException {
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("action", "create"));
        params.add(new BasicNameValuePair("name", project.getName()));
        params.add(new BasicNameValuePair("description", project.getDescription()));
        HttpPost httpPost = new HttpPost(projectUrl);
        httpPost.addHeader(HTTP.CONTENT_ENCODING, HTTP.IDENTITY_CODING);
        CookieStore cookieStore = new BasicCookieStore();
        cookieStore.addCookie(session.getCookies()[0]);
        HttpEntity entity = EntityBuilder.create()
                .setContentType(ContentType.create("application/x-www-form-urlencoded", Consts.UTF_8))
                .setParameters(params).build();
        httpPost.setEntity(entity);
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        try {
            httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
            response = httpClient.execute(httpPost);
            HttpEntity ent = response.getEntity();
            String entStr = IOUtils.toString(ent.getContent(), "utf-8");
            logger.error("新建工程 {}, azkaban 返回的信息是 {}", project.getName(), entStr);
            String message = AzkabanUtils.handleAzkabanEntity(entStr);
            if (!"success".equals(message)) {
                throw new AppJointErrorException(90008, "新建工程失败, 原因:" + message);
            }
        } catch (Exception e) {
            logger.error("创建工程失败:", e);
            throw new AppJointErrorException(90009, e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(response);
            IOUtils.closeQuietly(httpClient);
        }
        return null;
    }

    /**
     * delete=boolean
     * project=projectName
     *
     * @param project
     * @param session
     */
    @Override
    public void deleteProject(Project project, Session session) throws AppJointErrorException {
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("delete", "true"));
        params.add(new BasicNameValuePair("project", project.getName()));
        CookieStore cookieStore = new BasicCookieStore();
        cookieStore.addCookie(session.getCookies()[0]);
        HttpClientContext context = HttpClientContext.create();
        CloseableHttpResponse response = null;
        CloseableHttpClient httpClient = null;
        try {
            String finalUrl = projectUrl + "?" + EntityUtils.toString(new UrlEncodedFormEntity(params));
            HttpGet httpGet = new HttpGet(finalUrl);
            httpGet.addHeader(HTTP.CONTENT_ENCODING, "UTF-8");
            httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
            response = httpClient.execute(httpGet, context);
            Header[] allHeaders = context.getRequest().getAllHeaders();
            Optional<Header> header = Arrays.stream(allHeaders).filter(f -> "Cookie".equals(f.getName())).findFirst();
            header.ifPresent(DSSExceptionUtils.handling(this::parseCookie));
        } catch (Exception e) {
            logger.error("delete scheduler project failed,reason:",e);
            throw new AppJointErrorException(90010, e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(response);
            IOUtils.closeQuietly(httpClient);
        }
    }

    private void parseCookie(Header header) {
        String[] arr = header.getValue().split(";");
        Arrays.stream(arr).filter(f -> f.toLowerCase().trim().contains("azkaban.failure.message="))
                .forEach(DSSExceptionUtils.handling(this::parseHeader));
    }

    private void parseHeader(String headerValue) throws UnsupportedEncodingException, AppJointErrorException {
        String errorMsg = URLDecoder.decode(headerValue.split("=")[1], "UTF-8");
        if (StringUtils.isNotEmpty(errorMsg)) {
            throw new AppJointErrorException(90011, errorMsg);
        }
    }

    @Override
    public void publishProject(Project project, Session session) throws AppJointErrorException {
        String tmpSavePath;
        try {
            tmpSavePath = zipProject(project);
            //upload zip to Azkaban
            uploadProject(tmpSavePath,project,session.getCookies()[0]);
        } catch (Exception e) {
            logger.error("upload sheduler failed:reason",e);
            throw new AppJointErrorException(90012, e.getMessage(), e);
        }
    }

    private void uploadProject(String tmpSavePath, Project project, Cookie cookie) throws AppJointErrorException {
        String projectName = project.getName();
        HttpPost httpPost = new HttpPost(projectUrl + "?project=" + projectName);
        httpPost.addHeader(HTTP.CONTENT_ENCODING, "UTF-8");
        CloseableHttpResponse response = null;
        File file = new File(tmpSavePath);
        CookieStore cookieStore = new BasicCookieStore();
        cookieStore.addCookie(cookie);
        CloseableHttpClient httpClient = null;
        InputStream inputStream = null;
        try {
            httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
            MultipartEntityBuilder entityBuilder =  MultipartEntityBuilder.create();
            entityBuilder.addBinaryBody("file",file);
            entityBuilder.addTextBody("ajax", "upload");
            entityBuilder.addTextBody("project", projectName);
            httpPost.setEntity(entityBuilder.build());
            response = httpClient.execute(httpPost);
            HttpEntity httpEntity = response.getEntity();
            inputStream = httpEntity.getContent();
            String entStr = null;
            entStr = IOUtils.toString(inputStream, "utf-8");
            if(response.getStatusLine().getStatusCode() != 200){
                logger.error("调用azkaban上传接口的返回不为200, status code 是 {}", response.getStatusLine().getStatusCode());
                throw new AppJointErrorException(90013, "release project failed, " + entStr);
            }
            logger.info("upload project:{} success!",projectName);
        }catch (Exception e){
            logger.error("upload failed,reason:",e);
            throw new AppJointErrorException(90014,e.getMessage(), e);
        }
        finally {
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(response);
            IOUtils.closeQuietly(httpClient);
        }
    }

    @Override
    public void updateProject(Project project, Session session) throws AppJointErrorException {

    }

    public String zipProject(Project project) throws DSSErrorException {
        AzkabanSchedulerProject publishProject = (AzkabanSchedulerProject) project;
        String projectPath = publishProject.getStorePath();
        String zipPath = ZipHelper.zip(projectPath);
        return zipPath;
    }

}
