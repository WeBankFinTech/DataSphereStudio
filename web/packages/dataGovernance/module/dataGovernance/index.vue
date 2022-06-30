<template>
  <div style="height: 100%;">
    <navMenu
      :menuFold="menuFold"
      :treeNodes="projectsTree"
      :treeCurrentId="currentTreeId"
      @on-menu-toggle="handleMenuToggle"
      @handleApiChoosed="onClickMenu"
    />
    <div
      class="ds-main-content"
      :class="{
        'ds-main-content-fold': menuFold
      }"
    >
      <div class="ds-main-container">
        <div class="ds-breadcumb" v-if="title !== '数据资产目录'">
          <span>{{ title }}</span>
        </div>
        <div class="ds-router-view">
          <router-view></router-view>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import navMenu from "../common/navMenu.vue";
// import storage from "../../utils/storage";
export default {
  components: {
    navMenu
  },
  data() {
    return {
      menuFold: false,
      projectsTree: [
        {
          id: 1,
          name: "数据资产",
          children: [
            { id: 11, name: "数据总览", pathName: "dataGovernance/overview" },
            { id: 12, name: "数据资产目录", pathName: "dataGovernance/assets" }
          ]
        },
        /*{
          id: 2,
          name: "元数据管理",
          children: [{ id: 21, name: "元数据采集" }]
        },
        {
          id: 3,
          name: "数据权限",
          children: [{ id: 31, name: "数据权限管理" }]
        },*/
        {
          id: 4,
          name: "数仓规划",
          children: [
            {
              id: 41,
              name: "主题域配置",
              pathName: "dataGovernance/subjectDomain"
            },
            { id: 42, name: "分层配置", pathName: "dataGovernance/layered" }
          ]
        },
        /*{ id: 5, name: "数据质量", children: [] },
        { id: 6, name: "数据安全", children: [] }*/
      ],
      currentTreeId: 1,
      title: this.$t("message.dataGovernance.dataOverview")
    };
  },
  computed: {
    searchScroll() {
      return this.$route.name === "dataGovernance/assets/search";
    }
  },
  mounted() {
    let pathName = this.$route.name;
    if (pathName !== "dataGovernance/overview") {
      sessionStorage.removeItem("searchTbls");
      const workspaceId = this.$route.query.workspaceId;
      this.$router.push({
        name: "dataGovernance/overview",
        query: { workspaceId }
      });
    }
    console.log("f5");
  },
  methods: {
    handleMenuToggle() {
      this.menuFold = !this.menuFold;
    },
    onClickMenu(node) {
      const { id, pathName, name } = node;
      this.currentTreeId = id;
      const workspaceId = this.$route.query.workspaceId;
      if (id > 10) {
        this.title = name;
      }
      if (pathName) {
        this.$router.push({
          name: pathName,
          query: { workspaceId }
        });
      }
    }
  }
};
</script>
<style lang="scss" scoped>
@import "@dataspherestudio/shared/common/style/variables.scss";
.ds-main-content {
  height: 100%;
  min-height: 100%;
  margin-left: 250px;
  transition: margin-left 0.3s;
  &.ds-main-content-fold {
    margin-left: 54px;
  }
  &.search-scroll {
    overflow-y: hidden;
  }
  .ds-main-container {
    min-height: 100%;
    display: flex;
    flex-direction: column;
    @include bg-color(#fff, $dark-base-color);
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
    .ds-router-view {
      flex: 1;
      display: flex;
      flex-direction: column;
    }
  }
}
</style>
