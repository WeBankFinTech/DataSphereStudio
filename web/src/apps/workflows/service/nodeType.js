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

import shell from '../module/process/images/shell.svg';
import flow from '../module/process/images/newIcon/flow.svg';
import eventchecker from '../module/process/images/newIcon/eventcheckerf.svg';
import sparkPython from '../module/process/images/pyspark.svg';
import sql from '../module/process/images/newIcon/spark.svg';
import hivesql from '../module/process/images/newIcon/hive.svg';
import veceive from '../module/process/images/newIcon/eventcheckerw.svg';
import connect from '../module/process/images/newIcon/connector.svg';
import datacheck from '../module/process/images/newIcon/datacheck.svg';
import send from '../module/process/images/sender.svg';
import display from '../module/process/images/newIcon/display.svg';
import dashboard from '../module/process/images/newIcon/Dashboard.svg';
import widget from '../module/process/images/newIcon/widget.svg';
import sendmail from '../module/process/images/newIcon/email.svg';
import scala from '../module/process/images/scala.svg';
import exchange from '../module/process/images/newIcon/exchange.svg';
import qualitis from '../module/process/images/newIcon/qualitis.svg';
import python from '../module/process/images/newIcon/python.svg';
import projectNode from '../module/process/images/newIcon/projectNode.svg'
import mlss from '../module/process/images/newIcon/mlss.svg'
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
  SENDMAIL: 'linkis.appconn.sendemail',
  EVENTCHECKERF: 'linkis.appconn.eventchecker.eventsender',
  EVENTCHECKERW: 'linkis.appconn.eventchecker.eventreceiver',
  DATACHECKER: 'linkis.appconn.datachecker',
  RMBSENDER: 'azkaban.rmbsender',
  FLOW: 'workflow.subflow',
  EXCHANGE: 'linkis.data.exchange',
  QUALITIS: 'linkis.appconn.qualitis',
  PROJECTNODE: 'projectNode',
  MLSS: 'linkis.appconn.mlss'
}
const ext = {
  [NODETYPE.SHELL]: 'shell',
  [NODETYPE.HQL]: 'hql',
  [NODETYPE.SPARKSQL]: 'sql',
  [NODETYPE.SPARKPY]: 'pyspark',
  [NODETYPE.SCALA]: 'scala',
  [NODETYPE.PYTHON]: 'python'
}
const NODEICON = {
  [NODETYPE.SHELL]: {
    icon: shell,
    class: {'shell': true}
  },
  [NODETYPE.HQL]: {
    icon: hivesql,
    class: {'hivesql': true}
  },
  [NODETYPE.SPARKSQL]: {
    icon: sql,
    class: {'sql': true}
  },
  [NODETYPE.SPARKPY]: {
    icon: sparkPython,
    class: {'sparkPython': true}
  },
  [NODETYPE.SCALA]: {
    icon: scala,
    class: {'scala': true}
  },
  [NODETYPE.PYTHON]: {
    icon: python,
    class: {'python': true}
  },
  [NODETYPE.CONNECTOR]: {
    icon: connect,
    class: {'connect': true}
  },
  [NODETYPE.DISPLAY]: {
    icon: display,
    class: {'display': true}
  },
  [NODETYPE.DASHBOARD]: {
    icon: dashboard,
    class: {'dashboard': true}
  },
  [NODETYPE.WIDGET]: {
    icon: widget,
    class: {'widget': true}
  },
  [NODETYPE.SENDMAIL]: {
    icon: sendmail,
    class: {'sendmail': true}
  },
  [NODETYPE.EVENTCHECKERF]: {
    icon: eventchecker,
    class: {'eventchecker': true}
  },
  [NODETYPE.EVENTCHECKERW]: {
    icon: veceive,
    class: {'veceive': true}
  },
  [NODETYPE.DATACHECKER]: {
    icon: datacheck,
    class: {'datacheck': true}
  },
  [NODETYPE.RMBSENDER]: {
    icon: send,
    class: {'send': true}
  },
  [NODETYPE.FLOW]: {
    icon: flow,
    class: {'flow': true}
  },
  [NODETYPE.EXCHANGE]: {
    icon: exchange,
    class: {'exchange': true}
  },
  [NODETYPE.QUALITIS]: {
    icon: qualitis,
    class: {'qualitis': true}
  },
  [NODETYPE.PROJECTNODE]: {
    icon: projectNode,
    class: {'projectNode': true}
  },
  [NODETYPE.MLSS]: {
    icon: mlss,
    class: {'mlss': true}
  },
}
export { NODETYPE, ext, NODEICON};
