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

package com.webank.wedatasphere.linkis.gateway.parser

import java.util

import com.webank.wedatasphere.linkis.common.ServiceInstance
import com.webank.wedatasphere.linkis.gateway.exception.TooManyServiceException
import com.webank.wedatasphere.linkis.gateway.http.GatewayContext
import com.webank.wedatasphere.linkis.gateway.springcloud.SpringCloudGatewayConfiguration.{API_URL_PREFIX, normalPath}
import com.webank.wedatasphere.linkis.manager.label.builder.factory.LabelBuilderFactoryContext
import com.webank.wedatasphere.linkis.manager.label.entity.Label
import com.webank.wedatasphere.linkis.manager.label.entity.route.RouteLabel
import com.webank.wedatasphere.linkis.protocol.constants.TaskConstant
import com.webank.wedatasphere.linkis.rpc.sender.SpringCloudFeignConfigurationCache
import com.webank.wedatasphere.linkis.server.BDPJettyServerHelper
import org.springframework.stereotype.Component

import scala.collection.JavaConversions._


@Component
class DSSGatewayParser extends AbstractGatewayParser {

  val appConns = DSSGatewayConfiguration.DSS_URL_APPCONNS.getValue.split(",")

  override def shouldContainRequestBody(gatewayContext: GatewayContext): Boolean = {
    var contentType = gatewayContext.getRequest.getHeaders.get("Content-Type")
    if(null == contentType) {
      contentType = gatewayContext.getRequest.getHeaders.get("content-type")
    }

    if(contentType != null && contentType.nonEmpty
          && contentType(0).contains("form-data")){
      logger.info("DSS gateway get request type is form-data")
      return false
    }

    gatewayContext.getRequest.getRequestURI match {
      case DSSGatewayParser.DSS_URL_DEFAULT_REGEX(_, _) => true
      case DSSGatewayParser.DSS_URL_REGEX(_, _,_) => true
      case DSSGatewayParser.APPCONN_URL_DEFAULT_REGEX(_,appconn, _) if appConns.contains(appconn) => true
      case _ => false
    }
  }

  override def parse(gatewayContext: GatewayContext): Unit = gatewayContext.getRequest.getRequestURI match {

    case DSSGatewayParser.DSS_URL_REGEX(version, firstName, secondName) =>
      if (sendResponseWhenNotMatchVersion(gatewayContext, version)) return
      var tmpServerName = "dss-" + firstName + "-" + secondName + "-server"
      tmpServerName = getServiceNameFromLabel(gatewayContext, tmpServerName)
      val serviceName: Option[String] = findCommonService("dss/" + firstName + "/" + secondName, tmpServerName)
      if (serviceName.isDefined) {
        gatewayContext.getGatewayRoute.setServiceInstance(ServiceInstance(serviceName.get, null))
      } else {
        logger.info("Now set default serviceInstance name " + DSSGatewayConfiguration.DSS_SPRING_NAME.getValue + "," + gatewayContext.getRequest.getRequestURI)
        gatewayContext.getGatewayRoute.setServiceInstance(ServiceInstance(DSSGatewayConfiguration.DSS_SPRING_NAME.getValue, null))
      }
    case DSSGatewayParser.DSS_URL_DEFAULT_REGEX(version, firstName) =>
      if (sendResponseWhenNotMatchVersion(gatewayContext, version)) return
      var tmpServerName = "dss-" + firstName + "-server"
      tmpServerName = getServiceNameFromLabel(gatewayContext, tmpServerName)
      val serviceName: Option[String] = findCommonService("dss/" + firstName, tmpServerName)
      if (serviceName.isDefined) {
        gatewayContext.getGatewayRoute.setServiceInstance(ServiceInstance(serviceName.get, null))
      } else {
        logger.info("Now set default serviceInstance name " + DSSGatewayConfiguration.DSS_SPRING_NAME.getValue + "," + gatewayContext.getRequest.getRequestURI)
        gatewayContext.getGatewayRoute.setServiceInstance(ServiceInstance(DSSGatewayConfiguration.DSS_SPRING_NAME.getValue, null))
      }
    case DSSGatewayParser.APPCONN_URL_DEFAULT_REGEX(version, serverName, _) if appConns.contains(serverName) =>
      if (sendResponseWhenNotMatchVersion(gatewayContext, version)) return
      var tmpServerName = serverName
      tmpServerName = getServiceNameFromLabel(gatewayContext, tmpServerName)
      val serviceName: Option[String] = findCommonService(tmpServerName, tmpServerName)
      if (serviceName.isDefined) {
        gatewayContext.getGatewayRoute.setServiceInstance(ServiceInstance(serviceName.get, null))
      } else {
        logger.info("Now set default serviceInstance name " + DSSGatewayConfiguration.DSS_SPRING_NAME.getValue + "," + gatewayContext.getRequest.getRequestURI)
        gatewayContext.getGatewayRoute.setServiceInstance(ServiceInstance(DSSGatewayConfiguration.DSS_SPRING_NAME.getValue, null))
      }
    case _ =>
  }

