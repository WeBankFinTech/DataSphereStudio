const routes = [
  {
    path: '/dataAssetManagement',
    name: 'DataAssetManagement',
    meta: {
      title: 'DataSphere Studio',
      publicPage: true
    },
    component: () => import('./view/DataAssetManagement/index.vue')
  }
]

export default routes;
