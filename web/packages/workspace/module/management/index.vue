<template>
  <div class="workspace-home">
    <MenuSider v-model="isCollapsed">
      <div class="mune-bar">
        <h3 class="workspace-title"></h3>
        <div class="overview-box">
          <span class="overview-text" >{{$t('message.workspaceManagement.title')}}</span>
        </div>
        <Menu width="auto" :active-name="activeName" :open-names="['engineinfo']" @on-select="checkout">
          <template v-for="(item, index) in manageMenus">
            <Submenu v-if="item.children && item.children.length" :key="index" :name="item.path">
              <template #title>
                <Icon type="ios-paper" />
                {{item.name}}
              </template>
              <MenuItem class="left-menuItem" v-for="(subItem, subIndex) in item.children" :key="subIndex" :name="subItem.path">
                <SvgIcon class="menuItemIcon" :icon-class="subItem.icon"/>
                <span>{{subItem.name}}</span>
              </MenuItem>
            </Submenu>
            <MenuItem v-else class="left-menuItem" :key="index" :name="item.path">
              <SvgIcon class="menuItemIcon" :icon-class="item.icon"/>
              <span>{{item.name}}</span>
            </MenuItem>
          </template>
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
        name: i18n.t('message.workspaceManagement.baseInfo'),
        path: 'productsettings',
        children: [],
      },
      {
        icon: 'user',
        name: i18n.t('message.workspaceManagement.userInfo'),
        path: 'usertable',
        children: [],
      },
      {
        icon: 'control',
        name: i18n.t('message.workspaceManagement.authInfo'),
        path: 'jurisdiction',
        children: [],
      },
      // {
      //   icon: 'add',
      //   name: i18n.t('message.workspaceManagement.dataSourceAdministration'),
      //   path: 'dataSourceAdministration',
      //   children: [],
      // },
      {
        icon: 'engineinfo',
        name: i18n.t('message.workspaceManagement.engineInfo'),
        path: 'engineinfo',
        children: [
          {
            icon: 'engineHistory',
            name: i18n.t('message.workspaceManagement.engineSearch'),
            path: 'enginelist',
            children: [],
          },
          {
            icon: 'rulemgm',
            name: i18n.t('message.workspaceManagement.engineKill'),
            path: 'enginekill',
            children: [],
          },
          {
            icon: 'engineseach',
            name: i18n.t('message.workspaceManagement.engineKillList'),
            path: 'enginekillList',
            children: [],
          }
        ],
      },
      {
        icon: 'resTemplateManage',
        name: i18n.t('message.workspaceManagement.resTemplateManage'),
        path: 'resTemplate',
        children: [],
      },
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
