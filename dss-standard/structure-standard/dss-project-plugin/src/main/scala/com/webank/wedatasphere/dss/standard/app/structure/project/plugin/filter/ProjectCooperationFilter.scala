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

package com.webank.wedatasphere.dss.standard.app.structure.project.plugin.filter

import java.util
import java.util.concurrent.TimeUnit

import com.webank.wedatasphere.dss.standard.app.sso.SSOIntegrationStandard
import com.webank.wedatasphere.dss.standard.app.sso.origin.OriginSSOIntegrationStandardFactory
import com.webank.wedatasphere.dss.standard.app.sso.plugin.filter.UserInterceptor
import com.webank.wedatasphere.dss.standard.app.structure.project.plugin.conf.ProjectCooperateConfiguration
import com.webank.wedatasphere.dss.standard.app.structure.project.plugin.filter.ProjectRequestType.{Access, Delete, Edit, Execute}
import com.webank.wedatasphere.dss.standard.app.structure.project.plugin.{ProjectAuth, ProjectCooperationPlugin}
import com.webank.wedatasphere.dss.standard.sso.utils.ProxyUserSSOUtils
import org.apache.linkis.common.conf.Configuration
import org.apache.linkis.common.utils.{Logging, Utils}
import org.apache.linkis.httpclient.exception.HttpClientResultException
import javax.servlet._
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}
import org.apache.commons.lang.StringUtils


abstract class ProjectCooperationFilter extends Filter with Logging {

  protected var projectAuthInterceptor: ProjectAuthInterceptor = _
  protected var userInterceptor: UserInterceptor = _
  protected var projectCooperationPlugin: ProjectCooperationPlugin = _
  private val cachedProjectAuthsWithId = new util.HashMap[String, TimeoutProjectAuth]()
  private val cachedProjectAuthsWithName = new util.HashMap[String, TimeoutProjectAuth]()
  private val ignoreProjectWithId = new util.ArrayList[String]()
  private val ignoreProjectWithName = new util.ArrayList[String]()

  override def init(filterConfig: FilterConfig): Unit = {
    projectAuthInterceptor = getProjectAuthInterceptor(filterConfig)
    userInterceptor = getUserAuthInterceptor(filterConfig)
    projectCooperationPlugin = getProjectCooperationPlugin(filterConfig)
  }

  protected def getProjectAuthInterceptor(filterConfig: FilterConfig): ProjectAuthInterceptor
  protected def getUserAuthInterceptor(filterConfig: FilterConfig): UserInterceptor
  protected def getProjectCooperationPlugin(filterConfig: FilterConfig): ProjectCooperationPlugin

  override def doFilter(servletRequest: ServletRequest, servletResponse: ServletResponse,
                        filterChain: FilterChain): Unit = {
    val req = servletRequest.asInstanceOf[HttpServletRequest]
    val uri = req.getRequestURI
    val ssoIntegrationStandard:SSOIntegrationStandard =new OriginSSOIntegrationStandardFactory().getSSOIntegrationStandard
    if(ssoIntegrationStandard.getSSOPluginService
      .createDssMsgCacheOperation().getWorkspaceInSession(req) == null) {
      info(s"Request $uri has no relation with DSS, ignore it.")
      filterChain.doFilter(servletRequest, servletResponse)
      return
    }
    val resp = servletResponse.asInstanceOf[HttpServletResponse]
    if(!projectAuthInterceptor.isProjectRequest(req)) {
      info(s"Request $uri is not a project request, ignore it.")
      filterChain.doFilter(req, resp)
      return
    }
    val projectId = projectAuthInterceptor.getProjectId(req)
    if(ignoreProjectWithId.contains(projectId)) {
      info(s"The project $projectId is not a DSS project, ignore it, request is $uri.")
      filterChain.doFilter(req, resp)
      return
    }
    val projectAuth = if(StringUtils.isNotBlank(projectId)) {
      getProjectAuthById(req, projectId)
    } else {
      val projectName = projectAuthInterceptor.getProjectName(req)
      // If this request cannot get the projectId or projectName, ignore it.
      if(StringUtils.isBlank(projectName) || ignoreProjectWithName.contains(projectName)) {
        info(s"This request $uri cannot get the projectId or projectName, ignore it.")
        filterChain.doFilter(req, resp)
        return
      }
      getProjectAuthByName(req, projectName)
    }
    if(projectAuth == null) {
      filterChain.doFilter(req, resp)
      return
    }
    val projectRequestType = projectAuthInterceptor.getProjectRequestType(req)
    var user = userInterceptor.getUser(req)
    info(s"RequestURI: $uri, ProjectRequestType: $projectRequestType, ProjectAuth: $projectAuth, user: $user.")
    if(ProxyUserSSOUtils.existsProxyUser(user)) {
      // 这里使用代理用户的权限来表示实际的工程权限
      val pair = ProxyUserSSOUtils.getUserAndProxyUser(user)
      info(s"use proxy user ${pair.getValue} to decide if user ${pair.getKey} can $projectRequestType cooperation project ${projectAuth.getProjectName}.")
      user = pair.getValue
    }
    val passed = projectRequestType match {
      case Edit => projectAuth.getEditUsers.contains(user)
      case Access => projectAuth.getAccessUsers.contains(user)
      case Delete => projectAuth.getDeleteUsers.contains(user)
      case Execute => projectAuth.getDeleteUsers.contains(user)
    }
    if(passed) {
      info(s"Cooperation Project request $uri has passed auth validation.")
      filterChain.doFilter(req, resp)
    } else {
      val msg = projectAuthInterceptor.getForbiddenMsg(projectAuth, projectRequestType, req)
      info(s"Cooperation Project request $uri has been failed with auth validation for user $user.")
      resp.getOutputStream.write(msg.getBytes(Configuration.BDP_ENCODING.getValue))
      resp.setStatus(403)
      resp.getOutputStream.flush()
    }
  }

