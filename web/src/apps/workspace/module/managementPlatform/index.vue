<template>
  <div class="management-platform-wrap">

    <!-- tree  侧边栏 -->
    <div class="management-platform-sidebar" :class="{'sidebar-fold': sidebarFold}" >
      <div class="management-platform-sidebar-menu">
        <div class="management-platform-sidebar-menu-item active" v-for="item in menu" :key="item.title" @click="handleSidebarToggle(item.title)">
          <Icon :custom="`iconfont icon-${item.icon}`" size="26"></Icon>
        </div>
      </div>
      <div class="management-platform-sidebar-tree">
        <span class="management-platform-sidebar-tree-header">
          {{ defaultMenu.title }}
        </span>
        <Tree
          class="management-platform-sidebar-tree-container"
          :nodes='defaultMenu.nodes'
          :currentTreeId = this.currentTreeId
          @on-item-click="handleTreeClick" 
          @on-add-click="handleTreeComponent" 
        />
        <Spin v-show="loadingSidebar" size="large" fix/>
      </div>
    </div>

    <!-- tablist -->
    <!-- <div class="management-platform-tablist" >
      <div class="management-platform-tablist-header">
        <div class="management-platform-tablist-header-info">
          {{ $t("message.workspace.permissions") }}
        </div>
      </div>
      <component v-bind:is='choosedComponent'></component>
    </div> -->

  </div>
</template>

<script>
import Tree from './component/tree/tree.vue';
// import i18n from "@/common/i18n";
// import departManagement from '../../module/permissions/module/departManagement';
// import personManagement from '../../module/permissions/module/personManagement.vue';
// const permissionsMenus = [
//   {
//     title: i18n.t("message.permissions.departManagement"),
//     name: "departManagement"
//   },
//   {
//     title: i18n.t("message.permissions.personManagement"),
//     name: "personManagement"
//   }
// ];
const menu = [
  {
    title: '部门和用户管理',
    icon: 'bumenheyonghuguanli',
    nodes: [
      {name: '部门管理', type: 'permissions', id: 1024},
      {name: '用户管理', type: 'permissions', id: 1023}
    ]
  },
  {
    title: '控制台',
    icon: 'kongzhitai',
    nodes: [
      {name: '全局历史', type: 'console', id: 1022},
      {name: '资源管理', type: 'console', id: 1021},
      {name: '参数配置', type: 'console', id: 1020},
      {name: '全局变量', type: 'console', id: 1019}
    ]
  },
  {
    title: '组件接入',
    icon: 'zujianjieruguanli',
    nodes: [
      {name: '数据接入', type: 'component', id: 1018, children: []},
      {name: '数据开发', type: 'component', id: 1017, children: []},
      {name: '数据治理', type: 'component', id: 1016, children: []},
    ]
  }
];
export default {
  components: {
    Tree,
  },
  data() {
    return {
      sidebarFold: false,
      loadingSidebar: false,
      menu,
      defaultMenu: menu[0],
      currentTreeId: 0,

      treeMenu: ['permission'],

      // permissionsMenus,
      choosed: "departManagement",
      choosedComponent: "departManagement"
    }
  },
  methods: {
    handleSidebarToggle(title) {
      if( title === this.defaultMenu.title ) {
        this.sidebarFold = !this.sidebarFold
      } else {
        this.defaultMenu = this.menu.find( item => item.title === title )
        this.sidebarFold = false 
      }
    },
    changePermission(item) {
      this.choosed = item.name;
      this.choosedComponent = item.name;
    },
    handleTreeClick(node) {
      console.log('node',node)
      this.currentTreeId = node.id;
    },
    handleTreeComponent() {
      console.log('handleTreeComponent')
    }
  }
}
</script>

<style lang="scss" scoped>
@import '@/common/style/variables.scss';

$permissions-Backgroud: #F4F7FB;
$per-border-color:#DEE4EC;
.management-platform-wrap{
  display: flex;
  min-height: 100%;
  .management-platform-sidebar {
  display: flex;
  position: fixed;
  left: 0;
  top: 54px;
  bottom: 0;
  width: 304px;
  background: #F8F9FC;
  transition: all .3s;
  &.sidebar-fold {
    width: 54px;
    .management-platform-sidebar-tree {
       transform: translateX(-1304px);
    }
  }
  .management-platform-sidebar-menu {
    z-index: 1;
    width: 54px;
    background: #F8F9FC;
    border-right: 1px solid #DEE4EC;
    &-item {
      height: 44px;
      line-height: 44px;
      text-align: center;
      cursor: pointer;
      &:hover {
        background: #ECEFF4;
      }
    }
  }
  .management-platform-sidebar-tree {
    position: absolute;
    width: 250px;
    left: 54px;
    top: 0px;
    bottom: 0px;
    transition: all .3s;
    padding: 8px 12px;
    overflow-y: auto;
    border-right: 1px solid #DEE4EC;
    &-header {
      font-size: 14px;
      font-weight: bold;
      font-family: PingFangSC-Medium;
      color: rgba(0,0,0,0.65);
      line-height: 34px;
      white-space: nowrap;
      text-overflow: ellipsis;
    }
  }
  }
  .management-platform-tablist {
    flex: 1;
    background-color: #fff;
    &-header {
      padding: 0px $padding-25 0;
      background: $body-background;
      border-bottom: $border-width-base  $border-style-base $border-color-base;
      &-info {
        padding: 25px 0px 25px 0px;
        display: flex;
        justify-content: flex-start;
        align-items: center;
        font-size: 21px;
        color: $text-title-color;
        font-family: PingFangSC-Medium;
      }
    }
  }
}

</style>
