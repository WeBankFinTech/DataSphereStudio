const routes = [
  {
    path: '/dataGovernance',
    name: 'dataGovernance',
    meta: {
      title: '数据治理',
      publicPage: true
    },
    component: () => import('./view/governance/index.vue'),
    redirect: '/dataGovernance/overview',
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
      redirect: '/dataGovernance/assets/search',
      children: [{
        path: 'search',
        name: 'dataGovernance/assets/search',
        meta: {
          title: '数据资产目录查询',
          publicPage: true
        },
        component: () => import('./view/assetsSearch/index.vue'),
      },{
        path: 'info/:guid',
        name: 'dataGovernance/assets/info',
        meta: {
          title: '数据资产详情',
          publicPage: true
        },
        component: () => import('./view/assetsInfo/index.vue'),
      }]
    },{
      name: 'dataGovernance/subjectDomain',
      path: 'subjectDomain',
      component: () => import('./view/subjectDomain/index.vue'),
      meta: {
        title: '主题域配置',
        publicPage: true,
        icon: 'ios-paper'
      }
    },{
      name: 'dataGovernance/layered',
      path: 'layered',
      component: () => import('./view/layered/index.vue'),
      meta: {
        title: '分层配置',
        publicPage: true,
        icon: 'ios-paper'
      }
    },{
      name: 'dataGovernance/modifier',
      path: 'modifier',
      component: () => import('./view/modifier/index.vue'),
      meta: {
        title: '修饰词管理',
        publicPage: true,
        icon: 'ios-paper'
      }
    },
    {
      name: 'dataGovernance/statPeriod',
      path: 'statPeriod',
      component: () => import('./view/statPeriod/index.vue'),
      meta: {
        title: '统计周期管理',
        publicPage: true,
        icon: 'ios-paper'
      }
    }]
  }
]

export default routes;
