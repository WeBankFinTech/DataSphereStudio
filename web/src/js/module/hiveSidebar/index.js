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

import api from '@/js/service/api';
import Vue from 'vue';
export default {
  name: 'HiveSidebar',
  events: ['showHive', 'getAllDbsAndTables', 'HiveSidebar:getDatabase', 'HiveSidebar:getTables', 'HiveSidebar:getTablePartitions', 'HiveSidebar:deletedAndRefresh'],
  dispatchs: ['Workbench:add', 'Workbench:run', 'Workbench:pasteInEditor',
    'HiveSidebar:getTables',
    'WorkSidebar:showTree', 'HdfsSidebar:showTree'],
  data: {
    API_PATH: process.env.VUE_APP_MN_CONFIG_PREFIX || `${window.location.protocol}//${window.location.host}${process.env.VUE_APP_PREFIX}/api/rest_j/v1/`,
  },
  methods: {
    getAllDbsAndTables(args, cb) {
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
        cb({
          list: hiveList,
          isError: false,
        });
      }).catch((err) => {
        cb({
          list: [],
          isError: true,
        });
      });
    },
    showHive(arg, cb) {
      const Hive = Vue.extend(require('./hiveSidebar.vue').default);
      const newW = new Hive();
      cb(newW);
    },
  },
  component: () => import('./hiveSidebar.vue'),
};
