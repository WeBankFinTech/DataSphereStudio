/*
 *
 *  * Copyright 2019 WeBank
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.webank.wedatasphere.dss.orchestrator.core;

import com.webank.wedatasphere.dss.appconn.core.AppConn;
import com.webank.wedatasphere.dss.common.entity.DSSLabel;
import java.util.List;


/**
 * @author allenlliu
 * @date 2020/09/23 02:42 PM
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


  void setAppConn(AppConn appConn);

   /**
    *添加当前编排需要使用到在appconn
    * @param appconn
    */
   void addLinkedAppConn(AppConn appconn);


   /**
    * 为编排提供标签说明，如DEV
    * @param dssLabel
    */
   void addLinkedDssLabels(DSSLabel dssLabel);

   /**
    * 返回所有已经关联到的AppConn
    * @return
    */
   List<AppConn> getLinkedAppConn();

   /**
    * 用于工具条功能按钮展示，可以查到该模式可以提供的功能按钮
    * @return
    */
   List<String> getToolBars();
}
