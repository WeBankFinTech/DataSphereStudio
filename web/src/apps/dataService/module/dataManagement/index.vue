<template>
  <div>
    <navMenu :menuFold="menuFold" @on-menu-toggle="handleMenuToggle" />
    <div class="ds-main-content" :class="{'ds-main-content-fold': menuFold }">
      <router-view></router-view>
    </div>
  </div>
</template>
<script>
import navMenu from '../common/navMenu.vue';
export default {
  components: {
    navMenu,
  },
  data() {
    return {
      menuFold: false
    }
  },
  methods: {
    handleMenuToggle() {
      this.menuFold = !this.menuFold;
    },
  },
}
</script>
<style lang="scss" scoped>
.ds-main-content {
  margin-left: 304px;
  transition: margin-left .3s;
  &.ds-main-content-fold {
    margin-left: 54px;
  }
}
</style>

