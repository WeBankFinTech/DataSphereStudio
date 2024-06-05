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

import Vue from 'vue';
export default {
  name: 'WorkSidebar',
  /**
     * events在模块里的组件created时才加入监听者队列
     * 如果页面未加载vue，这里暴露的接口是无法被调用的
     * 可以提供跟视图相关，逻辑相关的接口
     * 命名方式是："模块名：xxx"
     */
  events: ['WorkSidebar:setHighLight', 'WorkSidebar:revealInSideBar'],
  dispatchs: {
    Workbench: ['add', 'run', 'openFile', 'remove', 'updateTab', 'checkExist', 'deleteDirOrFile', 'isOpenTab'],
    HdfsSidebar: ['showTree'],
    HiveSidebar: ['showHive', 'getDatabase', 'getTables', 'getTablePartitions'],
    IndexedDB: ['getTabs', 'appendTree', 'getTree'],
  },
  data: {
    API_PATH: process.env.VUE_APP_MN_CONFIG_PREFIX || `${location.protocol}//${window.location.host}/api/rest_j/v1/`,
  },
  /**
     * methods在js被加载时就会加入监听者队列
     * 如果页面未加载vue，这里暴露的接口可以被调用
     * 提供一些跟vue无关的东西，大多是数据
     */
  methods: {
    showTree(cb) {
      // get tree
      const WorkSpace = Vue.extend(require('./workSidebar.vue').default);
      const newW = new WorkSpace();
      cb(newW);
    },
  },
  component: () =>
    import('./workSidebar.vue'),
};
