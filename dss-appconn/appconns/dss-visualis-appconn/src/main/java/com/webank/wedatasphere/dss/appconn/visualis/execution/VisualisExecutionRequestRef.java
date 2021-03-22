//package com.webank.wedatasphere.dss.appconn.visualis.execution;
//
//import com.google.common.collect.Maps;
//import com.webank.wedatasphere.dss.appconn.visualis.ref.entity.DashboardCreateResponseRef;
//import com.webank.wedatasphere.dss.appconn.visualis.ref.entity.DisplayCreateResponseRef;
//import com.webank.wedatasphere.dss.appconn.visualis.ref.entity.WidgetCreateResponseRef;
//import com.webank.wedatasphere.dss.appconn.visualis.utils.NumberUtils;
//import com.webank.wedatasphere.dss.standard.app.development.execution.ExecutionRequestRef;
//import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
//
//import java.util.Map;
//
//public class VisualisExecutionRequestRef implements ExecutionRequestRef {
//
//    Map<String, Object> parameters = Maps.newHashMap();
//    private Workspace workspace;
//
//    @Override
//    public void init(String executionContent, Map<String, Object> parameters) throws Exception {
//        this.parameters = parameters;
//        this.parameters.put("createBy", parameters.get("wds.linkis.schedulis.submit.user"));
//        String nodeType = parameters.get("nodeType").toString();
//        if("visualis.display".equalsIgnoreCase(nodeType)){
//            DisplayCreateResponseRef displayCreateResponseRef = new DisplayCreateResponseRef(executionContent);
//            this.parameters.put("id", NumberUtils.parseDoubleString(displayCreateResponseRef.getDisplayId()));
//        } else if("visualis.dashboard".equalsIgnoreCase(nodeType)){
//            DashboardCreateResponseRef dashboardCreateResponseRef = new DashboardCreateResponseRef(executionContent);
//            this.parameters.put("id", NumberUtils.parseDoubleString(dashboardCreateResponseRef.getDashboardPortalId()));
//        } else if ("visualis.widget".equalsIgnoreCase(nodeType)){
//            WidgetCreateResponseRef widgetCreateResponseRef = new WidgetCreateResponseRef(executionContent);
//            this.parameters.put("id", NumberUtils.parseDoubleString(widgetCreateResponseRef.getWidgetId()));
//        }
//    }
//
//    @Override
//    public Object getParameter(String key) {
//        return parameters.get(key);
//    }
//
//    @Override
//    public void setParameter(String key, Object value) {
//        parameters.put(key, value);
//    }
//
//    @Override
//    public Map<String, Object> getParameters() {
//        return parameters;
//    }
//
//    @Override
//    public String getName() {
//        return parameters.get("name").toString();
//    }
//
//    @Override
//    public String getType() {
//        return parameters.get("nodeType").toString();
//    }
//
//    @Override
//    public Workspace getWorkspace() {
//        return workspace;
//    }
//
//    @Override
//    public void setWorkspace(Workspace workspace) {
//        this.workspace = workspace;
//    }
//}
