package com.webank.wedatasphere.dss.appconn.dolphinscheduler.entity;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;

import com.webank.wedatasphere.dss.workflow.core.entity.WorkflowWithContextImpl;

public class DolphinSchedulerWorkflow extends WorkflowWithContextImpl {

    private ProcessDefinitionJson processDefinitionJson;

    private Map<String, LocationInfo> locations;

    private List<Connect> connects;

    private String releaseState;

    public ProcessDefinitionJson getProcessDefinitionJson() {
        return processDefinitionJson;
    }

    public void setProcessDefinitionJson(ProcessDefinitionJson processDefinitionJson) {
        this.processDefinitionJson = processDefinitionJson;
    }

    public Map<String, LocationInfo> getLocations() {
        return locations;
    }

    public void setLocations(Map<String, LocationInfo> locations) {
        this.locations = locations;
    }

    public List<Connect> getConnects() {
        return connects;
    }

    public void setConnects(List<Connect> connects) {
        this.connects = connects;
    }

    public String getReleaseState() {
        return releaseState;
    }

    public void setReleaseState(String releaseState) {
        this.releaseState = releaseState;
    }

    public static class ProcessDefinitionJson {
        private List<Map<String, Object>> globalParams = new ArrayList<>(0);
        private List<DolphinSchedulerTask> tasks;

        public List<DolphinSchedulerTask> getTasks() {
            return tasks;
        }

        public void setTasks(List<DolphinSchedulerTask> tasks) {
            this.tasks = tasks;
        }

        public void addTask(DolphinSchedulerTask task) {
            if (CollectionUtils.isEmpty(tasks)) {
                tasks = new LinkedList<>();
            }
            tasks.add(task);
        }
        public List<Map<String, Object>> getGlobalParams() {
            return globalParams;
        }

        public void setGlobalParams(List<Map<String, Object>> globalParams) {
            this.globalParams = globalParams;
        }
    }

    public static class LocationInfo {
        private String name;

        private String targetarr;

        // 后继节点数
        // private String nodenumber;

        private int x;

        private int y;

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        public void setTargetarr(String targetarr) {
            this.targetarr = targetarr;
        }

        public String getTargetarr() {
            return this.targetarr;
        }

        //        public void setNodenumber(String nodenumber) {
        //            this.nodenumber = nodenumber;
        //        }
        //
        //        public String getNodenumber() {
        //            return this.nodenumber;
        //        }

        public void setX(int x) {
            this.x = x;
        }

        public int getX() {
            return this.x;
        }

        public void setY(int y) {
            this.y = y;
        }

        public int getY() {
            return this.y;
        }
    }

    public static class Connect {
        private String endPointSourceId;

        private String endPointTargetId;

        public Connect(String endPointSourceId, String endPointTargetId) {
            this.endPointSourceId = endPointSourceId;
            this.endPointTargetId = endPointTargetId;
        }

        public void setEndPointSourceId(String endPointSourceId) {
            this.endPointSourceId = endPointSourceId;
        }

        public String getEndPointSourceId() {
            return this.endPointSourceId;
        }

        public void setEndPointTargetId(String endPointTargetId) {
            this.endPointTargetId = endPointTargetId;
        }

        public String getEndPointTargetId() {
            return this.endPointTargetId;
        }

    }
}
