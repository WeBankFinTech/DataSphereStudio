// import Vue from 'vue';
export default {
  name: 'FeedBack',
  /**
     * events在模块里的组件created时才加入监听者队列
     * 如果页面未加载vue，这里暴露的接口是无法被调用的
     * 可以提供跟视图相关，逻辑相关的接口
     * 命名方式是："模块名：xxx"
     */
  // events: ['FeedBack:setHighLight'],
  // dispatchs: {
  //   Workbench: ['add', 'run', 'openFile', 'remove', 'updateTab', 'checkExist', 'deleteDirOrFile'],
  //   HdfsSidebar: ['showTree'],
  //   HiveSidebar: ['showHive', 'getDatabase', 'getTables', 'getTablePartitions'],
  //   IndexedDB: ['getTabs'],
  // },
  data: {
    API_PATH: process.env.VUE_APP_MN_CONFIG_PREFIX || `${window.location.protocol}//${window.location.host}/operationApi/`,
    // API_PATH: process.env.VUE_APP_MN_CONFIG_PREFIX || `${window.location.protocol}//${window.location.host}/testApi/`,
  },
  /**
     * methods在js被加载时就会加入监听者队列x
     * 如果页面未加载vue，这里暴露的接口可以被调用
     * 提供一些跟vue无关的东西，大多是数据
     */
  // methods: {
  //   showTree(cb) {
  //     console.log(cb);
  //     // get tree
  //     // const WorkSpace = Vue.extend(require('./index.vue').default);
  //     // const newW = new WorkSpace();
  //     // cb(newW);
  //   },
  // },
  component: () =>
    import('./index.vue'),
};