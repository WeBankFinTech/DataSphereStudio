<template>
  <div class="ds-nav-menu-wrap" :class="{'ds-nav-menu-fold': menuFold }" >
    <div class="ds-nav-menu">
      <div class="ds-nav-menu-item" :class="{'active': currentTab == '/dataService' }" @click="handleTabClick('dataService')">
        <Icon custom="iconfont icon-project" size="26"></Icon>
      </div>
      <div class="ds-nav-menu-item" :class="{'active': currentTab.startsWith('/dataManagement') }" @click="handleTabClick('dataManagement')">
        <Icon custom="iconfont icon-project" size="26"></Icon>
      </div>
    </div>
    <div class="ds-nav-panel" v-if="currentTab == '/dataService'">
      <TreeMenu />
    </div>
    <div class="ds-nav-panel" v-if="currentTab.startsWith('/dataManagement')">
      <ManageMenu />
    </div>
  </div>
</template>
<script>
import TreeMenu from './treeMenu.vue';
import ManageMenu from './manageMenu.vue';
export default {
  components: {
    TreeMenu,
    ManageMenu
  },
  props: {
    menuFold: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      currentTab: this.$route.path,
    }
  },
  methods: {
    handleTabClick(tab) {
      if (this.$route.path == `/${tab}` || this.$route.path.startsWith(`/${tab}`)) {
        this.$emit('on-menu-toggle')
      } else {
        this.$router.push({
          name: tab,
          query: this.$route.query,
        });
      }
    },
  },
}
</script>

<style lang="scss" scoped>
.ds-nav-menu-wrap {
    display: flex;
    position: fixed;
    left: 0;
    top: 54px;
    bottom: 0;
    width: 304px;
    background: #F8F9FC;
    transition: all .3s;
    &.ds-nav-menu-fold {
        width: 54px;
        .ds-nav-panel {
            transform: translateX(-304px);
        }
    }
    .ds-nav-menu {
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
        .active {
            background: #ECEFF4;
            border-left: 3px solid #2E92F7;
        }
    }
    .ds-nav-panel {
        position: absolute;
        width: 250px;
        left: 54px;
        top: 0;
        bottom: 0;
        transition: all .3s;
        overflow-y: auto;
        border-right: 1px solid #DEE4EC;
    }
}
</style>

