import com.webank.wedatasphere.dss.schedulis.exception.SchedulisSchedulerException;
import com.webank.wedatasphere.dss.schedulis.exception.SchedulisServerException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.junit.Test;
import scala.Tuple2;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


public class HttpTest {

    //@Test
    public Cookie test01() throws IOException {
        HttpPost httpPost = new HttpPost("http://127.0..0.1:8290/checkin");
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("username", "allenlliu"));
        params.add(new BasicNameValuePair("userpwd", "*****"));
        params.add(new BasicNameValuePair("action", "login"));
        httpPost.setEntity(new UrlEncodedFormEntity(params));
        CookieStore cookieStore = new BasicCookieStore();
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        HttpClientContext context = null;
        try {
            httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
            context = HttpClientContext.create();
            response = httpClient.execute(httpPost, context);
        } finally {
            IOUtils.closeQuietly(response);
            IOUtils.closeQuietly(httpClient);
        }
        List<Cookie> cookies = context.getCookieStore().getCookies();
        return cookies.get(0);
    }

    @Test
    public void test02() throws IOException {
        List<String> strings = new ArrayList<>();
        strings.add("1");
        strings.add("bbb");
        strings.add("ccc");
    }

    public void print(String string) throws IOException, IllegalAccessException{
        switch (string){
            case "1":throw new IOException("a");
            case "2":throw new IllegalAccessException("b");
            default:
                System.out.println(string);
        }
    }
    @Test
    public void  test03() throws IOException, SchedulisSchedulerException {
        Cookie cookie = test01();
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("ajax","fetchProjectPage"));
        params.add(new BasicNameValuePair("start","0"));
        params.add(new BasicNameValuePair("length","10"));
        params.add(new BasicNameValuePair("projectsType","personal"));
        params.add(new BasicNameValuePair("pageNum","1"));
        params.add(new BasicNameValuePair("order","orderProjectName"));
        CookieStore cookieStore = new BasicCookieStore();
        cookieStore.addCookie(cookie);
        HttpClientContext context = HttpClientContext.create();
        CloseableHttpResponse response = null;
        CloseableHttpClient httpClient = null;
        try {
            String finalUrl = "http://127.0.0.1:8088/index" + "?" + EntityUtils.toString(new UrlEncodedFormEntity(params));
            HttpGet httpGet = new HttpGet(finalUrl);
            httpGet.addHeader(HTTP.CONTENT_ENCODING, "UTF-8");
            httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
            response = httpClient.execute(httpGet, context);
            /*Header[] allHeaders = context.getRequest().getAllHeaders();
            Optional<Header> header = Arrays.stream(allHeaders).filter(f -> "Cookie".equals(f.getAppJointName())).findFirst();
            header.ifPresent(AzkabanUtils.handlingConsumerWrapper(this::parseCookie));*/
        } catch (Exception e) {
            throw new SchedulisSchedulerException(90004, e.getMessage());
        } finally {
            IOUtils.closeQuietly(response);
            IOUtils.closeQuietly(httpClient);
        }
    }

    @Test
    public void test04(){
/*        A a = new A();
        Tuple2<String, String> hello = a.hello();
        Tuple2<String, String> stringStringTuple2 = new Tuple2<>("","");*/
    }

    @Test
    public void test05(){
        String subFlowPath = "/Users/v_wbjftang/linuxDownloads/project_0926_tjf/flow2/subFlows/subFlow21/subFlows/subFlow211";
        int indexOf = subFlowPath.indexOf("subFlows");
        if(indexOf != -1){
            subFlowPath = subFlowPath.substring(0, indexOf-1);
        }
        String substring1 = subFlowPath.substring(0, subFlowPath.lastIndexOf("/"));
        System.out.println(substring1);
        String allPath = "/Users/v_wbjftang/linuxDownloads/project_0926_tjf/flow2/subFlows/subFlow21/subFlows/subFlow211";
        String substring = allPath.substring(substring1.length() +1 );
        System.out.println("\\" + File.separator);
        //String s = substring.replaceAll("\\\\subFlows\\\\", "....");
        String s = substring.replaceAll("\\" + File.separator +"subFlows" + "\\" + File.separator, "....");
        System.out.println(s);
    }
    @Test
    public void test06(){
        String nodeType = "spark.sql";
        String[] split = nodeType.split("\\.");
        for (int i = 1; i < split.length; i++) {
            String s = split[i];
            String replace = s.replace(s.charAt(0), (char) (s.charAt(0) - 32));
            split[i] = replace;
        }
        System.out.println(this.getClass().getPackage().getName());
        for (int i = 0; i < split.length; i++) {
            System.out.println(i);
        }
    }

    @Test
    public void test07() throws IOException {
        Cookie cookie = test01();
        String projectName = "create_project0930";
        HttpPost httpPost = new HttpPost("http://127.0..0.1:8290/manager" + "?project=" + projectName);
        httpPost.addHeader(HTTP.CONTENT_ENCODING, "UTF-8");
        CloseableHttpResponse response = null;
        File file = new File("E:\\appcom\\tmp\\dws\\wtss\\neiljianliu\\2019-09-30\\project_0926_tjf.zip");
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
                throw new SchedulisServerException(90005, "release project failed, " + entStr);
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        finally {
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(response);
            IOUtils.closeQuietly(httpClient);
        }
    }
    @Test
    public void test08(){

    }
}
