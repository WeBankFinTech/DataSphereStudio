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

import api from '@dataspherestudio/shared/common/service/api';
import Vue from 'vue';
export default {
  name: 'HiveSidebar',
  events: ['showHive', 'getAllDbsAndTables', 'getAllowMap', 'HiveSidebar:getDatabase', 'HiveSidebar:getTables', 'HiveSidebar:getTablePartitions', 'HiveSidebar:deletedAndRefresh'],
  dispatchs: ['Workbench:add', 'Workbench:run', 'Workbench:pasteInEditor', 'HiveSidebar:getTables', 'WorkSidebar:showTree', 'HdfsSidebar:showTree', 'IndexedDB:getTree', 'IndexedDB:appendTree'],
  data: {
    API_PATH: process.env.VUE_APP_MN_CONFIG_PREFIX || `http://${window.location.host}/api/rest_j/v1/`,
    ALLOW_MAP: ['_ind', '_share', '_work', '_qml', '_tmp', '_bak'],
  },
  methods: {
    getAllDbsAndTables() {
      return new Promise((resolve, reject) => {
        api.fetch(`/datasource/all`, 'get').then((rst) => {
          const hiveList = [];
          if (rst.dbs) {
            rst.dbs.forEach((list) => {
              if (list.tables.length) {
                list.tables.forEach((table) => {
                  hiveList.push({
                    meta: 'tbname',
                    value: table.tableName,
                    caption: list.databaseName + '.' + table.tableName,
                    documentation: `the name of a table, which belong to the db: ${list.databaseName}.`,
                  });
                });
              }
              hiveList.push({
                meta: 'dbname',
                value: list.databaseName,
                caption: list.databaseName,
                documentation: 'the name of a database.',
              });
            });
          }
          resolve(hiveList);
        }).catch(() => {
          reject('未获取到数据库表信息，脚本联想词功能可能存在异常！可刷新重试！');
        });
      });
    },
    showHive(arg, cb) {
      const Hive = Vue.extend(require('./hiveSidebar.vue').default);
      const newW = new Hive();
      cb(newW);
    },
    getAllowMap(cb) {
      cb(this.data.ALLOW_MAP);
    }
  },
  component: () => import('./hiveSidebar.vue'),
};
