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

export default {
  name: 'Workbench',
  // 规范模块监测什么事件
  events: ['Workbench:add', 'Workbench:deleteDirOrFile', 'Workbench:save', 'Workbench:socket', 'Workbench:openFile', 'Workbench:run', 'Workbench:pasteInEditor', 'Workbench:saveAs', 'Workbench:updateTab', 'Workbench:setResultCache', 'Workbench:setResult', 'Workbench:insertValue', 'Workbench:checkExist', 'Workbench:getWorksLangList', 'Workbench:setEditorPanelSize', 'Workbench:setTabPanelSize', 'Workbench:updateFlowsTab', 'Workbench:resetScriptData', 'Workbench:removeWork', 'Workbench:updateFlowsNodeName', 'Workbench:isOpenTab', 'Workbench:removeTab'],
  // 规范模块能够触发什么事件
  dispatchs: {
    Workbench: ['save', 'saveAs', 'run', 'add', 'setResultCache', 'setResult', 'insertValue', 'openFile', 'checkExist', 'setEditorPanelSize', 'setTabPanelSize', 'resetScriptData', 'removeWork', 'repeatWork', 'removeTab'],
    IndexedDB: [
      'appendLog', 'clearLog', 'getLog', 'changeLogKey',
      'updateHistory', 'appendHistory', 'getHistory', 'clearHistory', 'changeHistoryKey',
      'updateResult', 'appendResult', 'getResult', 'clearResult', 'changResultKey',
      'updateProgress', 'clearProgress', 'getProgress', 'changProgressKey',
      'getTabs', 'recordTab', 'toggleTab', 'removeTab', 'changeTabKey',
      'getGlobalCache', 'setGlobalCache', 'updateGlobalCache', 'removeGlobalCache', 'getTree', 'appendTree', 'clearTab'
    ],
    WebSocket: ['init', 'send'],
    WorkSidebar: ['setHighLight', 'showTree', 'revealInSideBar'],
    HdfsSidebar: ['setHighLight', 'showTree'],
    HiveSidebar: ['getAllDbsAndTables', 'deletedAndRefresh', 'showHive', 'getAllowMap'],
    fnSidebar: ['getAllLoadedFunction'],
    Footer: ['updateRunningJob'],
    GlobalValiable: ['getGlobalVariable'],
    IDE: ['saveNode'],
  },
  data: {
    API_PATH: process.env.VUE_APP_MN_CONFIG_PREFIX || `${location.protocol}//${window.location.host}/api/rest_j/v1/`,
    BI_API_PATH: 'dws/vg#',
  },
  component: () =>
    import('./container.vue'),
  config: {
  },
};
