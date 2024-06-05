

// import index from './index.vue';
export default {
  // 模块名称
  name: 'Streamis',
  // 规范模块监测什么事件，或者说模块对外提供什么接口
  events: [], // Demo:add
  // 规范模块能够触发其他模块什么事件或者说调用其他模块什么接口
  dispatchs: {},
  // 规范模块的动作，由外部调用或者自己执行
  methods: {},
  data: {
    API_PATH: process.env.VUE_APP_MN_CONFIG_PREFIX || `${location.protocol}//${window.location.host}/api/rest_j/v1/`,
  },
  component: () => import('./index.vue'),
};
