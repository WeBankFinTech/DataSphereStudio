<template>
  <div class="layout-body layout-top">
    <layout-header
      v-if="!$route.query.noHeader"
      @clear-session="clearSession"
      ref="layoutHeader"></layout-header>
    <notice api-path="/dss/scriptis/getNotice" />
    <router-view/>
    <layout-footer ref="layoutFooter" v-if="!$route.query.noFooter"/>
  </div>
</template>
<script>
import headerModule from '../module/header';
import footerModule from '../module/footer';
import notice from '@dataspherestudio/shared/components/notice';
import layoutMixin from '@dataspherestudio/shared/common/service/layoutMixin.js';
export default {
  components: {
    layoutHeader: headerModule.component,
    layoutFooter: footerModule.component,
    notice
  },
  mixins: [layoutMixin]
};
</script>
