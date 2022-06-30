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

package com.webank.wedatasphere.dss.standard.app.sso.origin.request

/**
 * 通常用于请求与 DSS 打通了一级规范的第三方系统，是默认的使用方案
 */
class OriginSSORequestServiceImpl extends HttpSSORequestServiceImpl {

  override protected def createHttpSSORequestOperation(appName: String): HttpSSORequestOperation =
    new OriginSSORequestOperation(appName)

  override def createSSORequestOperation(appName: String): OriginSSORequestOperation = {
    super.createSSORequestOperation(appName).asInstanceOf[OriginSSORequestOperation]
  }
}
