<template>
  <div>
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
.ds-main-content {
  margin-left: 304px;
  transition: margin-left .3s;
  &.ds-main-content-fold {
    margin-left: 54px;
  }
  .ds-main-container {
    background: #f3f5fc;
    .ds-breadcumb {
      padding: 24px;
      background: #fff;
      font-size: 22px;
      line-height: 30px;
      color: #333;
      .ds-breadcumb-divider {
        margin: 0 10px;
      }
    }
  }
}
</style>

