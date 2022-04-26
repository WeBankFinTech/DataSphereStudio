<template>
  <div class="manage-menu-wrap">
    <div class="manage-title">{{ $t("message.dataService.serviceManage") }}</div>
    <div class="manage-menu">
      <div
        v-for="menu in menuList"
        :key="menu.path"
        class="manage-menu-item" 
        :class="{'active': $route.path.startsWith(`/${menu.path}`) }" 
        @click="handleTabClick(menu.path)"
      >
        <SvgIcon :icon-class="menu.icon" />
        <span>{{menu.name}}</span>
      </div>
    </div>
  </div>
</template>
<script>
export default {
  name: 'manageMenu',
  data() {
    return {
      menuList: [
        { name: 'API管理', icon: 'api-index', path: 'dataManagement/index'},
        { name: 'API监控', icon: 'api-monitor', path: 'dataManagement/monitor'},
        { name: 'API测试', icon: 'api-test', path: 'dataManagement/test'},
        { name: 'API调用', icon: 'api-auth', path: 'dataManagement/call'},
      ]
    }
  },
  methods: {
    handleTabClick(tab) {
      this.$router.push({
        name: tab,
        query: this.$route.query,
      });
    }
  }
};
</script>
<style lang="scss" scoped>
@import "@dataspherestudio/shared/common/style/variables.scss";
.manage-menu-wrap {
  @include bg-color(#f8f9fc, $dark-base-color);
  .manage-title {
    padding: 10px 12px;
    font-size: 14px;
    line-height: 20px;
    @include font-color(#666, $dark-text-color);
    font-weight: bold;
    border-bottom: 1px solid #DEE4EC;
    @include border-color(#DEE4EC, $dark-border-color);
  }
  .manage-menu {
    &-item {
      padding: 0 10px;
      height: 40px;
      line-height: 40px;
      font-size: 14px;
      @include font-color(#666, $dark-text-color);
      cursor: pointer;
      border-bottom: 1px solid #DEE4EC;
      @include border-color(#DEE4EC, $dark-border-color);
      &:hover {
        @include bg-color(#EDF1F6, $dark-active-menu-item);
      }
      span {
        margin-left: 6px;
      }
    }
    .active {
      color: #2E92F7;
    }
  }
}
</style>