  protected def getProjectAuthById(req: HttpServletRequest, projectId: String): ProjectAuth =
    getProjectAuth(projectId, cachedProjectAuthsWithId,
      projectCooperationPlugin.getProjectAuth(req, projectId), ignoreProjectWithId)

  protected def getProjectAuthByName(req: HttpServletRequest, projectName: String): ProjectAuth =
    getProjectAuth(projectName, cachedProjectAuthsWithName,
      projectCooperationPlugin.getProjectAuthByName(req, projectName), ignoreProjectWithName)

  private def getProjectAuth(key: String,
                               map: util.HashMap[String, TimeoutProjectAuth],
                               getNewProjectAuth: => ProjectAuth,
                               list: util.ArrayList[String]): ProjectAuth = {
    var timeoutProjectAuth = map.get(key)
    if(timeoutProjectAuth == null || timeoutProjectAuth.isTimeout) {
      key.intern().synchronized {
        timeoutProjectAuth = map.get(key)
        if(timeoutProjectAuth == null || timeoutProjectAuth.isTimeout) {
          val projectAuth = Utils.tryCatch(getNewProjectAuth) {
            case t: HttpClientResultException =>
              if(t.getDesc.contains("not exists")) {
                list.add(key)
                return null
              } else throw t
            case t: Throwable => throw t
          }
          map.put(key, TimeoutProjectAuth(projectAuth))
        }
      }
    }
    map.get(key).projectAuth
  }

  override def destroy(): Unit = {}

  private case class TimeoutProjectAuth(createTime: Long, projectAuth: ProjectAuth) {
    def isTimeout: Boolean = System.currentTimeMillis() - createTime > ProjectCooperateConfiguration.EXPIRE_PROJECT_AUTH_TIMEOUT.getValue.toLong
  }
  private object TimeoutProjectAuth {
    def apply(projectAuth: ProjectAuth): TimeoutProjectAuth =
      new TimeoutProjectAuth(System.currentTimeMillis(), projectAuth)
  }

  Utils.defaultScheduler.scheduleAtFixedRate(new Runnable {
    override def run(): Unit = {
      cachedProjectAuthsWithId.keySet().toArray
        .foreach{ case k: String => if(cachedProjectAuthsWithId.get(k).isTimeout) cachedProjectAuthsWithId.remove(k)}
      cachedProjectAuthsWithName.keySet().toArray
        .foreach{ case k: String => if(cachedProjectAuthsWithName.get(k).isTimeout) cachedProjectAuthsWithName.remove(k)}
    }
  } , ProjectCooperateConfiguration.EXPIRE_PROJECT_AUTH_SCAN_INTERVAL.getValue.toLong, ProjectCooperateConfiguration.EXPIRE_PROJECT_AUTH_SCAN_INTERVAL.getValue.toLong, TimeUnit.MILLISECONDS)

}
