export default {
  name: 'MyResource',
  data: {
    API_PATH: process.env.VUE_APP_MN_CONFIG_PREFIX || `${window.location.protocol}//${window.location.host}${process.env.VUE_APP_PREFIX}/test/`,
  },
  component: () => import('./index.vue'),
};