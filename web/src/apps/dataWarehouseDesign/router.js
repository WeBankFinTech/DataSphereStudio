const routes = [
  {
    path: '/datawarehousedesign',
    name: 'DataWarehouseDesign',
    meta: {
      title: 'DataSphere Studio',
      publicPage: true
    },
    component: () => import('./view/DataWarehouseDesign/index.vue')
  }
]

export default routes;
