<template>
  <div class="layout-body" :class="{'hide-header': ismicroModule}">
    <layout-header
      v-if="!ismicroModule"
      @clear-session="clearSession"/>
    <router-view/>
    <layout-footer/>
  </div>
</template>
<script>
import headerModule from '../module/header';
import footerModule from '@/dss/module/footer';
import layoutMixin from '@/common/service/layoutMixin.js';
import {currentModules} from '@/common/util/currentModules.js';
export default {
  components: {
    layoutFooter: footerModule.component,
    layoutHeader: headerModule.component,
  },
  computed: {
    ismicroModule() {
      return currentModules().microModule;
    }
  },
  mixins: [layoutMixin]
};
</script>
<style lang="scss" scoed>
.hide-header {
  padding: 0;
}
</style>