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

package com.webank.wedatasphere.linkis.gateway.parser

import java.util

import com.webank.wedatasphere.linkis.gateway.http.GatewayContext
import com.webank.wedatasphere.linkis.gateway.ujes.route.label.RouteLabelParser
import com.webank.wedatasphere.linkis.manager.label.builder.factory.LabelBuilderFactoryContext
import com.webank.wedatasphere.linkis.manager.label.entity.Label
import com.webank.wedatasphere.linkis.manager.label.entity.route.RouteLabel
import com.webank.wedatasphere.linkis.protocol.constants.TaskConstant
import com.webank.wedatasphere.linkis.server.BDPJettyServerHelper
import org.springframework.stereotype.Component

import scala.collection.JavaConversions._

/**
 * @author allenlliu
 * @date 2021/1/5 16:33
 */
@Component
class DSSRouteLabelParser  extends RouteLabelParser{
  override def parse(gatewayContext: GatewayContext): util.List[RouteLabel] = {
    val routeLabelList = new util.ArrayList[RouteLabel]()
    var requestLabels = gatewayContext.getRequest.getQueryParams.getOrDefault(DSSGatewayConfiguration.DSS_URL_LABEL_PREFIX.getValue,null)
    if(requestLabels == null) requestLabels =gatewayContext.getRequest.getQueryParams.getOrDefault(DSSGatewayConfiguration.DSS_URL_ROUTE_LABEL_PREFIX.getValue,null)
    if(null != requestLabels && requestLabels.size >0) {
        val labelNameList = requestLabels(0).replace(" ", "").split(",").toList
        if (labelNameList.size > 0) labelNameList.foreach(labelName => {
          val routeLabel = new RouteLabel
          routeLabel.setRoutePath(labelName)
          routeLabelList.add(routeLabel)
        })
      }
    if(routeLabelList.isEmpty){
      val requestBody = Option(gatewayContext.getRequest.getRequestBody)
      requestBody match {
        //todo form-data resolve
        case Some(body) =>if(body.contains("form-data")){
          //        val routeLabel:RouteLabel = new RouteLabel()
          //        if (body.toLowerCase.contains("dev")){
          //          routeLabel.setRoutePath("dev")
          //          routeLabelList.add(routeLabel)
          //        }else if(body.toLowerCase.contains("prod")){
          //          routeLabel.setRoutePath("prod")
          //          routeLabelList.add(routeLabel)
          //        }  else{
          //          //不使用标签，单服务部署
          //        }

        }
        else {
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
        }
        case _ => null
      }
    }

    routeLabelList
  }
}
