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
        <Icon custom="iconfont" :class="menu.icon" size="16"></Icon>
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
        { name: 'API管理', icon: 'icon-APIguanli', path: 'dataManagement/index'},
        { name: 'API监控', icon: 'icon-APIjiankong', path: 'dataManagement/monitor'},
        { name: 'API测试', icon: 'icon-APIceshi', path: 'dataManagement/test'},
        { name: 'API调用', icon: 'icon-APItiaoyong', path: 'dataManagement/call'},
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
.manage-menu-wrap {
  background: #F8F9FC;
  .manage-title {
    padding: 10px 12px;
    font-size: 14px;
    line-height: 20px;
    color: #666;
    font-weight: bold;
    border-bottom: 1px solid #DEE4EC;
  }
  .manage-menu {
    &-item {
      padding: 0 10px;
      height: 40px;
      line-height: 40px;
      color: #666;
      cursor: pointer;
      border-bottom: 1px solid #DEE4EC;
      &:hover {
        background: #EDF1F6;
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
