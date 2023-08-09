/*
 * Copyright 2019 WeBank
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.webank.wedatasphere.dss.orchestrator.core;

import com.webank.wedatasphere.dss.appconn.core.AppConn;
import com.webank.wedatasphere.dss.appconn.scheduler.SchedulerAppConn;

import java.util.List;


/**
 * 一个编排抽象，代表了一类编排。包括了编排的类型、关联的appconn、支持的工具条类型等信息。
 * 注意，编排并不是工作流，因此不会包含任何的节点内容
 */
public interface DSSOrchestrator {

   /**
    * 返回Orchestrator的名称，如workflow
    * @return
    */
   String getName();


   /**
    * 返回编排关联的AppConn
    * @return
    */
   AppConn getAppConn();

    /**
     * 返回编排关联的 SchedulerAppConn。
     * DSSOrchestrator 允许每个编排绑定一个特有的 SchedulerAppConn。当 orchestrator-framework 在管理一个
     * Orchestrator 时（即增删改查一个 DSSOrchestrator），会同步向 SchedulerAppConn 请求，希望能够在关联的
     * 调度系统也进行这样的操作。
     * <br>
     * 一般情况下，整个 DSS 会至少加载一个 SchedulerAppConn，如果 DSSOrchestrator 没有显示的绑定一个 SchedulerAppConn，
     * 则 DSS 系统会默认为该 DSSOrchestrator 随机绑定一个。
     * @return 返回绑定的 SchedulerAppConn
     */
   SchedulerAppConn getSchedulerAppConn();

   DSSOrchestratorContext getDSSOrchestratorContext();

   /**
    * 返回所有已经关联到的AppConn
    * @return
    */
   List<AppConn> getLinkedAppConn();

   /**
    * 用于工具条功能按钮展示，可以查到该模式可以提供的功能按钮
    * @return 按钮列表
    */
   List<String> getToolBars();
}
