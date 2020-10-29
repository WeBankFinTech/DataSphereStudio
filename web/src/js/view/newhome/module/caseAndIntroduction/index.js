export default {
  name: 'CaseAndIntroduction',
  events: [],
  dispatchs: {
  },
  data: {
    API_PATH: process.env.VUE_APP_MN_CONFIG_PREFIX || `${window.location.protocol}//${window.location.host}${process.env.VUE_APP_PREFIX}/api/rest_j/v1/`,
  },
  component: () => import('./index.vue'),
};