const routes = [
  {
    path: '/dataService',
    name: 'dataService',
    meta: {
      title: '鲁班',
      publicPage: true
    },
    component: () => import('./view/develop/index.vue')
  },
  {
    path: '/dataManagement',
    name: 'dataManagement',
    meta: {
      title: '鲁班',
      publicPage: true
    },
    component: () => import('./view/management/index.vue')
  },
]

export default routes;