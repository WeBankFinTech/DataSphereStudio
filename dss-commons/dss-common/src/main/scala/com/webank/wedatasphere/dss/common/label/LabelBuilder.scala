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

package com.webank.wedatasphere.dss.common.label

import com.webank.wedatasphere.linkis.manager.label.entity.Label

import scala.collection.JavaConverters._
import java.util

import com.webank.wedatasphere.linkis.manager.label.builder.factory.{LabelBuilderFactory, LabelBuilderFactoryContext}


/**
 * @author: jinyangrao on 2021/7/16
 * @description:
 */
object LabelBuilder {

  def buildLabel(labelMap: util.Map[String, Object]): util.Map[String, Label[_]] = {
    val labelKeyValueMap = new util.HashMap[String, Label[_]]()
    if (null != labelMap && !labelMap.isEmpty) {
      val list: util.List[Label[_]] = labelBuilderFactoryProxyMethod.getLabels(labelMap.asInstanceOf[util.Map[String, AnyRef]])
      if (null != list) {
        list.asScala.filter(_ != null).foreach {
          label => labelKeyValueMap.put(label.getLabelKey, label)
        }
      }
    }
    labelKeyValueMap
  }

  private def labelBuilderFactoryProxyMethod: LabelBuilderFactory = {
    LabelBuilderFactoryContext.getLabelBuilderFactory
  }
}
