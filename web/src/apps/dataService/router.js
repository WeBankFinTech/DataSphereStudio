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
    path: '/dataMangement',
    name: 'dataMangement',
    meta: {
      title: '鲁班',
      publicPage: true
    },
    component: () => import('./view/mangement/index.vue')
  },
]

export default routes;