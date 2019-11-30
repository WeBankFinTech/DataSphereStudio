/*
 * Copyright 2019 WeBank
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
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

package com.webank.wedatasphere.dss.linkis.appjoint.entrance.exception

import com.webank.wedatasphere.linkis.common.exception.ErrorException

/**
  * created by cooperyang on 2019/10/8
  * Description:
  */
case class AppJointEntranceIllegalParamException(errorMsg:String) extends ErrorException(70012, errorMsg)

case class AppJointErrorException(errorMsg:String) extends ErrorException(70058, errorMsg)