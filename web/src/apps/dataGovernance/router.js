// 如果子应用无需自己的layout，subAppRoutes可去掉
export const subAppRoutes = {
  path: '/',
  name: 'layout',
  component: () => import('@/dss/view/layout.vue'),
  redirect: '/dataGovernance',
  meta: {
    title: 'DataGovernance',
    publicPage: true // 权限公开
  },
  children: []
}

const routes = [
  {
    name: 'dataGovernance',
    path: '/dataGovernance',
    redirect: '/dataGovernance/overview',
    component: () => import('./view/governance/index.vue'),
    meta: {
      title: '数据治理',
      publicPage: true
    },
    children: [
      {
        name: 'overview',
        path: '/dataGovernance/overview',
        component: () => import('./module/dataGovernance/overview.vue'),
        meta: {
          title: '数据总览',
          publicPage: true,
          icon: 'ios-paper'
        }
      },
      {
        name: 'assets',
        path: '/dataGovernance/assets',
        redirect: '/dataGovernance/assets/search',
        component: () => import('./module/dataGovernance/assetsIndex.vue'),
        meta: {
          title: '数据资产目录',
          publicPage: true,
          cover: 'assetsSearch'
        },
        children: [
          {
            name: 'assetsSearch',
            path: '/dataGovernance/assets/search',
            component: () => import('./view/assetsSearch/index.vue'),
            meta: {
              title: '数据资产目录',
              publicPage: true,
              icon: 'ios-paper'
            }
          },
          {
            name: 'assetsInfo',
            path: 'dataGovernance/assets/info',
            component: () => import('./view/assetsInfo/index.vue'),
            meta: {
              title: '数据资产详情',
              publicPage: false
            }
          }
        ]
      },
      {
        name: 'subjectDomain',
        path: '/dataGovernance/subjectdomain',
        component: () => import('./view/subjectDomain/index.vue'),
        meta: {
          title: '主题域配置',
          publicPage: true,
          icon: 'ios-paper'
        }
      },
      {
        name: 'layered',
        path: '/dataGovernance/layered',
        component: () => import('./view/layered/index.vue'),
        meta: {
          title: '分层配置',
          publicPage: true,
          icon: 'ios-paper'
        }
      },
      {
        name: 'modifier',
        path: '/dataGovernance/modifier',
        component: () => import('./view/modifier/index.vue'),
        meta: {
          title: '修饰词管理',
          publicPage: true,
          icon: 'ios-paper'
        }
      },
      {
        name: 'statPeriod',
        path: '/dataGovernance/statPeriod',
        component: () => import('./view/statPeriod/index.vue'),
        meta: {
          title: '统计周期管理',
          publicPage: true,
          icon: 'ios-paper'
        }
      }
    ]
  }
]

export default routes
