export default {
  name: 'WorkspaceHome',
  events: ['WorkspaceHome:updateSidebar'],
  dispatchs: {
    WorkspaceHome: ['updateSidebar']
  },
  data: {
    API_PATH: process.env.VUE_APP_MN_CONFIG_PREFIX || `${location.protocol}//${window.location.host}/api/rest_j/v1/`,
  },
  component: () => import('./index.vue'),
};
