import storage from '@dataspherestudio/shared/common/helper/storage';
export default {
  data() {
    return {}
  },

  created() {
    // 刷新页面还是需要清缓存的，因为scriptis里的数结构都是缓存，如果不清，后台更新了刷新页面也看不到
    this.clearSession();
  },
  mounted() {
    document.addEventListener('copy', this.copyAction, false);
    document.addEventListener('cut', this.copyAction, false);
  },
  beforeDestroy() {
    document.removeEventListener('copy', this.copyAction, false);
    document.removeEventListener('cut', this.copyAction, false);
  },
  methods: {
    copyAction(event) {
      // 谷歌浏览器中的clipboardData对象存在event事件里，用于获取剪贴板中的数据，只有在复制操作过程中才能监听到
      const string = event.clipboardData.getData('text/plain') || event.target.value || event.target.outerText;
      storage.set('copyString', string);
    },
    clearSession() {
      storage.set('shareRootPath', '');
      storage.set('hdfsRootPath', '');
      storage.set('copyString', '');
    },
  }
}
