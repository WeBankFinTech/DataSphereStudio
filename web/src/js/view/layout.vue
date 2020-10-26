<template>
  <div class="layout-body">
    <layout-header
      @clear-session="clearSession"
      @set-init="setInit"/>
    <keep-alive v-if="isInit">
      <router-view
        v-if="$route.meta.keepAlive"/>
    </keep-alive>
    <router-view
      v-if="!$route.meta.keepAlive && isInit"/>
    <layout-footer v-if="isInit"/>
  </div>
</template>
<script>
import headerModule from '@js/module/header';
import footerModule from '@js/module/footer';
import storage from '@/js/helper/storage';
// import moduleMixin from '@/js/service/moduleMixin';
// let serviceMixin = moduleMixin({
//     name: 'layout',
// });
export default {
  components: {
    layoutFooter: footerModule.component,
    layoutHeader: headerModule.component,
  },
  // mixins: [serviceMixin],
  data() {
    return {
      isInit: false,
    };
  },
  created() {
    this.clearSession();
  },
  mounted() {
    document.addEventListener('copy', (event) => {
      // 谷歌浏览器中的clipboardData对象存在event事件里，用于获取剪贴板中的数据，只有在复制操作过程中才能监听到
      const string = event.clipboardData.getData('text/plain') || event.target.value || event.target.outerText;
      storage.set('copyString', string);
    }, false);
  },
  methods: {
    clearSession() {
      storage.set('scriptTree', '', 'SESSION');
      storage.set('hdfsTree', '', 'SESSION');
      storage.set('shareRootPath', '', 'SESSION');
      storage.set('hdfsRootPath', '', 'SESSION');
      storage.set('hiveTree', '', 'SESSION');
      storage.set('udfTree', '', 'SESSION');
      storage.set('functionTree', '', 'SESSION');
      storage.set('userInfo', '', 'SESSION');
      storage.set('copyString', '');
      storage.set('baseInfo', '');

      //清除cookie，防止用户之间登陆用户不一致
      var keys = document.cookie.match(/[^ =;]+(?=\=)/g);
      if(keys) {
        for(var i = keys.length; i--;)
          document.cookie = keys[i] + '=0;expires=' + new Date(0).toUTCString()
      }

    },
    setInit() {
      this.isInit = true;
    },
  },
};
</script>
