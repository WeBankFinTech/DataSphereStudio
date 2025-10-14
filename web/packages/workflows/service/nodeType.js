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

const NODETYPE = {
  SHELL: 'linkis.shell.sh',
  HQL: 'linkis.hive.hql',
  SPARKSQL: 'linkis.spark.sql',
  SPARKPY: 'linkis.spark.py',
  SCALA: 'linkis.spark.scala',
  PYTHON: 'linkis.python.python',
  CONNECTOR: 'linkis.control.empty',
  DISPLAY: 'linkis.appconn.visualis.display',
  DASHBOARD: 'linkis.appconn.visualis.dashboard',
  WIDGET: 'linkis.appconn.visualis.widget',
  VIEW: 'linkis.appconn.visualis.view',
  SENDMAIL: 'linkis.appconn.sendemail',
  EVENTCHECKERF: 'linkis.appconn.eventchecker.eventsender',
  EVENTCHECKERW: 'linkis.appconn.eventchecker.eventreceiver',
  DATACHECKER: 'linkis.appconn.datachecker',
  RMBSENDER: 'azkaban.rmbsender',
  FLOW: 'workflow.subflow',
  EXCHANGE: 'linkis.data.exchange',
  QUALITIS: 'linkis.appconn.qualitis',
  PROJECTNODE: 'projectNode',
  MLSS: 'linkis.appconn.mlss',
  JDBC: 'linkis.jdbc.jdbc',
  NEBULA: 'linkis.nebula.nebula',
}

const ext = {
  [NODETYPE.SHELL]: 'shell',
  [NODETYPE.HQL]: 'hql',
  [NODETYPE.SPARKSQL]: 'sql',
  [NODETYPE.SPARKPY]: 'pyspark',
  [NODETYPE.SCALA]: 'scala',
  [NODETYPE.PYTHON]: 'python',
  [NODETYPE.JDBC]: 'jdbc',
  [NODETYPE.NEBULA]: 'nebula'
}

export { NODETYPE, ext};
