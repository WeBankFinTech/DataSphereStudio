const routes = [
  {
    path: '/datamodelcenter',
    name: 'DataModelCenter',
    meta: {
      title: 'DataSphere Studio',
      publicPage: true
    },
    component: () => import('./view/DataModelCenter/index.vue')
  }
]

export default routes;
