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

import VueRouter from 'vue-router';
import Layout from '../view/layout.vue';
import storage from '@/js/helper/storage';
import { Modal } from 'iview';

// 解决重复点击路由跳转报错
const originalPush = VueRouter.prototype.push
VueRouter.prototype.push = function push(location) {
  return originalPush.call(this, location).catch(err => err)
}

const router = new VueRouter({
  routes: [
    {
      path: '',
      name: 'layout',
      redirect: '/newhome',
      component: Layout,
      meta: {
        title: 'DataSphere Studio',
        publicPage: true, // 权限公开
      },
      children: [
        {
          path: 'newhome',
          name: 'Newhome',
          meta: {
            title: 'DataSphere Studio',
            publicPage: true,
          },
          component: () => import('../view/newhome/index.vue'),
        },
        {
          name: 'API',
          path: 'api',
          component: () => import('../module/dataAPI/index.vue'),
          meta: {
            title: 'API',
            publicPage: true
          }
        },
        {
          name: 'WorkOrder',
          path: 'workOrder',
          component: () => import('../module/workOrder/index.vue'),
          meta: {
            title: 'WorkOrder',
            publicPage: true
          }
        },
        {
          name: 'NewAPI',
          path: 'newApi',
          component: () => import('../module/dataAPI/ApiForm.vue'),
          meta: {
            title: 'NewAPI',
            publicPage: true
          }
        },
        {
          path: 'home',
          name: 'Home',
          meta: {
            title: 'Scriptis',
            keepAlive: true, // 缓存导致页面有多个编辑器，广播事件会触发报错
            publicPage: true, // 权限公开
          },
          component: () => import('../view/home/index.vue'),
        },
        {
          path: 'project',
          name: 'Project',
          meta: {
            title: 'DataSphere Studio',
            publicPage: true,
          },
          component: () => import('../view/project/index.vue'),
        },
        {
          path: 'workflow',
          name: 'Workflow',
          meta: {
            title: 'My Workflow',
            publicPage: true,
            parent: 'Project',
          },
          component: () => import('../view/workflow/index.vue'),
        },
        {
          path: 'workspace',
          name: 'workspace',
          meta: {
            title: 'Workspace',
            publicPage: true,
          },
          component: () => import('../view/workspace/index.vue'),
        },
        {
          path: 'workspace1',
          name: 'workspace1',
          meta: {
            title: 'Workspace1',
            publicPage: true,
          },
          component: () => import('../view/newhome/module/workspace/index.vue'),
        },
        {
          path: 'commonIframe',
          name: 'commonIframe',
          meta: {
            title: 'DSS Component',
            publicPage: true,
          },
          component: () => import('../view/commonIframe/index.vue'),
        },
        {
          path: 'console',
          name: 'Console',
          meta: {
            title: 'linkis console',
            publicPage: true,
          },
          component: () => import('../view/console/index.vue'),
          children: [{
            name: 'globalHistory',
            path: 'globalHistory',
            component: () => import('../module/globalHistory/index.vue'),
            meta: {
              title: 'Global History',
              publicPage: true,
            },
          }, {
            name: 'resource',
            path: 'resource',
            component: () => import('../module/resource/resource.vue'),
            meta: {
              title: 'resource',
              publicPage: true,
            },
          }, {
            name: 'setting',
            path: 'setting',
            component: () => import('../module/setting/setting.vue'),
            meta: {
              title: 'setting',
              publicPage: true,
            },
          }, {
            name: 'globalValiable',
            path: 'globalValiable',
            component: () => import('../module/globalValiable/index.vue'),
            meta: {
              title: 'Global Valiable',
              publicPage: true,
            },
          }, {
            name: 'FAQ',
            path: 'FAQ',
            component: () => import('../module/FAQ/index.vue'),
            meta: {
              title: 'FAQ',
              publicPage: true,
            },
          },
          ],
        },
      ],
    },
    {
      path: '/login',
      name: 'login',
      meta: {
        title: 'Login',
        publicPage: true,
      },
      component: () => import('../view/login/index.vue'),
    },
    // 公用页面，不受权限控制
    {
      path: '/500',
      name: 'serverErrorPage',
      meta: {
        title: '服务器错误',
        publicPage: true,
      },
      component: () => import('../view/500.vue'),
    },
    {
      path: '/404',
      name: 'pageNotFound',
      meta: {
        title: '404',
        publicPage: true,
      },
      component: () => import('../view/404.vue'),
    },
    {
      path: '/403',
      name: 'pageForbidden',
      meta: {
        title: '403',
        publicPage: true,
      },
      component: () => import('../view/403.vue'),
    },
    {
      path: '*',
      meta: {
        title: 'DataSphere Studio',
        publicPage: true,
      },
      component: () => import('../view/404.vue'),
    },
  ],
});
router.beforeEach((to, from, next) => {
  const userInfo = storage.get('userInfo');

  if (to.path === '/newhome') {
    next()
  } else {
    if (userInfo && userInfo.basic.status === 0) {
      Modal.confirm({
        title: '开通资源',
        content: '<p>尊敬的用户，使用本功能需要计算和存储资源，您可以去申请开通资源</p>',
        okText: '去开通',
        cancelText: '再看看案例和入门',
        onOk: () => {
          window.open(process.env.VUE_APP_CTYUN_SUBSCRIBE);
        },
        onCancel: () => {
          console.log('Clicked cancel');
        }
      });
    } else {
      next()
    }
  }
  // 直接进入某个工程
  // const query = from.query;
  // if (query && query.projectID && query.projectTaxonomyID && query.projectVersionID && (to.name === 'Kanban' || to.name === 'Home' || to.name === 'Workflow')) {
  //   next({
  //     path: to.path,
  //     query,
  //   })
  //   return;
  // }
  // if (to.name === 'Workflow' && !query.projectID && from.name !== 'Project') {
  //   next({
  //     path: '/project',
  //   })
  //   return;
  // }
});
router.afterEach((to) => {
  if (to.meta) {
    document.title = to.meta.title || 'DataSphere Studio';
  }
});

export default router
;