  private def getServiceNameFromLabel(gatewayContext: GatewayContext, tmpServiceName: String): String = {
    var requestUrlLabels = gatewayContext.getRequest.getQueryParams.getOrDefault(DSSGatewayConfiguration.DSS_URL_LABEL_PREFIX.getValue, null)
    if(requestUrlLabels == null) requestUrlLabels = gatewayContext.getRequest.getQueryParams.getOrDefault(DSSGatewayConfiguration.DSS_URL_ROUTE_LABEL_PREFIX.getValue,null)
    logger.info("Get ServiceName From  Label and method is "+ gatewayContext.getRequest.getMethod.toString+",and urlLabels is "+requestUrlLabels)
    if (requestUrlLabels == null && gatewayContext.getRequest.getMethod.equalsIgnoreCase("post")) {
      val requestBody = Option(gatewayContext.getRequest.getRequestBody)
      val routeLabelList = new util.ArrayList[RouteLabel]()

      requestBody match {
          //todo form-data resolve
        case Some(body) =>
          val labelBuilderFactory = LabelBuilderFactoryContext.getLabelBuilderFactory
          val json = BDPJettyServerHelper.gson.fromJson(body, classOf[java.util.Map[String, Object]])
          val labels: util.List[Label[_]] = json.get(TaskConstant.LABELS) match {
            case map: util.Map[String, Object] => labelBuilderFactory.getLabels(map)
            case map: util.Map[String, Any] => labelBuilderFactory.getLabels(map.asInstanceOf)
            case _ => new util.ArrayList[Label[_]]()
          }
          labels.filter(label => label.isInstanceOf[RouteLabel]).foreach(label => {
            routeLabelList.add(label.asInstanceOf[RouteLabel])
          })

        case _ => null
      }
      val labelNameList = routeLabelList.map(routeLabel => routeLabel.getStringValue).toList
      if(labelNameList != null && labelNameList.size>0) {
        genServiceNameByDSSLabel(labelNameList, tmpServiceName)
      }else if(null != requestUrlLabels){
        genServiceNameByDSSLabel(requestUrlLabels.toList, tmpServiceName)
      } else tmpServiceName

    } else {
      if(requestUrlLabels != null) {
        genServiceNameByDSSLabel(requestUrlLabels.toList, tmpServiceName)
      }else tmpServiceName
    }
  }

  private def genServiceNameByDSSLabel(labelList: List[String], tmpServiceName: String): String = {
    var resultName = tmpServiceName
    if (null != labelList && labelList.size > 0) {
      val labelNameList = labelList(0).replace(" ", "").split(",").toList
      if (labelNameList.size > 0) {
        if (labelNameList.find(name => name.equalsIgnoreCase("dev")).isDefined) {
          resultName = tmpServiceName + "-dev"
        } else if (labelNameList.find(name => name.equalsIgnoreCase("prod")).isDefined) {
          resultName = tmpServiceName + "-prod"
        } else if (labelNameList.find(name => name.equalsIgnoreCase("test")).isDefined){
          resultName = tmpServiceName + "-test"
        }else{
          resultName = tmpServiceName
        }
      }
    }
    resultName
  }


  private def findCommonService(parsedServiceId: String, tmpServerName: String) = findService(parsedServiceId, tmpServerName, services => {
    val errorMsg = new TooManyServiceException(s"Cannot find a correct serviceId for parsedServiceId $parsedServiceId, service list is: " + services)
    warn("", errorMsg)
    throw errorMsg
  })

  protected def findService(parsedServiceId: String, tmpServerName: String, tooManyDeal: List[String] => Option[String]): Option[String] = {
    val findIt: (String => Boolean) => Option[String] = op => {
      val services = SpringCloudFeignConfigurationCache.getDiscoveryClient
        .getServices.filter(op).toList
      if (services.length == 1) Some(services.head)
      else if (services.length > 1) tooManyDeal(services)
      else None
    }
    //通过匹配到最多的url中的path进行路由,如/dss/framework/workspace/ 会匹配到 dss-framework-workspace-server 而不是 dss-server
    // 如果产生了相等的情况，则按照短的service名字为准 比如/dss/getProject,
    // 我们可能会匹配到dss-server以及 dss-framework-workspace-server,则选择短名称的dss-server
    val findMostCorrect: (String => (String, Int)) => Option[String] = { op => {
      val serviceMap = SpringCloudFeignConfigurationCache.getDiscoveryClient
        .getServices.map(op).toMap
      var count = 0
      var retService: Option[String] = None
      serviceMap.foreach {
        case (k, v) => if (v > count) {
          count = v
          retService = Some(k)
        } else if (retService.isDefined && v == count && k.length < retService.get.length) {
          retService = Some(k)
        }
      }
      retService
    }
    }
    val lowerServiceId = parsedServiceId.toLowerCase
    val serverName = tmpServerName.toLowerCase
    //findIt(_.toLowerCase == lowerServiceId).orElse(findIt(_.toLowerCase.contains(lowerServiceId)))
    findIt(_.toLowerCase == serverName).orElse(findMostCorrect(service => {
      (service, lowerServiceId.split("/").count(word => service.contains(word)))
    }))
  }


}

object DSSGatewayParser {
  val DSS_HEADER = normalPath(API_URL_PREFIX) + "rest_[a-zA-Z][a-zA-Z_0-9]*/(v\\d+)/dss/"
  val DSS_URL_REGEX = (DSS_HEADER + "([^/]+)/" + "([^/]+)/.+").r
  val DSS_URL_DEFAULT_REGEX = (DSS_HEADER + "([^/]+).+").r

  val APPCONN_HEADER = normalPath(API_URL_PREFIX) + "rest_[a-zA-Z][a-zA-Z_0-9]*/(v\\d+)/([^/]+)/"
  val APPCONN_URL_DEFAULT_REGEX = (APPCONN_HEADER + "([^/]+).+").r

}
