<template>
  <div class="layout-body" :class="{ 'layout-top': !$route.query.noHeader }">
    <component
      v-if="!$route.query.noHeader"
      :is="showHeader"
      @clear-session="clearSession"
      @set-init="setInit"></component>
    <keep-alive v-if="isInit">
      <router-view
        v-if="$route.meta.keepAlive"/>
    </keep-alive>
    <router-view
      v-if="!$route.meta.keepAlive"/>
    <layout-footer/>
  </div>
</template>
<script>
import headerModule from '@/dss/module/header';
import footerModule from '@/dss/module/footer';
import layoutMixin from '@/common/service/layoutMixin.js';
// 导入虚拟模块获取各个header
const apps = require('dynamic-modules');
let obj = {}
Object.entries(apps.headers).forEach((item) => {
  obj[item[0]] = item[1].default.component
})
export default {
  components: {
    layoutFooter: footerModule.component,
    layoutHeader: headerModule.component,
    ...obj
  },
  data() {
    return {
      isInit: false
    }
  },
  mixins: [layoutMixin],
  computed: {
    // 首页的header需要是变化的，可以设置为路由参数来控制
    showHeader() {
      return apps.microModule === 'apiServices'? 'apiServices' : (this.$route.query.showHeader || 'layoutHeader');
    }
  },
  methods: {
    setInit() {
      this.isInit = true;
    },
  },
};
</script>
