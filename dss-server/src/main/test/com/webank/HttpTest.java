//package com.webank;
//
//import com.webank.wedatasphere.dss.schedulis.exception.SchedulisSchedulerException;
//import com.webank.wedatasphere.dss.schedulis.linkisjob.AbstractLinkisAzkabanJob;
//import com.webank.wedatasphere.dss.schedulis.linkisjob.LinkisAzkabanJobFactory;
//import org.apache.commons.io.IOUtils;
//import org.apache.http.NameValuePair;
//import org.apache.http.client.CookieStore;
//import org.apache.http.client.entity.UrlEncodedFormEntity;
//import org.apache.http.client.methods.CloseableHttpResponse;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.client.protocol.HttpClientContext;
//import org.apache.http.cookie.Cookie;
//import org.apache.http.impl.client.BasicCookieStore;
//import org.apache.http.impl.client.CloseableHttpClient;
//import org.apache.http.impl.client.HttpClients;
//import org.apache.http.message.BasicNameValuePair;
//import org.apache.http.protocol.HTTP;
//import org.apache.http.util.EntityUtils;
//import org.junit.Test;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
//
//public class HttpTest {
//
//    //@Test
//    public Cookie test01() throws IOException {
//        HttpPost httpPost = new HttpPost("http://127.0.0.1:8088/checkin");
//        List<NameValuePair> params = new ArrayList<>();
//        params.add(new BasicNameValuePair("username", "neiljianliu"));
//        params.add(new BasicNameValuePair("userpwd", "*****"));
//        params.add(new BasicNameValuePair("action", "login"));
//        httpPost.setEntity(new UrlEncodedFormEntity(params));
//        CookieStore cookieStore = new BasicCookieStore();
//        CloseableHttpClient httpClient = null;
//        CloseableHttpResponse response = null;
//        HttpClientContext context = null;
//        try {
//            httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
//            context = HttpClientContext.create();
//            response = httpClient.execute(httpPost, context);
//        } finally {
//            IOUtils.closeQuietly(response);
//            IOUtils.closeQuietly(httpClient);
//        }
//        List<Cookie> cookies = context.getCookieStore().getCookies();
//        return cookies.get(0);
//    }
//
//    @Test
//    public void test02() throws IOException {
//        List<String> strings = new ArrayList<>();
//        strings.add("1");
//        strings.add("bbb");
//        strings.add("ccc");
//    }
//
//    public void print(String string) throws IOException, IllegalAccessException{
//        switch (string){
//            case "1":throw new IOException("a");
//            case "2":throw new IllegalAccessException("b");
//            default:
//                System.out.println(string);
//        }
//    }
//    @Test
//    public void  test03() throws IOException, SchedulisSchedulerException {
//        Cookie cookie = test01();
//        List<NameValuePair> params = new ArrayList<>();
//        params.add(new BasicNameValuePair("ajax","fetchProjectPage"));
//        params.add(new BasicNameValuePair("start","0"));
//        params.add(new BasicNameValuePair("length","10"));
//        params.add(new BasicNameValuePair("projectsType","personal"));
//        params.add(new BasicNameValuePair("pageNum","1"));
//        params.add(new BasicNameValuePair("order","orderProjectName"));
//        CookieStore cookieStore = new BasicCookieStore();
//        cookieStore.addCookie(cookie);
//        HttpClientContext context = HttpClientContext.create();
//        CloseableHttpResponse response = null;
//        CloseableHttpClient httpClient = null;
//        try {
//            String finalUrl = "http://127.0.0.1:8088/index" + "?" + EntityUtils.toString(new UrlEncodedFormEntity(params));
//            HttpGet httpGet = new HttpGet(finalUrl);
//            httpGet.addHeader(HTTP.CONTENT_ENCODING, "UTF-8");
//            httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
//            response = httpClient.execute(httpGet, context);
//            /*Header[] allHeaders = context.getRequest().getAllHeaders();
//            Optional<Header> header = Arrays.stream(allHeaders).filter(f -> "Cookie".equals(f.getAppJointName())).findFirst();
//            header.ifPresent(AzkabanUtils.handlingConsumerWrapper(this::parseCookie));*/
//        } catch (Exception e) {
//            throw new SchedulisSchedulerException(90002, e.getMessage());
//        } finally {
//            IOUtils.closeQuietly(response);
//            IOUtils.closeQuietly(httpClient);
//        }
//    }
//
//    @Test
//    public void test04(){
///*        A a = new A();
//        Tuple2<String, String> hello = a.hello();
//        Tuple2<String, String> stringStringTuple2 = new Tuple2<>("","");*/
//    }
//
//    @Test
//    public void test05(){
//        //String subFlowPath = "C:\\Users\\v_wbjftang\\linuxDownloads\\project_0926_tjf\\flow2\\subFlows\\subFlow21\\subFlows\\subFlow211";
//        String subFlowPath = "/Users/v_wbjftang/linuxDownloads/project_0926_tjf/flow2/subFlows/subFlow21/subFlows/subFlow211";
//        int indexOf = subFlowPath.indexOf("subFlows");
//        if(indexOf != -1){
//            subFlowPath = subFlowPath.substring(0, indexOf-1);
//        }
//        String substring1 = subFlowPath.substring(0, subFlowPath.lastIndexOf("/"));
//        System.out.println(substring1);
//        String allPath = "/Users/v_wbjftang/linuxDownloads/project_0926_tjf/flow2/subFlows/subFlow21/subFlows/subFlow211";
//        String substring = allPath.substring(substring1.length() +1 );
//        System.out.println("\\" + File.separator);
//        //String s = substring.replaceAll("\\\\subFlows\\\\", "....");
//        String s = substring.replaceAll("\\" + File.separator +"subFlows" + "\\" + File.separator, "....");
//        System.out.println(s);
//    }
//    @Test
//    public void test06(){
///*        String nodeType = "spark.sql";
//        try {
//            AbstractLinkisAzkabanJob job = LinkisAzkabanJobFactory.createJob(nodeType);
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (InstantiationException e) {
//            e.printStackTrace();
//        }
//        System.out.println("sdf");*/
//    }
//}
