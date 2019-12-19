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

import storage from '@/js/helper/storage';
import api from '@/js/service/api';

export default {
  data: function() {
    return {
      SUPPORTED_LANG_MODES: Object.freeze([
        { rule: /[表详情]|(Table\sdetails)/, executable: false, isCanBeOpen: true },
        { rule: /[建表向导]|(Table\screation\sguide)/, executable: false, isCanBeOpen: true },
        { rule: /\.(bi)$/i, executable: false, logo: 'fi-bi', isCanBeNew: false, isCanBeOpen: true },
        { rule: /\.(sql)$/i, lang: 'hql', executable: true, application: 'spark', runType: 'sql', ext: '.sql', scriptType: 'hive', abbr: 'sql', logo: 'fi-spark', isCanBeNew: true, label: 'Sql', isCanBeOpen: true, flowType: 'sparksql' },
        { rule: /\.(hql)$/i, lang: 'hql', executable: true, application: 'hive', runType: 'hql', ext: '.hql', scriptType: 'hql', abbr: 'hql', logo: 'fi-hive', isCanBeNew: true, label: 'Hive', isCanBeOpen: true, flowType: 'hql' },
        { rule: /\.(out)$/i, lang: 'hql', executable: true, application: 'pipeline', runType: 'pipeline', ext: '.out', scriptType: 'storage', abbr: 'stor', logo: 'fi-storage', isCanBeNew: true, label: 'Storage', isCanBeOpen: true },
        { rule: /\.scala$/i, lang: 'java', executable: true, application: 'spark', runType: 'scala', ext: '.scala', scriptType: 'scala', abbr: 'scala', logo: 'fi-scala', isCanBeNew: true, label: 'Scala', isCanBeOpen: true, flowType: 'scala' },
        { rule: /\.scala$/i, lang: 'java', executable: true, application: 'spark', runType: 'function.mdq', ext: '.scala', scriptType: 'scala', abbr: 'scala', logo: 'fi-scala', isCanBeNew: false, label: 'Scala', isCanBeOpen: true },
        { rule: /\.jdbc$/i, lang: 'hql', executable: true, application: 'jdbc', runType: 'jdbc', ext: '.jdbc', scriptType: 'jdbc', abbr: 'jdbc', logo: 'fi-jdbc', isCanBeNew: true, label: 'JDBC', isCanBeOpen: true },
        { rule: /\.python$/i, lang: 'python', executable: true, application: 'python', runType: 'python', ext: '.python', scriptType: 'python', abbr: 'py', logo: 'fi-python', isCanBeNew: true, label: 'Python', isCanBeOpen: true, flowType: 'python' },
        { rule: /\.py$/i, lang: 'python', executable: true, application: 'spark', runType: 'python', ext: '.py', scriptType: 'pythonSpark', abbr: 'py', logo: 'fi-spark-python', isCanBeNew: true, label: 'PythonSpark', isCanBeOpen: true, flowType: 'pyspark' },
        { rule: /\.r$/i, lang: 'r', executable: true, application: 'spark', runType: 'r', ext: '.r', scriptType: 'r', abbr: 'r', logo: 'fi-r', isCanBeNew: true, label: 'R', isCanBeOpen: true },
        { rule: /\.sh$/i, lang: 'sh', executable: true, application: 'shell', runType: 'shell', ext: '.sh', scriptType: 'shell', abbr: 'shell', logo: 'fi-r', isCanBeNew: false, label: 'Shell', isCanBeOpen: true, flowType: 'shell' },
        { rule: /\.qmlsql$/i, lang: 'hql', executable: false, application: 'spark', runType: 'sql', ext: '.qmlsql', scriptType: 'qmlsql', abbr: 'qmlsql', logo: 'fi-spark', isCanBeNew: false, label: 'QMLSQL', isCanBeOpen: true },
        { rule: /\.qmlpy$/i, lang: 'python', executable: false, application: 'spark', runType: 'python', ext: '.qmlpy', scriptType: 'qmlpy', abbr: 'qmlpy', logo: 'fi-python', isCanBeNew: false, label: 'QMLPy', isCanBeOpen: true },
        { rule: /\.txt$/i, lang: 'text', executable: false, application: null, runType: null, ext: '.txt', scriptType: 'txt', abbr: '', logo: 'fi-txt', isCanBeNew: false, isCanBeOpen: true },
        { rule: /\.log$/i, lang: 'text', executable: false, application: null, runType: null, ext: '.log', scriptType: 'txt', abbr: '', logo: 'fi-log', isCanBeNew: false, isCanBeOpen: true },
        { rule: /\.xls$/i, logo: 'fi-xls', isCanBeNew: false, isCanBeOpen: false },
        { rule: /\.xlsx$/i, logo: 'fi-xlsx', isCanBeNew: false, isCanBeOpen: false },
        { rule: /\.csv$/i, logo: 'fi-csv', isCanBeNew: false, isCanBeOpen: false },
        { rule: /\.jar$/i, logo: 'fi-jar', isCanBeNew: false, isCanBeOpen: false },
      ]),
    };
  },
  beforeRouteLeave(to, from, next) {
    if (typeof this.beforeLeaveHook === 'function') {
      let hookRes = this.beforeLeaveHook();
      if (hookRes === false) {
        next(false);
      } else if (hookRes && hookRes.then) {
        hookRes.then((flag) => {
          next(flag);
        });
      } else {
        next(true);
      }
    } else {
      next(true);
    }
  },
  created: function() {
  },
  mounted: function() {},
  beforeDestroy: function() {},
  destroyed: function() {},
  methods: {
    getUserName() {
      const userInfo = storage.get('userInfo', 'SESSION');
      return userInfo && userInfo.basic.username;
    },
    getVsBiUrl() {
      const baseInfo = storage.get('baseInfo');
      const vsBi = baseInfo.applications.find((item) => item.name === 'visualis') || {};
      return vsBi.url;
    },
    getQualitisUrl() {
      const baseInfo = storage.get('baseInfo');
      const info = baseInfo.applications.find((item) => item.name === 'qualitis') || {};
      return info.projectUrl;
    },
    getProjectJson() {
      const baseInfo = storage.get('baseInfo');
      const vsBi = baseInfo.applications.find((item) => item.name === 'visualis') || {};
      // projectMsg.projectJson  这个变为了applications 里面的enhancejson
      const projectJson = vsBi.enhanceJson;
      return projectJson;
    },
    getDataReportUrl() {
      const projectJson = this.getProjectJson();
      return projectJson ? JSON.parse(projectJson).dailyReport : '';
    },
    getWaterMask() {
      const projectJson = this.getProjectJson();
      return projectJson ? JSON.parse(projectJson).watermark : true;
    },
    getRsDownload() {
      const projectJson = this.getProjectJson();
      return projectJson ? JSON.parse(projectJson).rsDownload : true;
    },
    getFAQUrl() {
      const baseInfo = storage.get('baseInfo');
      const url = baseInfo.DWSParams.faq;
      return url;
    },
    getSupportModes() {
      return this.SUPPORTED_LANG_MODES;
    },
    getLogManager() {
      const baseInfo = storage.get('baseInfo');
      if (!baseInfo.userInfo.role || !baseInfo.userInfo.role[0]) {
        return false;
      }
      const findRole = baseInfo.userInfo.role.find((role) => role.name === 'logAdmin');
      return !!findRole;
    },
    getProjectList() {
      const baseInfo = storage.get('baseInfo');
      return baseInfo.applications;
    },
    getCommonProjectId(type, query) {
      return api.fetch(`/dss/getAppjointProjectIDByApplicationName`, {
        projectID: query.projectID,
        applicationName: type
      }, 'get').then((res) => {
        window.console.log(res)
        localStorage.setItem('appJointProjectId', res.appJointProjectID);
      })
    },
    async gotoCommonIframe(type, query = {}) {
      const baseInfo = storage.get('baseInfo');
      const info = baseInfo.applications.find((item) => item.name === type) || {};
      // 根据是否有projectid来确定是走首页还是工程页
      let url = '';
      if (query.projectID) {
        await this.getCommonProjectId(type, query);
        url = info.projectUrl
      } else {
        localStorage.removeItem('appJointProjectId')
        url = info.homepageUrl
      }
      window.console.log(url, 'url')
      // 如果没有提示用户功能暂未开发
      if (Object.keys(info).length === 0) {
        this.$Message.warning(this.$t('message.constants.warning.comingSoon'));
      } else {
        if (!info.ifIframe) {
          this.$router.push({path: url, query});
        } else {
          this.$router.push({name: 'commonIframe', query: {
            ...query,
            url,
            type
          }})
        }
      }
    }
  },
};
