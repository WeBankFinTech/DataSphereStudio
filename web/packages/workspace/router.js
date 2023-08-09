export const apiServicesRoutes = [
  {
    path: 'newhome',
    name: 'newhome',
    meta: {
      publicPage: true,
      admin: true
    },
    component: () => import('./view/home/index.vue'),
  },
  {
    path: '/workspaceManagement',
    name: 'workspaceManagement',
    redirect: '/workspaceManagement/productsettings',
    meta: {
      publicPage: true
    },
    component: () => import('./view/management/index.vue'),
    children: [{
      path: 'productsettings',
      name: 'productsettings',
      meta: {
        publicPage: true
      },
      component: () =>
        import('./module/management/module/productsettings.vue'),
    },
    {
      path: 'usertable',
      name: 'usertable',
      meta: {
        publicPage: true
      },
      component: () =>
        import('./module/management/module/usertable.vue'),
    },
    {
      path: 'jurisdiction',
      name: 'jurisdiction',
      meta: {
        publicPage: true
      },
      component: () =>
        import('./module/management/module/jurisdiction.vue'),
    },
    {
      path: 'dataSourceAdministration',
      name: 'dataSourceAdministration',
      meta: {
        publicPage: true
      },
      component: () =>
        import('./module/management/module/dataSourceAdministration.vue'),
    },
    {
      path: 'enginelist',
      name: 'enginelist',
      meta: {
        publicPage: true,
      },
      component: () => import('./view/enginelist/index.vue'),
    },
    {
      path: 'enginekill',
      name: 'enginekill',
      meta: {
        publicPage: true,
      },
      component: () => import('./view/enginekill/index.vue'),
    },
    {
      path: 'enginekillList',
      name: 'enginekillList',
      meta: {
        publicPage: true,
      },
      component: () => import('./view/enginekill/list.vue'),
    }]
  },
  {
    path: 'permissions',
    name: 'Permissions',
    meta: {
      publicPage: true,
      admin: true
    },
    component: () => import('./view/permissions/index.vue'),
  },
  {
    path: 'managementPlatform',
    name: 'ManagementPlatform',
    meta: {
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

