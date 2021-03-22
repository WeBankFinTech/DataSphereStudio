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

package com.webank.wedatasphere.dss.contextservice.service;

import com.webank.wedatasphere.dss.common.exception.DSSErrorException;

/**
 * Linkis ContextService访问接口工具类
 * 提供创建ContextID、存储状态信息等到CS服务的接口
 * @Author alexyang
 * @Date 2020-0318
 */
public interface ContextService {

    /**
     * 创建ContextID，返回ContextID序列化后的字符串
     * @param flow
     * @param user
     * @param version
     * @return
     */
    String createContextID(String workspace, String projectName, String flow, String version, String user);

    /**
     * 检查和创建ContextID，返回创建ID后的jsonFlow
     * 检查ContextID信息：如果jsonFlow不包含ContextID信息，则创建新的；
     * 如果已经有ContextID，判断传入flowVersion是否相同，不同则创建新的ContextID，并返回新创建的ContextID序列化内容；相同则不创建新ID;
     * 新创建的ContextID会更新到flowJson里
     * @param jsonFlow
     * @param flowVersion
     * @param workspace
     * @param project
     * @param flow
     * @param user
     * @param fullCheck - true 检查所有参数； false 只检查jsonFlow中ContextID是否存在，存在则不再比较ContextID中version参数等是否相同，且不创建
     * @return
     */
    String checkAndCreateContextID(String jsonFlow, String flowVersion, String workspace, String project, String flow, String user, boolean fullCheck);

    /**
     * 解析jsonFlow、DWSProject内容，存储到CS里
     * 解析DWSProject、jsonFlow里面的资源、变量、节点依赖等信息，存储到CS服务
     * @param jsonFlow
     * @param parentFlowID
     */
    void checkAndSaveContext(String jsonFlow, String parentFlowID) throws DSSErrorException;

    /**
     * 检查更新ContextID信息，解析传入的jsonFlow、DWSProject内容，存储到CS里
     * @param jsonFlow 必需
     * @param parentFlowId 必需
     * @param workspace
     * @param flowVersion
     * @param user
     * @throws DSSErrorException
     */
    String checkAndInitContext(String jsonFlow, String parentFlowId, String workspace, String  projectName, String flowName, String flowVersion, String user) throws DSSErrorException;

    /**
     * 解析SchedulerFlow信息，并更新ContextID和存储Context信息，调用checkAndInitContext信息
     * @param schedulerFlow
     * @return
     * @throws DSSErrorException
     */
//    String checkAndInitContext(SchedulerFlow schedulerFlow) throws DSSErrorException;
}
