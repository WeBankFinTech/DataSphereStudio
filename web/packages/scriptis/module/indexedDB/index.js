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

import tab from '@/scriptis/service/db/tab.js';
import log from '@/scriptis/service/db/log.js';
import history from '@/scriptis/service/db/history.js';
import result from '@/scriptis/service/db/result.js';
import globalcache from '@dataspherestudio/shared/common/service/db/globalcache.js';
import progress from '@/scriptis/service/db/progress.js';
import tree from '@/scriptis/service/db/tree.js';
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
      if (tabs && cb) {
        cb(tabs);
      }
    },
    /**
     * 用于缓存打开的脚本tab,有则更新，没有就新增
     * @param {*} data 缓存的数据
     * @param {*} cb 回调函数
     */
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
      toggleActive(id, newData.userName);
      if (typeof cb == 'function') {
        cb();
      }
    },
    /**
     * 更新当前用户下的脚本打开情况
     * @param {*} tabId
     * @param {*} userName
     */
    toggleTab(tabId, userName) {
      toggleActive(tabId, userName);
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



    // 新增树结构的indexDB操作
    clearTree(tabId) {// 清除
      tree.remove(tabId);
    },
    updateTree({ tabId, ...changes }) { // 更新
      tree.update(tabId, changes);
    },
    appendTree(item) {
      tree.add(item);
    },
    async getTree({ name, cb }) { // 获取
      const items = await tree.get(name);
      const data = items && items[0]
      cb(data);
    },
    async changTreeKey({ oldKey, newKey }) { // 改变key值
      await tree.modifyPrimaryKey(oldKey, newKey);
    },
    clearTab(tabId) {
      log.remove(tabId);
      history.remove(tabId);
      result.remove(tabId);
      progress.remove(tabId);
      tab.remove(tabId);
    }
  },
};

/**
 * 改变不同用户下打开脚本时的当前状态
 * @param {*} tabId
 * @return {promise}
 */
async function toggleActive(tabId = '', userName) {
  let allTabs = await tab.get();
  if (!allTabs) return;
  allTabs = allTabs.filter((tab) => !!tab);
  allTabs.forEach(async (t) => {
    const id = t.id;
    const owner = t.userName;
    let actived = false;
    if (userName === owner) {
      actived = id === tabId;
      await tab.update(id, {
        actived,
      });
    }
  });
}
