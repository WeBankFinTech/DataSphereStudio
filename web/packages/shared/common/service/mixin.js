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

import storage from '@dataspherestudio/shared/common/helper/storage';
import util from '@dataspherestudio/shared/common/util';
import SUPPORTED_LANG_MODES from '@dataspherestudio/shared/common/config/scriptis';

export default {
  data: function () {
    return {
      SUPPORTED_LANG_MODES,
    };
  },
  filters: util.filters,
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
  created: function () {},
  mounted: function () {},
  beforeDestroy: function () {},
  destroyed: function () {},
  methods: {
    getSupportModes() {
      return this.SUPPORTED_LANG_MODES;
    },
    getAppConnItem(name, id) {
      const applications = storage.get('applications') || []
      let applicationItem = {}
      applications.forEach(item => {
        if (item.appconns) {
          const findApp = item.appconns.find(app => app.name == name || app.id == id)
          if (findApp) applicationItem = findApp
        }
      })
      return applicationItem
    },
    /**
     * 跳转应用模块支持第三方应用iframe打开
     * @param {*} info
     * @param {*} query
     */
    gotoCommonFunc({app , index}, query = {}) {
      let url = app.homepageUri
      const openFn = (info) => {
        if (info.external) {
          if (info.ifIframe) {
            this.$router.push({
              path: info.isMicroApp ? `/microApp/${info.name}` : `/commonIframe/${info.name}`, //isMicroApp判断是否微应用
              query: {
                ...query,
                url,
                type: info.name,
                menuApplicationId: info.id
              }
            })
          } else {
            let newUrl = new URLSearchParams();
            Object.keys(query).forEach(key=>{
              newUrl.set(key, query[key]);
            })
            window.open(util.replaceHolder(url), '_blank')
          }
        } else { // 内部路由地址
          this.$router.push({path: url, query: {
            ...query,
            menuApplicationId: info.id
          }});
        }
      }
      if (url) {
        openFn(app)
      } else {
        const item = this.getAppConnItem(app.name, app.id)
        if (item.appInstances && item.appInstances[index]) {
          url = item.appInstances[index].homepageUri
          openFn(item)
        }
      }
    },
    getUserName() {
      return  storage.get("baseInfo", 'local') ? storage.get("baseInfo", 'local').username : null;
    },
    getIsAdmin() {
      return  storage.get("baseInfo", 'local')  ? storage.get("baseInfo", 'local').isAdmin : false;
    },
    // 获取当前工作流的环境
    getCurrentDsslabels() {
      return this.$route.query.label || (storage.get("currentDssLabels") ? storage.get("currentDssLabels") : null);
    },
    getCurrentWorkspaceName() {
      const workspaceData = storage.get("currentWorkspace");
      if (this.$route.query.workspaceName){
        return this.$route.query.workspaceName
      }
      return workspaceData ? workspaceData.name : ''
    },
    // 获取工作空间Id
    getCurrentWorkspaceId() {
      const workspaceData = storage.get("currentWorkspace");
      if (this.$route.query.workspaceId){
        return this.$route.query.workspaceId
      }
      return workspaceData ? workspaceData.id : ''
    },
    getCurrentWorkspace(key) {
      let cache = storage.get('currentWorkspace')
      if (key === 'id') {
        return this.$route.query.workspaceId || cache && cache.id
      }
      if(key === 'name') {
        return this.$route.query.workspaceName || cache && cache.name
      }
      return storage.get('currentWorkspace');
    },
    getHandbookUrl() {
      let baseInfo = storage.get('baseInfo', 'local') || {}
      return baseInfo.dss ? baseInfo.dss.handbookUrl || window.$APP_CONF.handbook : window.$APP_CONF.handbook
    }
  },
};
