<template>
  <div class="workspace-home">
    <MenuSider v-model="isCollapsed">
      <div class="mune-bar">
        <h3 class="workspace-title"></h3>
        <div class="overview-box">
          <span class="overview-text" >{{$t('message.workspaceManagement.title')}}</span>
        </div>
        <Menu class="left-mune" width="auto" :active-name="activeName" @on-select="checkout">
          <MenuItem class="left-menuItem" v-for="(item, index) in manageMenus" :key="index" :name="item.path">
            <SvgIcon class="menuItemIcon" :icon-class="item.icon"/>
            <span>{{item.name}}</span>
          </MenuItem>
        </Menu>
      </div>
    </MenuSider>
    <div class="right">
      <router-view></router-view>
    </div>
  </div>
</template>
<script>
import MenuSider from '@dataspherestudio/shared/components/menuSider/index.vue';
import i18n from '@dataspherestudio/shared/common/i18n';

export default {
  data() {
    const manageMenus = [
      {
        icon: 'base-2',
        name: i18n.$t('message.workspaceManagement.baseInfo'),
        path: 'productsettings',
        children: [],
      },
      {
        icon: 'user',
        name: i18n.$t('message.workspaceManagement.userInfo'),
        path: 'usertable',
        children: [],
      },
      {
        icon: 'control',
        name: i18n.$t('message.workspaceManagement.authInfo'),
        path: 'jurisdiction',
        children: [],
      },
      // {
      //   icon: 'add',
      //   name: i18n.$t('message.workspaceManagement.dataSourceAdministration'),
      //   path: 'dataSourceAdministration',
      //   children: [],
      // },
    ];
    return {
      mode: '',
      activeName: '',
      manageMenus,
      isCollapsed: false
    }
  },
  components: {
    MenuSider
  },
  created() {
    if (this.$route.name) {
      this.activeName = this.$route.name
    }
  },
  methods: {
    checkout(item) {
      if (item) {
        this.$router.push({name: item, query: {
          workspaceId: this.$route.query.workspaceId
        }})
      } else {
        this.$Message.warning(this.$t('message.workspaceManagement.ZWKF'))
      }
    }
  }
}
</script>
<style lang="scss" scoped src="./index.scss"></style>
