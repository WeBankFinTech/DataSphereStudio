//package com.webank.wedatasphere.dss.framework.admin.test;
//
//import com.google.gson.*;
//import com.webank.wedatasphere.dss.framework.admin.pojo.entity.DssExchangisProject;
//import okhttp3.Call;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.Response;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * @Auther: Han Tang
// * @Date: 2022/1/17-01-17-19:23
// */
//public class Test {
//
//    public static void main(String[] args) throws IOException {
//
//        OkHttpClient httpClient = new OkHttpClient();
////        http://172.24.2.235:9503/api/v1/jobinfo/pageList?projectId=86&page=1&pageSize=10&fuzzyName=&jobId=
////        http://172.24.2.235:9503/api/v1/project/tree
////        http://172.24.2.235:9503/api/v1/jobinfo/runTask/75?userName=admin
//
//        String url = "http://172.24.2.235:9503/api/v1/project/tree";
//        Request getRequest = new Request.Builder()
//                .url(url)
//                .addHeader("Content-Type", "application/json")
//                .addHeader("Cookie", "UM-SSO-BDP=eyJYLUFVVEgtSUQiOiJhZG1pbiIsInRrLXRpbWUiOiIyMjIxMTIyMzE3MDg1MiIsImFsZyI6Ik1ENSJ9%2CeyJwYXNzd29yZCI6Ikx4bCMjMjMwOTAxMSIsImlkIjoxLCJyb2xlIjoidW5Mb2dpbiIsInVzZXJOYW1lIjoiYWRtaW4iLCJYLUFVVEgtSUQiOiJhZG1pbiJ9%2Cc18bf47cd6d83718a7c52352658484b1")
//                .get()
//                .build();
//
//        Call call = httpClient.newCall(getRequest);
//
//
//        Response response = call.execute();
////        Gson gson = new Gson().
//
//        JsonObject returnData = new JsonParser().parse(response.body().string()).getAsJsonObject();
//
//        JsonArray jsonArray = returnData.getAsJsonArray("data");
//
//        List<DssExchangisProject> projectList = new ArrayList<>();
//        List<DssExchangisProject>  dssExchangisProjects = getExchangeProjectList(projectList,jsonArray);
//
//        for(DssExchangisProject dssExchangisProject:dssExchangisProjects){
//            System.out.println(dssExchangisProject.getProjectName());
//        }
//
////        if (jsonArray.size() > 0) {
////            for (JsonElement jsonElement : jsonArray) {
////               System.out.println(jsonElement.getAsJsonObject().get("projectName").getAsString());
////            }
////        }
//
//
////        System.out.println(returnData.toString() + "111");
//
//    }
//
//
//    public static List<DssExchangisProject> getExchangeProjectList(List<DssExchangisProject> projectList, JsonArray jsonArray){
//        for (JsonElement jsonElement : jsonArray) {
//            DssExchangisProject dssExchangisProject = new DssExchangisProject();
//            dssExchangisProject.setProjectName( jsonElement.getAsJsonObject().get("projectName").getAsString());
//            dssExchangisProject.setId(jsonElement.getAsJsonObject().get("id").getAsInt());
//            projectList.add(dssExchangisProject);
//
//           JsonArray childJsonArray =  jsonElement.getAsJsonObject().getAsJsonArray("children");
//           if(childJsonArray != null && childJsonArray.size() > 0){
//               getExchangeProjectList(projectList,childJsonArray);
//           }
//
//        }
//        return projectList;
//    }
//}
