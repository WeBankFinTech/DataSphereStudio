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

package com.webank.wedatasphere.dss.flow.execution.entrance.conf

import com.webank.wedatasphere.dss.flow.execution.entrance.engine.{FlowEntranceEngine, FlowExecutionExecutorManagerImpl}
import com.webank.wedatasphere.dss.flow.execution.entrance.entranceparser.FlowExecutionParser
import com.webank.wedatasphere.dss.flow.execution.entrance.persistence.WorkflowPersistenceEngine
import com.webank.wedatasphere.linkis.entrance.EntranceParser
import com.webank.wedatasphere.linkis.entrance.annotation.{EntranceExecutorManagerBeanAnnotation, EntranceParserBeanAnnotation, PersistenceEngineBeanAnnotation}
import com.webank.wedatasphere.linkis.entrance.persistence.PersistenceEngine
import com.webank.wedatasphere.linkis.scheduler.executer.ExecutorManager
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration

/**
 * created by chaogefeng on 2019/11/4 16:37
 * Description:
 */
@Configuration
class FlowExecutionEntranceSpringConfiguration {
  private val logger = LoggerFactory.getLogger(classOf[FlowExecutionEntranceSpringConfiguration])

  @EntranceExecutorManagerBeanAnnotation
  def generateExternalEntranceExecutorManager(@Autowired flowEntranceEngine:FlowEntranceEngine): ExecutorManager = {
    logger.info("begin to get FlowExecution Entrance EntranceExecutorManager")
    new FlowExecutionExecutorManagerImpl(flowEntranceEngine)
  }

  @PersistenceEngineBeanAnnotation
  def generatePersistenceEngine(): PersistenceEngine ={
    new WorkflowPersistenceEngine()
  }

  @EntranceParserBeanAnnotation
  def generateEntranceParser(): EntranceParser = {
    logger.info("begin to get FlowExecution Entrance parser")
    new FlowExecutionParser()
  }


}
