<template>
  <div class="layout-body" :class="{'hide-header': ismicroModule}">
    <layout-header
      v-if="!ismicroModule && !$route.query.noHeader"
      @clear-session="clearSession"/>
    <router-view/>
    <layout-footer/>
  </div>
</template>
<script>

import layoutMixin from '@dataspherestudio/shared/common/service/layoutMixin.js';
import {currentModules} from '@dataspherestudio/shared/common/util/currentModules.js';
export default {
  components: {
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