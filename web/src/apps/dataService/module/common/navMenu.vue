<template>
  <div class="ds-nav-menu-wrap" :class="{ 'ds-nav-menu-fold': menuFold }">
    <div class="ds-nav-menu">
      <div
        class="ds-nav-menu-item"
        :class="{ active: currentTab == '/dataService' }"
        @click="handleTabClick('dataService')"
      >
        <SvgIcon class="nav-icon" icon-class="data-develop" />
      </div>
      <div
        class="ds-nav-menu-item"
        :class="{ active: currentTab.startsWith('/dataManagement') }"
        @click="handleTabClick('dataManagement')"
      >
        <SvgIcon class="nav-icon" icon-class="project-workflow" />
      </div>
    </div>
    <div class="ds-nav-panel ds-nav-panel-develop" v-if="currentTab == '/dataService'">
      <TreeMenu
        @showModal="showModal"
        @handleApiChoosed="handleApiChoosed"
        ref="treeMenu"
      />
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
      this.$emit("showModal", pyload);
    },
    handleApiChoosed(pyload) {
      this.$emit("handleApiChoosed", pyload);
    },

    treeMethod(name, payload = {}) {
      if (name === "getApi") {
        this.$refs.treeMenu.getAllApi("update", payload);
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
  @include bg-color(#f8f9fc, $dark-base-color);
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
    @include bg-color(#f8f9fc, $dark-base-color);
    border-right: 1px solid #dee4ec;
    @include border-color(#dee4ec, $dark-menu-base-color);
    &-item {
      height: 44px;
      line-height: 44px;
      text-align: center;
      cursor: pointer;
      font-size: 26px;
      @include font-color(#333, $dark-text-color);
      &:hover {
        @include bg-color(#eceff4, $dark-menu-base-color);
      }
    }
    .active {
      @include bg-color(#eceff4, $dark-menu-base-color);
      border-left: 3px solid #2e92f7;
      @include border-color(#2e92f7, #4B8FF3);
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
    @include border-color(#dee4ec, $dark-menu-base-color);
    @include bg-color(#f8f9fc, $dark-menu-base-color);
  }
  .ds-nav-panel-develop{
    padding-left: 0;
    padding-right: 0;
  }
}
.develop_nav {
  width: 24px;
}
</style>
