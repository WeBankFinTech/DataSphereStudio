const routes = [
  {
    path: '/workspace',
    name: 'workspace',
    meta: {
      title: 'Workspace',
      publicPage: true,
    },
    component: () => import('./view/layout.vue'),
    redirect: '/workspace/resourceTmlManage',
    children: [
      {
        path: 'resourceTmlManage',
        name: 'resourceTmlManage',
        meta: {
          title: 'Resource Template',
          publicPage: true,
        },
        component: () => import('./module/resourceTmlManage/layout.vue'),
        children: [
          {
            path: '',
            name: 'resourceTmlManage',
            meta: {
              title: 'Resource Template',
              publicPage: true,
            },
            component: () => import('./module/resourceTmlManage/index.vue'),
          },
          {
            path: 'applicationRule',
            name: 'applicationRule',
            meta: {
              title: 'Application Rule',
              publicPage: true,
            },
            component: () =>
              import('./module/resourceTmlManage/applicationRule/index.vue'),
          },
        ],
      },
      {
        path: 'announcement',
        name: 'announcement',
        meta: {
          title: 'Announcement',
          publicPage: true,
        },
        component: () => import('./module/announcement/index.vue'),
      },
      {
        path: 'workflow',
        name: 'workflow',
        meta: {
          title: 'Workflow',
          publicPage: true,
        },
        component: () => import('./module/workFlow/index.vue'),
      },
    ],
  },
];

export default routes;
