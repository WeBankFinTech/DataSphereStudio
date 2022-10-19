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
  name: 'HdfsSidebar',
  events: ['HdfsSidebar:setHighLight'],
  dispatchs: {
    Workbench: ['add', 'openFile', 'run', 'remove', 'updateTab', 'checkExist', 'deleteDirOrFile'],
    WorkSidebar: ['showTree'],
    HiveSidebar: ['showHive', 'getDatabase', 'getTables', 'getTablePartitions'],
    IndexedDB: ['getTabs', 'getTree', 'appendTree'],
  },
  data: {
    API_PATH: process.env.VUE_APP_MN_CONFIG_PREFIX || `${location.protocol}//${window.location.host}/api/rest_j/v1/`,
  },
  methods: {
    showTree(cb) {
      const WorkSpace = Vue.extend(require('./hdfsSidebar.vue').default);
      const newW = new WorkSpace();
      cb(newW);
    },
  },
  component: () => import('./hdfsSidebar.vue'),
};
