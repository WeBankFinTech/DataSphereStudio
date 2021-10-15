package com.webank.wedatasphere.dss.appconn.dolphinscheduler.entity;

import com.webank.wedatasphere.dss.appconn.schedule.core.entity.SchedulerFlow;
import org.apache.commons.collections.CollectionUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * The type Dolphin scheduler flow.
 *
 * @author yuxin.yuan
 * @date 2021/04/29
 */
public class DolphinSchedulerFlow extends SchedulerFlow {
    private ProcessDefinitionJson processDefinitionJson;

    private Map<String, LocationInfo> locations;

    private List<Connect> connects;

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

    public static class ProcessDefinitionJson {
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
    }

    public static class LocationInfo {
        private String name;

        private String targetarr;

        // 后继节点数
        //        private String nodenumber;

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
