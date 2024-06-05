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

// import index from './index.vue';
export default {
  // 模块名称
  name: 'Process',
  // 规范模块监测什么事件，或者说模块对外提供什么接口
  events: [], // Demo:add
  // 规范模块能够触发其他模块什么事件或者说调用其他模块什么接口
  dispatchs: {
    WorkSidebar: ['showTree'],
    Workbench: ['updateFlowsTab', 'updateFlowsNodeName'],
    IndexedDB: ['clearLog', 'clearResult', 'clearProgress', 'updateResult',  'getTree', 'appendTree'],
    workflowIndexedDB: [
      'getNodeCache', 'updateNodeCache', 'removeNodeCache', 'addNodeCache', 'clearNodeCache',
    ],
  },
  // 规范模块的动作，由外部调用或者自己执行
  methods: {
  },
  data: {
    API_PATH: process.env.VUE_APP_MN_CONFIG_PREFIX || `${location.protocol}//${window.location.host}/api/rest_j/v1/`,
  },
  component: () => import('./index.vue'),
};
