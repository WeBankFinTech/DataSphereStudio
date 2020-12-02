// import Vue from 'vue';
export default {
  name: 'FeedBack',
  data: {
    API_PATH: process.env.VUE_APP_MN_CONFIG_PREFIX || `${window.location.protocol}//${window.location.host}${process.env.VUE_APP_PREFIX}/operationApi/`,
  },
  component: () =>
    import('./index.vue'),
};