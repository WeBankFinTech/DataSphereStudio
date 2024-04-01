package com.webank.wedatasphere.dss.git.thread;

import com.webank.wedatasphere.dss.git.config.GitServerConfig;
import org.apache.linkis.common.utils.Utils;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.*;

public class GitServerThreadPool {
    private final Map<String, ExecutorService> taskExecutors = new HashMap<>();
    private final int POOL_SIZE;

    public GitServerThreadPool(int num) {
        this.POOL_SIZE = num;
        // 初始化每个任务的ExecutorService
        for (int i = 0; i < POOL_SIZE; i++) {
            taskExecutors.put("TaskExecutor_" + i, Executors.newSingleThreadExecutor());
        }
    }

    public <T> Future<T> submitTask(String taskName, Callable<T> task) {
        // 根据任务名称选择对应的ExecutorService，这里简化为根据hashCode选取
        int index = Math.abs(taskName.hashCode()) % POOL_SIZE;
        ExecutorService executorService = taskExecutors.get("TaskExecutor_" + index);
        return executorService.submit(task);
    }

    public void shutdown() {
        // 关闭所有ExecutorService
        taskExecutors.values().forEach(ExecutorService::shutdown);
    }

}
