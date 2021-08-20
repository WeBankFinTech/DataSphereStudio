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
        publicPage: true,
        admin: true
      },
      component: () =>
        import('./module/management/module/usertable.vue'),
    },
    {
      path: 'jurisdiction',
      name: 'jurisdiction',
      meta: {
        title: '鲁班',
        publicPage: true,
        admin: true
      },
      component: () =>
        import('./module/management/module/jurisdiction.vue'),
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