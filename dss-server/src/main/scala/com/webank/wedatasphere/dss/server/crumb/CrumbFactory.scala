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

package com.webank.wedatasphere.dss.server.crumb

import com.webank.wedatasphere.dss.server.entity.CrumbType.CrumbType
import com.webank.wedatasphere.dss.server.entity.{Crumb, CrumbType, DSSFlowTaxonomy, DSSProjectTaxonomy}
import com.webank.wedatasphere.dss.server.service.impl.{DSSFlowServiceImpl, DSSFlowTaxonomyServiceImpl, DSSProjectServiceImpl, DSSProjectTaxonomyServiceImpl}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import scala.collection.mutable.ArrayBuffer


@Component
class CrumbFactory {
  @Autowired
  private var projectService: DSSProjectServiceImpl = _
  @Autowired
  private var projectTaxonomyService:DSSProjectTaxonomyServiceImpl = _
  @Autowired
  private var flowService: DSSFlowServiceImpl = _
  @Autowired
  private var flowTaxonomyService:DSSFlowTaxonomyServiceImpl = _

  def createCrumbs(crumbType: CrumbType, params: Array[String]): Array[Crumb] = {
    crumbType match {
      case CrumbType.All => Array(createIndexCrumb, createAllCrumb)
      case CrumbType.SortProject => Array(createIndexCrumb, createAllCrumb, createSortProjectCrumb(params))
      case CrumbType.Project => Array(createIndexCrumb, createAllCrumb, createSortProjectCrumb(subParams(params,Array(1))),
        createProjectCrumb(params))
      case CrumbType.SortFlow => Array(createIndexCrumb, createAllCrumb, createSortProjectCrumb(subParams(params,Array(1))),
        createProjectCrumb(subParams(params,Array(1,2,4))), createSortFlowCrumb(params))
      case CrumbType.Flow => Array(createIndexCrumb, createAllCrumb, createSortProjectCrumb(subParams(params,Array(1))),
        createProjectCrumb(subParams(params,Array(1,2,5))), createSortFlowCrumb(subParams(params,Array(1,2,3,5))),
        createFlowCrumb(params))
    }
  }

  private def subParams(params: Array[String], index: Array[Int]): Array[String] = {
    val subParams = new ArrayBuffer[String]
    for (i <- 1 to params.length) {
      if (index.contains(i)) subParams += params(i - 1)
    }
    subParams.toArray
  }

  private def createSortProjectCrumb(params: Array[String]): Crumb = {
    val paramsMap = QuerParamsParser.toMap(params)
    val projectTaxonomy = projectTaxonomyService.getProjectTaxonomyByID(paramsMap.get("projectTaxonomyID").toLong)
    new Crumb(CrumbType.SortProject, QuerParamsParser.toQueryString(params), projectTaxonomy.getName, null)
  }

  private def createProjectCrumb(params: Array[String]): Crumb = {
    val paramsMap = QuerParamsParser.toMap(params)
    val project = projectService.getProjectByProjectVersionID(paramsMap.get("projectVersionID").toLong)
    new Crumb(CrumbType.Project, QuerParamsParser.toQueryString(params), project.getName, null)
  }

  private def createSortFlowCrumb(params: Array[String]): Crumb = {
    val paramsMap = QuerParamsParser.toMap(params)
    val flowTaxonomy = flowTaxonomyService.getFlowTaxonomyByID(paramsMap.get("flowTaxonomyID").toLong)
    new Crumb(CrumbType.SortFlow, QuerParamsParser.toQueryString(params), flowTaxonomy.getName, null)
  }

  private def createFlowCrumb(params: Array[String]): Crumb = {
    val paramsMap = QuerParamsParser.toMap(params)
    val flow = flowService.getFlowByID(paramsMap.get("flowID").toLong)
    new Crumb(CrumbType.Flow, QuerParamsParser.toQueryString(params), flow.getName, null)
  }

  private def createIndexCrumb: Crumb = {
    new Crumb(CrumbType.Index, "", "首页", null)
  }

  private def createAllCrumb: Crumb = {
    new Crumb(CrumbType.All, "", "工程开发", null)
  }

  def createCrumbTree(crumbType: CrumbType, params: java.util.Map[String, String]): Crumb = {
    crumbType match {
      case CrumbType.All =>
      case CrumbType.SortProject =>
      case CrumbType.Project =>
      case CrumbType.SortFlow =>
      case CrumbType.Flow =>
    }
    null
  }

  def createCrumbData(crumbType: CrumbType, params: java.util.Map[String, String],userName:String): Any = {
    crumbType match {
      case CrumbType.All =>createAllData(userName)
      case CrumbType.SortProject =>createSortProjectData(params.get("projectTaxonomyID").toLong,userName)
      case CrumbType.Project =>createProjectData(params.get("projectVersionID").toLong,params.get("isRootFlow").toBoolean)
      case CrumbType.SortFlow =>createSortFlowData(params.get("projectVersionID").toLong,params.get("flowTaxonomyID").toLong,params.get("isRootFlow").toBoolean)
      case CrumbType.Flow =>null
    }
  }

  private def createAllData(userName:String):java.util.List[DSSProjectTaxonomy] ={
    projectTaxonomyService.listAllProjectTaxonomy(userName)
  }

  private def createSortProjectData(projectTaxonomyID:Long,userName:String):java.util.List[DSSProjectTaxonomy] ={
    projectTaxonomyService.listProjectTaxonomy(projectTaxonomyID,userName)
  }

  private def createProjectData(projectVersionID:Long,isRootFlow:Boolean):java.util.List[DSSFlowTaxonomy] = {
    flowTaxonomyService.listAllFlowTaxonomy(projectVersionID,isRootFlow)
  }

  private def createSortFlowData(projectVersionID:Long,flowTaxonomyID:Long,isRootFlow:Boolean):java.util.List[DSSFlowTaxonomy] = {
    flowTaxonomyService.listFlowTaxonomy(projectVersionID,flowTaxonomyID:Long,isRootFlow:Boolean)
  }
}

/*object Test {
  def main(args: Array[String]): Unit = {
    import collection.JavaConversions._
    val a = new util.HashMap[String, String]()
    a.put("a", "b")
    a += "c" -> "d"
    a += "e" -> "f"
    a += "g" -> "dh"
    a += "cerq" -> "dwer"
    val b = new CrumbFactory
    print(b.removeParams(a, "565"))
  }
}*/
