const routes = [
  {
    path: '/apiservices',
    name: 'Apiservices',
    meta: {
      publicPage: true
    },
    component: () => import('./view/apiServices/index.vue')
  },
  {
    path: '/servicesMangement',
    name: 'ServicesMangement',
    meta: {
      publicPage: true
    },
    component: () => import('./view/servicesMangement/index.vue')
  },
  {
    path: '/servicesExecute',
    name: 'ServicesExecute',
    meta: {
      publicPage: true
    },
    component: () => import('./view/servicesExecute/index.vue')
  },
  {
    path: '/ServicesSubmit',
    name: 'ServicesSubmit',
    meta: {
      publicPage: true
    },
    component: () => import('./view/servicesSubmit/index.vue')
  },
]

export default routes;
