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
import axios from 'axios'
import VueRouter from "vue-router"
import routes from "./common-router"
import { apps, subRoutes } from './dynamic-apps'
import storage from '@dataspherestudio/shared/common/helper/storage'
import plugin from '@dataspherestudio/shared/common/util/plugin'

/**
 * 检查是否需要更新前端资源
 * 当前页面app.{hash}.js与服务端index.html文件里对应script文件hash不同则需要提示更新
 */
async function checkNeedShowUpdate() {
  const { data } = await axios.get(`/index.html?t=${Date.now()}`)
  let serverAppHash = data.split('<script src=js/app.')[1]
  serverAppHash = serverAppHash && serverAppHash.substring(0, 8)
  const appjs = [
    ...document.getElementsByTagName('script')
  ]
    .map(it => it.src)
    .find(it => /app\.[\da-z]{8}\.js/.test(it))
  const pageAppHash = appjs && appjs.match(/app\.([\da-z]{8})\.js/)[1]
  return Promise.resolve(pageAppHash !== serverAppHash)
}
// 解决重复点击路由跳转报错
const originalPush = VueRouter.prototype.push;
VueRouter.prototype.push = function push(location) {
  return originalPush.call(this, location).catch(err => err)
}

routes.unshift(subRoutes)

/**
 * 创建router
 */
export default function () {

  plugin.emitHook('app_router_config', routes)

  const router = new VueRouter({
    routes
  });

  router.beforeEach((to, from, next) => {
    plugin.emitHook('app_router_beforechange', { to, from, next })
    if (to.meta) {
      // 给路由添加参数，控制显示对应header
      if (to.meta.header) {
        to.query.showHeader = to.meta.header
      }
      // 路由控制不需要显示 footer
      if (to.meta.noFooter) {
        to.query.noFooter = to.meta.noFooter;
      }
      if (to.meta.admin) {
        // 如果不是管理员权限，则跳转404，防止手动修改路由跳转
        const baseInfo = storage.get("baseInfo", 'local');
        if (baseInfo && baseInfo.isAdmin) {
          next();
        } else {
          next('/404')
        }
      } else if (to.meta.publicPage) {
        // 公共页面不需要权限控制（404，500）
        next();
      } else {
        next()
      }
    }
  });

  router.afterEach((to, from) => {
    plugin.emitHook('app_router_afterchange', { to, from })
    if (to.meta) {
      document.title = to.meta.title || (apps.conf ? apps.conf.app_name || '' : '');
    }
    // 检查前端资源是否更新
    if (process.env.NODE_ENV === 'production' && apps.conf && apps.conf.showUpdateNotice !== false) {
      checkNeedShowUpdate().then(
        (show) => {
          if (show) {
            plugin.emit('show_app_update_notice')
          }
        }
      )
    }
  });

  return router
}
