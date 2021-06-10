const routes = [
  {
    path: '/apiservices',
    name: 'Apiservices',
    meta: {
      title: '鲁班',
      publicPage: true
    },
    component: () => import('./view/apiServices/index.vue')
  },
  {
    path: '/servicesMangement',
    name: 'ServicesMangement',
    meta: {
      title: '鲁班',
      publicPage: true
    },
    component: () => import('./view/servicesMangement/index.vue')
  },
  {
    path: '/servicesExecute',
    name: 'ServicesExecute',
    meta: {
      title: '鲁班',
      publicPage: true
    },
    component: () => import('./view/servicesExecute/index.vue')
  },
]

export default routes;