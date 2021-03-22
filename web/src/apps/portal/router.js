const routes = [
  {
    path: '/portal',
    name: 'Portal',
    meta: {
      title: 'DataSphere Portal',
      publicPage: true
    },
    component: () => import('./view/layout.vue'),
    children: [{
      path: 'portalhome',
      name: 'portalhome',
      meta: {
        title: 'DataSphere Portal',
        publicPage: true
      },
      component: () => import('./view/home/index.vue')
    },{
      path: 'portallist',
      name: 'portallist',
      meta: {
        title: 'DataSphere Portal',
        publicPage: true,
        header: 'portal'
      },
      component: () => import('./view/list/index.vue')
    }, {
      path: 'portalsetting',
      name: 'portalsetting',
      meta: {
        title: 'DataSphere Portal',
        publicPage: true
      },
      component: () => import('./view/setting/index.vue')
    }]
  },
]

export default routes;