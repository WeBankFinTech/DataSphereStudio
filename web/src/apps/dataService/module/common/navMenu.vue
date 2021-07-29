<template>
  <div class="ds-nav-menu-wrap" :class="{ 'ds-nav-menu-fold': menuFold }">
    <div class="ds-nav-menu">
      <div
        class="ds-nav-menu-item"
        :class="{ active: currentTab == '/dataService' }"
        @click="handleTabClick('dataService')"
      >
        <img src="../../assets/images/develop_nav.svg" class="develop_nav" />
      </div>
      <div
        class="ds-nav-menu-item"
        :class="{ active: currentTab.startsWith('/dataManagement') }"
        @click="handleTabClick('dataManagement')"
      >
        <Icon custom="iconfont icon-project" size="26"></Icon>
      </div>
    </div>
    <div class="ds-nav-panel" v-if="currentTab == '/dataService'">
      <TreeMenu @showModal="showModal" ref="treeMenu" />
    </div>
    <div class="ds-nav-panel" v-if="currentTab.startsWith('/dataManagement')">
      <ManageMenu />
    </div>
  </div>
</template>
<script>
import ManageMenu from "./manageMenu.vue";
import TreeMenu from "./treeMenu.vue";

export default {
  name: "navMenu",
  components: {
    ManageMenu,
    TreeMenu
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
      loadingTree: false,
      projectsTree: [],
      currentTreeId: +this.$route.query.projectID, // tree中active节点,
      searchValue: 123,
      originDatas: []
    };
  },
  mounted() {},
  methods: {
    handleTabClick(tab) {
      if (
        this.$route.path == `/${tab}` ||
        this.$route.path.startsWith(`/${tab}`)
      ) {
        this.$emit("on-menu-toggle");
      } else {
        this.$router.push({
          name: tab,
          query: this.$route.query
        });
      }
    },

    showModal(pyload) {
      console.log("addApi");
      this.$emit("showModal", pyload);
    },

    treeMethod(name, payload = {}) {
      if (name === "getApi") {
        this.$refs.treeMenu.getAllApi();
      } else {
        const { id, data } = payload;
        this.$refs.treeMenu.addApi(id, data);
      }
    }
  }
};
</script>
<style lang="scss" scoped>
@import "@/common/style/variables.scss";
.ds-nav-menu-wrap {
  display: flex;
  position: fixed;
  left: 0;
  top: 54px;
  bottom: 0;
  width: 304px;
  background: #f8f9fc;
  transition: all 0.3s;
  &.ds-nav-menu-fold {
    width: 54px;
    .ds-nav-panel {
      transform: translateX(-304px);
    }
  }
  .ds-nav-menu {
    z-index: 1;
    width: 54px;
    background: #f8f9fc;
    border-right: 1px solid #dee4ec;
    &-item {
      height: 44px;
      line-height: 44px;
      text-align: center;
      cursor: pointer;
      &:hover {
        background: #eceff4;
      }
    }
    .active {
      background: #eceff4;
      border-left: 3px solid #2e92f7;
    }
  }
  .ds-nav-panel {
    position: absolute;
    width: 250px;
    left: 54px;
    top: 0;
    bottom: 0;
    transition: all 0.3s;
    padding: 10px;
    overflow-y: auto;
    border-right: 1px solid #dee4ec;
  }
}
.develop_nav {
  width: 24px;
}
</style>
