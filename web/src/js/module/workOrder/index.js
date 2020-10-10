export default {
  name: 'WorkOrder',
  data: {
    API_PATH: process.env.VUE_APP_MN_CONFIG_PREFIX || `https://${window.location.host}/api/rest_j/v1/`,
  },
  component: () => import('./index.vue'),
};