<template>
  <div style="height: 100%">
    <navMenu :menuFold="menuFold" @on-menu-toggle="handleMenuToggle" />
    <div class="ds-main-content" :class="{ 'ds-main-content-fold': menuFold }">
      <div class="ds-main-container">
        <router-view></router-view>
      </div>
    </div>
  </div>
</template>
<script>
import navMenu from "../common/navMenu.vue";
export default {
  components: {
    navMenu,
  },
  data() {
    return {
      menuFold: false,
    };
  },
  methods: {
    handleMenuToggle() {
      this.menuFold = !this.menuFold;
    },
  },
};
</script>
<style lang="scss" scoped>
@import "@dataspherestudio/shared/common/style/variables.scss";
.ds-main-content {
  z-index: 1;
  height: 100%;
  margin-left: 304px;
  transition: margin-left 0.3s;
  ::v-deep .monitor-chart-modal {
    left: 304px;
  }
  &.ds-main-content-fold {
    margin-left: 54px;
    ::v-deep .monitor-chart-modal {
      left: 54px;
    }
  }
  .ds-main-container {
    @include bg-color(#f3f5fc, $dark-base-color);
    height: 100%;
    min-height: 100%;
    padding-top: 18px;
  }
}
</style>

