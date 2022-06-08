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
export const subAppRoutes = {
  path: '',
  name: 'layout',
  component: () => import('./view/layout.vue'),
  redirect: '/home',
  meta: {
    publicPage: true, // 权限公开
  },
  children: [
    {
      path: '/commonIframe/:appName',
      name: 'commonIframe',
      meta: {
        publicPage: true,
      },
      component: () =>
        import('./view/iframe/index.vue'),
    }
  ]
}

export default [
  {
    path: 'home',
    name: 'Home',
    meta: {
      title: 'Scriptis',
      keepAlive: false,
      publicPage: true, // 权限公开
    },
    component: () =>
      import('./view/home/index.vue'),
  },
  {
    path: 'results',
    name: 'results',
    meta: {
      title: 'Scriptis result',
      keepAlive: false,
      publicPage: true, // 权限公开
    },
    component: () =>
      import('./view/result/index.vue'),
  },
  {
    path: 'DownloadAudit',
    name: 'DownloadAudit',
    meta: {
      title: '下载审计',
      publicPage: true
    },
    component: () =>
      import('./view/audit/index.vue'),
  }
]
