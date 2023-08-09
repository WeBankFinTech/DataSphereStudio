// 子应用layout 自定义 header footer 等需要导出subAppRoutes
// export const subAppRoutes = {
//   path: '',
//   name: 'layout',
//   component: () => import('./view/layout.vue'),
//   redirect: '/home',
//   meta: {
//     publicPage: true, // 权限公开
//   },
//   children: [

//   ]
// }

export default [
  {
    path: 'demoHome',
    name: 'DemoHome',
    meta: {
      title: 'Tests',
      publicPage: true,
    },
    component: () =>
      import('./module/index.vue'),
  },
]
