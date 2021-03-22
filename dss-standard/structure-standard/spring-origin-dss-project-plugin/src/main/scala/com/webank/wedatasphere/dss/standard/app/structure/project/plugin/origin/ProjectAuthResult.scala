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

package com.webank.wedatasphere.dss.standard.app.structure.project.plugin.origin

import scala.beans.BeanProperty
import java.util

import com.webank.wedatasphere.linkis.httpclient.dws.annotation.DWSHttpMessageResult
import com.webank.wedatasphere.linkis.httpclient.dws.response.DWSResult

/**
  * Created by enjoyyin on 2020/8/11.
  */
@DWSHttpMessageResult("/api/rest_j/v\\d+/dss/getProjectAuthOfWorkspace")
class ProjectAuthResult extends DWSResult {

  @BeanProperty
  var projectName: String = _

  @BeanProperty
  var projectId: String = _

  @BeanProperty
  var workspaceName: String = _

  @BeanProperty
  var editUsers: util.List[String] = _

  @BeanProperty
  var accessUsers: util.List[String] = _

  @BeanProperty
  var deleteUsers: util.List[String] = _

}
