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

import{ NODETYPE, NODEICON } from '@/apps/workflows/service/nodeType';
import i18n from '@/common/i18n'
export default [
  {
    title: i18n.t('message.workflow.shape.SJJH'), // 节点分类名
    children: [
      {
        type: NODETYPE.EXCHANGE, // 节点类型
        title: 'exchange', // 节点title
        image: NODEICON[NODETYPE.EXCHANGE].icon, // 节点图标
        editParam: false, // 是否支持插件参数弹窗
        editBaseInfo: false, // 是否支持节点基础信息编辑
      },
    ],
  },
  {
    title: i18n.t('message.workflow.shape.dataDev'),
    children: [{
      type: NODETYPE.SHELL,
      title: 'shell',
      image: NODEICON[NODETYPE.SHELL].icon,
      editParam: false,
      editBaseInfo: false,
    },
    {
      type: NODETYPE.HQL,
      title: 'hql',
      image: NODEICON[NODETYPE.HQL].icon,
      editParam: false,
      editBaseInfo: false,
    },
    {
      type: NODETYPE.SPARKSQL,
      title: 'sql',
      image: NODEICON[NODETYPE.SPARKSQL].icon,
      editParam: false,
      editBaseInfo: false,
    },
    {
      type: NODETYPE.SPARKPY,
      title: 'pyspark',
      image: NODEICON[NODETYPE.SPARKPY].icon,
      editParam: false,
      editBaseInfo: false,
    },
    {
      type: NODETYPE.SCALA,
      title: 'scala',
      image: NODEICON[NODETYPE.SCALA].icon,
      editParam: false,
      editBaseInfo: false,
    },
    {
      type: NODETYPE.PYTHON,
      title: 'python',
      image: NODEICON[NODETYPE.PYTHON].icon,
      editParam: false,
      editBaseInfo: false,
    }],
  },
  {
    title: i18n.t('message.workflow.shape.SJZL'),
    children: [
      {
        type: NODETYPE.QUALITIS,
        title: 'qualitis',
        image: NODEICON[NODETYPE.QUALITIS].icon,
        editParam: false,
        editBaseInfo: false,
      }
    ],
  },
  {
    title: i18n.t('message.workflow.shape.dataVis'),
    children: [
      {
        type: NODETYPE.DISPLAY,
        title: 'display',
        image: NODEICON[NODETYPE.DISPLAY].icon,
        editParam: false,
        editBaseInfo: false,
      },
      {
        type: NODETYPE.DASHBOARD,
        title: 'dashboard',
        image: NODEICON[NODETYPE.DASHBOARD].icon,
        editParam: false,
        editBaseInfo: false,
      },
      {
        type: NODETYPE.WIDGET,
        title: 'widget',
        image: NODEICON[NODETYPE.WIDGET].icon,
        editParam: false,
        editBaseInfo: false,
      },
    ],
  },

  {
    title: i18n.t('message.workflow.shape.dataSend'),
    children: [
      {
        type: NODETYPE.SENDMAIL,
        title: 'sendemail',
        image: NODEICON[NODETYPE.SENDMAIL].icon,
        editParam: false,
        editBaseInfo: false,
      },
    ],
  },
  {
    title: i18n.t('message.workflow.shape.XHJD'),
    children: [{
      type: NODETYPE.EVENTCHECKERF,
      title: 'eventsender',
      image: NODEICON[NODETYPE.EVENTCHECKERF].icon,
      editParam: false,
      editBaseInfo: false,
    }, {
      type: NODETYPE.EVENTCHECKERW,
      title: 'eventreceiver',
      image: NODEICON[NODETYPE.EVENTCHECKERW].icon,
      editParam: false,
      editBaseInfo: false,
    },
    {
      type: NODETYPE.DATACHECKER,
      title: 'datachecker',
      image: NODEICON[NODETYPE.DATACHECKER].icon,
      editParam: false,
      editBaseInfo: false,
    },
    {
      type: NODETYPE.RMBSENDER,
      title: 'rmbsender',
      image: NODEICON[NODETYPE.RMBSENDER].icon,
      editParam: false,
      editBaseInfo: false,
    }
    ],
  },
  {
    title: i18n.t('message.workflow.shape.GNJD'),
    children: [
      {
        type: NODETYPE.FLOW,
        title: 'subFlow',
        image: NODEICON[NODETYPE.FLOW].icon,
        editParam: false,
        editBaseInfo: false,
      },
      {
        type: NODETYPE.CONNECTOR,
        title: 'connector',
        image: NODEICON[NODETYPE.CONNECTOR].icon,
        editParam: false,
        editBaseInfo: false,
      },
    ],
  }];
