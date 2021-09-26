<template>
  <div style="height: 100%;">
    <navMenu :menuFold="menuFold" @on-menu-toggle="handleMenuToggle" />
    <div class="ds-main-content" :class="{'ds-main-content-fold': menuFold }">
      <div class="ds-main-container">
        <div class="ds-breadcumb">
          <span>{{$t("message.dataService.dataService")}}</span>
          <span class="ds-breadcumb-divider">/</span>
          <span>{{$t("message.dataService.serviceManage")}}</span>
        </div>
        <router-view></router-view>
      </div>
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
@import "@/common/style/variables.scss";
.ds-main-content {
  height: 100%;
  margin-left: 304px;
  transition: margin-left .3s;
  /deep/ .monitor-chart-modal {
    left: 304px;
  }
  &.ds-main-content-fold {
    margin-left: 54px;
    /deep/ .monitor-chart-modal {
      left: 54px;
    }
  }
  .ds-main-container {
    @include bg-color(#f3f5fc, $dark-base-color);
    min-height: 100%;
    .ds-breadcumb {
      padding: 24px;
      @include bg-color(#fff, $dark-base-color);
      font-size: 22px;
      line-height: 30px;
      @include font-color(#333, $dark-text-color);
      .ds-breadcumb-divider {
        margin: 0 10px;
      }
    }
  }
}
</style>

