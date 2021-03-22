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

package com.webank.wedatasphere.dss.framework.release.service;

import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.linkis.common.exception.ErrorException;
import scala.Tuple2;

import java.util.List;

/**
 * created by cooperyang on 2020/11/17
 * Description: release是一个复杂的过程，应该是要做成异步的操作,采用发布线程池来进行控制。
 */
public interface ReleaseService {


    Long releaseOrchestrator(String releaseUser, Long orchestratorVersionId, Long orchestratorId, String dssLabel, Workspace workspace) throws ErrorException;


    String releaseOrchestratorBatch(String releaseUser, List<Long> orchestratorVersionIds, String dssLabel) throws ErrorException;


    Tuple2<String, String> getStatus(Long jobId);






}
