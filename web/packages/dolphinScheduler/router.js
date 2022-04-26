export const subAppRoutes = {
  path: '',
  name: 'layout',
  component: () => import('./view/layout.vue'),
  redirect: '/scheduler',
  meta: {
    publicPage: true, // 权限公开
  },
  children: []
}
export default [
  {
    path: 'scheduler',
    name: 'Scheduler',
    meta: {
      title: 'My Scheduler',
      publicPage: true,
      parent: 'Project',
    },
    component: () =>
      import('./view/scheduler/index.vue'),
  }
]
