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

import { cloneDeep } from 'lodash';
import { db } from '@js/service/db/index.js';
import tab from '@js/service/db/tab.js';
import log from '@js/service/db/log.js';
import history from '@js/service/db/history.js';
import result from '@js/service/db/result.js';
import globalcache from '@js/service/db/globalcache.js';
import progress from '@js/service/db/progress.js';
import node from '@js/service/db/node.js';
import project from '@js/service/db/project.js';
export default {
  name: 'IndexedDB',
  events: [],
  methods: {
    // tab open
    async getLog({ tabId, cb }) {
      const logs = await log.get(tabId);
      if (logs && logs[0]) {
        const tabLog = logs[0].content;
        cb(tabLog);
      }
    },
    async appendLog({ tabId, rst }) {
      let items = await log.get(tabId);
      if (items && items[0]) {
        log.update(tabId, {
          content: rst,
        });
      } else {
        log.add({
          tabId,
          content: rst,
        });
      }
    },
    clearLog(tabId) {
      log.remove(tabId);
    },
    async changeLogKey({ oldKey, newKey }) {
      await log.modifyPrimaryKey(oldKey, newKey);
    },
    updateHistory({ tabId, ...changes }) {
      history.update(tabId, changes);
    },
    appendHistory(item) {
      history.add(item);
    },
    async getHistory({ tabId, cb }) {
      const items = await history.get(tabId);
      const historyList = items && items[0];
      if (historyList) {
        cb(historyList);
      }
    },
    clearHistory(tabId) {
      history.remove(tabId);
    },
    async changeHistoryKey({ oldKey, newKey }) {
      await history.modifyPrimaryKey(oldKey, newKey);
    },
    clearResult(tabId) {
      result.remove(tabId);
    },
    updateResult({ tabId, ...changes }) {
      result.update(tabId, changes);
    },
    appendResult(item) {
      result.add(item);
    },
    async getResult({ tabId, cb }) {
      const items = await result.get(tabId);
      const ResultList = items && items[0];
      if (ResultList) {
        cb(ResultList);
      } else {
        cb();
      }
    },
    async changResultKey({ oldKey, newKey }) {
      await result.modifyPrimaryKey(oldKey, newKey);
    },
    async getTabs(cb) {
      let tabs = await tab.get() || [];
      tabs.sort((a, b) => {
        return a.actived - b.actived;
      });
      if (tabs && cb) {
        cb(tabs);
      }
    },
    async recordTab(data, cb) {
      const id = data.id;
      const items = await tab.get() || [];
      let item = null;
      items.forEach(async (work) => {
        if (work.id === id) {
          item = work;
        }
      });
      let newData = cloneDeep(data);
      if (item) {
        delete newData.id;
        await tab.update(id, newData);
      } else {
        await tab.add(newData);
      }
      toggleActive(id);
      if (typeof cb == 'function') {
        cb();
      }
    },
    toggleTab(tabId) {
      toggleActive(tabId);
    },
    removeTab(tabId) {
      tab.remove(tabId);
    },
    async changeTabKey({ oldKey, newKey }) {
      await tab.modifyPrimaryKey(oldKey, newKey);
    },
    async setGlobalCache(item, cb = function() {}) {
      await globalcache.setCache(item);
      cb();
    },
    async getGlobalCache(arg, cb = function() {}) {
      const items = await globalcache.getCache(arg.id);
      cb(items);
    },
    updateGlobalCache(args) {
      globalcache.updateCache(args);
    },
    removeGlobalCache(args) {
      globalcache.removeCache(args);
    },
    async getProgress({ tabId, cb }) {
      const items = await progress.get(tabId);
      if (items && items[0]) {
        const info = items[0];
        cb(info);
      }
    },
    async updateProgress({ tabId, rst }) {
      let items = await progress.get(tabId);
      if (items && items[0]) {
        progress.update(tabId, {
          content: rst,
        });
      } else {
        progress.add({
          tabId,
          content: rst,
        });
      }
    },
    clearProgress(tabId) {
      progress.remove(tabId);
    },
    async changProgressKey({ oldKey, newKey }) {
      await progress.modifyPrimaryKey(oldKey, newKey);
    },

    deleteDb() {
      db.db.delete();
    },

    async getNodeCache({nodeId, key, cb}) {
      const result = await node.getNodeCache({nodeId, key});
      cb(result);
    },

    addNodeCache({nodeId, value}) {
      node.addNodeCache({nodeId, value});
    },

    updateNodeCache({nodeId, key, value}) {
      node.updateNodeCache({nodeId, key, value});
    },

    removeNodeCache({nodeId, key}) {
      node.removeNodeCache({nodeId, key});
    },

    clearNodeCache() {
      db.db.node.clear();
    },

    async getProjectCache({projectID, key, cb}) {
      const result = await project.getProjectCache({projectID, key});
      cb(result);
    },

    addProjectCache({projectID, value}) {
      project.addProjectCache({projectID, value});
    },

    updateProjectCache({projectID, key, value, isDeep}) {
      project.updateProjectCache({projectID, key, value, isDeep});
    },

    removeProjectCache({projectID, key}) {
      project.removeProjectCache({projectID, key});
    },

    clearProjectCache() {
      db.db.project.clear();
    },
  },
};

/**
 * @param {*} tabId
 * @return {promise}
 */
async function toggleActive(tabId = '') {
  let allTabs = await tab.get();
  if (!allTabs) return;
  allTabs = allTabs.filter((tab) => !!tab);
  allTabs.forEach(async (t) => {
    const id = t.id;
    let actived = false;
    if (id === tabId) {
      actived = true;
    }
    await tab.update(id, {
      actived,
    });
  });
}
