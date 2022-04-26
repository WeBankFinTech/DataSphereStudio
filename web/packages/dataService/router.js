const routes = [
  {
    path: '/dataService',
    name: 'dataService',
    meta: {
      publicPage: true
    },
    component: () => import('./view/develop/index.vue')
  },
  {
    path: '/dataManagement',
    name: 'dataManagement',
    redirect: '/dataManagement/index',
    meta: {
      publicPage: true
    },
    component: () => import('./view/management/index.vue'),
    children: [{
      path: 'index',
      name: 'dataManagement/index',
      meta: {
        title: 'API管理',
        publicPage: true
      },
      component: () => import('./module/dataManagement/apiIndex.vue'),
    },
    {
      path: 'monitor',
      name: 'dataManagement/monitor',
      meta: {
        title: 'API监控',
        publicPage: true
      },
      component: () => import('./module/dataManagement/apiMonitor.vue'),
    },
    {
      path: 'test',
      name: 'dataManagement/test',
      meta: {
        title: 'API测试',
        publicPage: true
      },
      component: () => import('./module/dataManagement/apiTest.vue'),
    },
    {
      path: 'test/:apiId',
      name: 'dataManagement/test/apiId',
      meta: {
        title: 'API测试',
        publicPage: true
      },
      component: () => import('./module/dataManagement/apiTest.vue'),
    },
    {
      path: 'call',
      name: 'dataManagement/call',
      meta: {
        title: 'API调用',
        publicPage: true
      },
      component: () => import('./module/dataManagement/apiCall.vue'),
    }]
  },
]

export default routes;
