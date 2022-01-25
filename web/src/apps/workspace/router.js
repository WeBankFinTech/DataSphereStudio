export const apiServicesRoutes = [
  {
    path: 'newhome',
    name: 'Newhome',
    meta: {
      title: '鲁班',
      publicPage: true,
      admin: true
    },
    component: () => import('./view/home/index.vue'),
  },
  {
    path: '/workspace',
    name: 'Workspace',
    meta: {
      title: 'Workspace',
      publicPage: true,
      admin: true
    },
    component: () => import('./view/workspace/index.vue'),
  },
  {
    path: '/workspaceManagement',
    name: 'workspaceManagement',
    redirect: '/workspaceManagement/productsettings',
    meta: {
      title: '鲁班',
      publicPage: true
    },
    component: () => import('./view/management/index.vue'),
    children: [{
      path: 'productsettings',
      name: 'productsettings',
      meta: {
        title: '鲁班',
        publicPage: true
      },
      component: () =>
        import('./module/management/module/productsettings.vue'),
    },
    {
      path: 'usertable',
      name: 'usertable',
      meta: {
        title: '鲁班',
        publicPage: true
      },
      component: () =>
        import('./module/management/module/usertable.vue'),
    },
    {
      path: 'jurisdiction',
      name: 'jurisdiction',
      meta: {
        title: '鲁班',
        publicPage: true
      },
      component: () =>
        import('./module/management/module/jurisdiction.vue'),
    },
    {
      path: 'dataSourceAdministration',
      name: 'dataSourceAdministration',
      meta: {
        title: '鲁班',
        publicPage: true
      },
      component: () =>
        import('./module/management/module/dataSourceAdministration.vue'),
    }]
  },
  {
    path: 'permissions',
    name: 'Permissions',
    meta: {
      title: 'DataSphere Studio',
      publicPage: true,
      admin: true
    },
    component: () => import('./view/permissions/index.vue'),
  },
  {
    path: 'managementPlatform',
    name: 'ManagementPlatform',
    meta: {
      title: 'DataSphere Studio',
      publicPage: true,
    },
    redirect: '/managementPlatform/departManagement',
    component: () => import('./view/managementPlatform/index.vue'),
    children: [
      {
        path: 'departManagement',
        name: 'departManagement',
        meta: {
          title: 'departManagement',
          publicPage: true
        },
        component: () => import('./module/permissions/module/departManagement.vue')
      },
      {
        path: 'personManagement',
        name: 'personManagement',
        meta: {
          title: 'personManagement',
          publicPage: true
        },
        component: () => import('./module/permissions/module/personManagement.vue')
      },
      // {
      //   path: 'globalHistory',
      //   name: 'globalHistory',
      //   meta: {
      //     title: 'Global History',
      //     publicPage: true
      //   },
      //   component: () => import('../linkis/module/globalHistoryManagement/index.vue')
      // },
      // {
      //   path: 'viewHistory',
      //   name: 'viewHistory',
      //   meta: {
      //     title: 'View History',
      //     publicPage: true
      //   },
      //   component: () => import('../linkis/module/globalHistoryManagement/viewHistory.vue')
      // },
      // {
      //   path: 'resource',
      //   name: 'resource',
      //   meta: {
      //     title: 'Resource',
      //     publicPage: true
      //   },
      //   component: () => import('../linkis/module/resourceManagement/index.vue')
      // },
      // {
      //   path: 'setting',
      //   name: 'setting',
      //   meta: {
      //     title: 'Setting',
      //     publicPage: true
      //   },
      //   component: () => import('../linkis/module/setting/setting.vue')
      // },
      // {
      //   path: 'globalValiable',
      //   name: 'globalValiable',
      //   meta: {
      //     title: 'Global Valiable',
      //     publicPage: true
      //   },
      //   component: () => import('../linkis/module/globalValiable/index.vue')
      // },
      // {
      //   path: 'ECM',
      //   name: 'ECM',
      //   meta: {
      //     title: 'ECM',
      //     publicPage: true
      //   },
      //   component: () => import('../linkis/module/ECM/index.vue')
      // },
      // {
      //   path: 'EngineConnList',
      //   name: 'EngineConnList',
      //   meta: {
      //     title: 'Engine ConnList',
      //     publicPage: true
      //   },
      //   component: () => import('../linkis/module/ECM/engineConn.vue')
      // },
      // {
      //   path: 'FAQ',
      //   name: 'FAQ',
      //   meta: {
      //     title: 'FAQ',
      //     publicPage: true
      //   },
      //   component: () => import('../linkis/module/FAQ/index.vue')
      // },
      {
        path: 'accessComponents',
        name: 'accessComponents',
        meta: {
          title: 'Access Components',
          publicPage: true
        },
        component: () => import('./module/managementPlatform/component/accessComponent/index.vue')
      },
      {
        path: 'guide',
        name: 'guide',
        meta: {
          title: 'Guide',
          publicPage: true
        },
        component: () => import('./module/managementPlatform/component/guide/index.vue')
      },
      {
        path: 'library',
        name: 'library',
        meta: {
          title: 'Library',
          publicPage: true
        },
        component: () => import('./module/managementPlatform/component/library/index.vue')
      }
    ]
  }
];

const routes = apiServicesRoutes.concat([{
  path: '/workspaceHome',
  name: 'workspaceHome',
  meta: {
    title: 'workspaceHome',
    publicPage: true,
  },
  component: () => import('./view/workspaceHome/index.vue'),
}])

export default routes

