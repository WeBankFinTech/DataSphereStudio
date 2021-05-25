export const subAppRoutes = {
  path: '',
  name: 'layout',
  component: () => import('./view/layout.vue'),
  redirect: '/newHome',
  meta: {
    title: '鲁班',
    publicPage: true, // 权限公开
  },
  children: [{
    path: 'commonIframe',
    name: 'commonIframe',
    meta: {
      title: 'DSS Component',
      publicPage: true,
    },
    component: () =>
      import('./view/commonIframe/index.vue'),
  }]
}

export default [
  // 日志查看
  {
    path: '/log',
    name: 'log',
    meta: {
      title: 'Log',
      publicPage: true,
    },
    component: () =>
      import('./view/logPage/index.vue')
  },
  {
    path: '/login',
    name: 'login',
    meta: {
      title: 'Login',
      publicPage: true,
    },
    component: () =>
      import('./view/login/index.vue'),
  },
  // 公用页面，不受权限控制
  {
    path: '/500',
    name: 'serverErrorPage',
    meta: {
      title: '服务器错误',
      publicPage: true,
    },
    component: () =>
      import('./view/500.vue'),
  },
  {
    path: '/404',
    name: 'pageNotFound',
    meta: {
      title: '404',
      publicPage: true,
    },
    component: () =>
      import('./view/404.vue'),
  },
  {
    path: '/403',
    name: 'pageForbidden',
    meta: {
      title: '403',
      publicPage: true,
    },
    component: () =>
      import('./view/403.vue'),
  },
  // svg可用图标预览
  {
    path: '/icon',
    name: 'icon',
    meta: {
      title: 'icon',
      publicPage: true,
    },
    component: () =>
      import('./view/icon.vue'),
  },
  {
    path: '*',
    meta: {
      title: '鲁班',
      publicPage: true,
    },
    component: () =>
      import('./view/404.vue'),
  }
]