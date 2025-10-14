<template>
  <div class="layout-body" :class="{ 'layout-top': !$route.query.noHeader }">
    <layout-header
      v-if="!$route.query.noHeader"
      @clear-session="clearSession"
      ref="layoutHeader"></layout-header>

    <router-view />
    <layout-footer ref="layoutFooter" v-if="!$route.query.noFooter"/>
    <newGuidance v-if="$route.path === '/workspaceHome'" ref="newGuidance"/>
  </div>
</template>
<script>
import headerModule from '../module/header';
import footerModule from '../module/footer';
import newComerGuidanceModule from '../module/newGuidance'
import layoutMixin from '@dataspherestudio/shared/common/service/layoutMixin.js';

export default {
  components: {
    layoutFooter: footerModule.component,
    layoutHeader: headerModule.component,
    newGuidance: newComerGuidanceModule.component,
  },
  data() {
    return {
    }
  },
  mixins: [layoutMixin],
  computed: {
  },
  methods: {
  },
};
</script>
