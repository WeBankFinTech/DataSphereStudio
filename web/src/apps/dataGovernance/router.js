const routes = [
  {
    path: '/dataGovernance',
    name: 'dataGovernance',
    meta: {
      title: '数据治理',
      publicPage: true
    },
    component: () => import('./view/governance/index.vue'),
    children: [{
      path: 'overview',
      name: 'dataGovernance/overview',
      meta: {
        title: '数据总览',
        publicPage: true
      },
      component: () => import('./module/dataGovernance/overview.vue'),
    },{
      path: 'assets',
      name: 'dataGovernance/assets',
      meta: {
        title: '数据资产目录',
        publicPage: true
      },
      component: () => import('./module/dataGovernance/assetsIndex.vue'),
    }]
  }
]

export default routes;
