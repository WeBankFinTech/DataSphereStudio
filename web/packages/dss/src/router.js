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

import VueRouter from "vue-router";
import routes from "./common-router"
import { apps, subRoutes } from './dynamic-apps'
import storage from '@dataspherestudio/shared/common/helper/storage';

// 解决重复点击路由跳转报错
const originalPush = VueRouter.prototype.push;
VueRouter.prototype.push = function push(location) {
  return originalPush.call(this, location).catch(err => err)
}

routes.unshift(subRoutes)

const router = new VueRouter({
  routes
});

router.beforeEach((to, from, next) => {
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

router.afterEach((to) => {
  if (to.meta) {
    document.title = to.meta.title || (apps.conf ? apps.conf.app_name || '' : '');
  }
});

export default router;
