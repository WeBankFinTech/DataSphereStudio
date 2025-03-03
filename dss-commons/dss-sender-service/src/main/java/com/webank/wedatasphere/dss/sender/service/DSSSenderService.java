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

package com.webank.wedatasphere.dss.sender.service;

import com.webank.wedatasphere.dss.common.label.DSSLabel;
import org.apache.linkis.rpc.Sender;
import java.util.List;

public interface DSSSenderService {

    Sender getOrcSender();

    Sender getOrcSender(List<DSSLabel> dssLabels);

    Sender getScheduleOrcSender();

    Sender getWorkflowSender(List<DSSLabel> dssLabels);

    Sender getWorkflowSender();

    Sender getSchedulerWorkflowSender();

    Sender getProjectServerSender();

    Sender getGitSender();

}

