<template>
  <div class="management-platform-container">
    <!-- list header -->
    <!-- <div class="management-platform-container-tapbar">
      <div class="management-platform-container-tapbar-header">
        <span>{{ header }}</span>
      </div>
    </div> -->
    <!-- list container -->
    <!-- 产品文档 -->
    <div
      class="management-platform-container-list"
      v-if="header === this.$t('message.workspace.Documents2')"
    >
      <!-- tabs -->

      <!-- appmain  -->
      <div class="management-platform-container-list-appmain">
        <access-component
          :topTapList="topTapList"
          :currentTab="currentTab"
          @bandleTapTab="tabClick"
          @handleTabRemove="tabRemove"
          @on-save="saveComponent"
        />
      </div>
    </div>

    <!-- 组件接入 -->
    <div
      class="management-platform-container-list"
      v-if="header === this.$t('message.workspace.ComponentAccess')"
    >
      <!-- tabs -->

      <!-- appmain  -->
      <div class="management-platform-container-list-appmain">
        <access-component
          :topTapList="topTapList"
          :currentTab="currentTab"
          @bandleTapTab="tabClick"
          @handleTabRemove="tabRemove"
          @on-save="saveComponent"
        />
      </div>
    </div>
    <div
      class="management-platform-container-list"
      :class="{ consoleStyle: header == this.$t('message.workspace.Console') }"
      v-else
    >
      <!-- 面包屑  管理台和组件接入模块拥有 -->
      <div
        class="management-platform-container-list-breadcrumb"
        v-if="breadcrumbName"
      >
        <Breadcrumb>
          <BreadcrumbItem :to="skipPath">{{ breadcrumbName }}</BreadcrumbItem>
          <BreadcrumbItem v-if="$route.name === 'viewHistory'">{{
            $route.query.taskID
          }}</BreadcrumbItem>
          <template v-if="$route.name === 'EngineConnList'">
            <BreadcrumbItem>{{ $route.query.instance }}</BreadcrumbItem>
            <BreadcrumbItem>EngineConnList</BreadcrumbItem>
          </template>
        </Breadcrumb>
      </div>

      <!-- appmain  -->
      <div class="management-platform-container-list-appmain">
        <router-view></router-view>
      </div>
    </div>
  </div>
</template>

<script>
import accessComponent from "../accessComponent/index.vue";
export default {
  name: "TabList",
  components: {
    "access-component": accessComponent,
  },
  props: {
    header: {
      type: String,
      default: "",
    },
    breadcrumbName: {
      type: String,
      default: "",
    },
    contentHeight: {
      type: Number,
      default: 400,
    },
    // 组件接入相关
    topTapList: {
      type: Array,
      required: true,
    },
    currentTab: {
      type: null,
    },
  },
  watch: {
    header(newVal) {
      this.header = newVal;
    },
    currentTab: {
      handler(newVal) {
        this.currentTab = newVal;
      },
      deep: true,
    },
    topTapList: {
      handler(newVal) {
        this.topTapList = newVal;
      },
      deep: true,
    },
  },
  computed: {
    skipPath() {
      let path = "";
      if (this.$route.name === "viewHistory")
        path = "/managementPlatform/globalHistory";
      if (this.$route.name === "EngineConnList")
        path = "/managementPlatform/ECM";
      return path;
    },
  },
  methods: {
    tabClick(id) {
      this.$emit("bandleTapTab", id);
    },
    tabRemove(id) {
      this.$emit("handleTabRemove", id);
    },
    saveComponent(componentItem) {
      this.$emit("on-save", componentItem);
    },
  },
};
</script>

<style lang="scss" scoped>
@import "@dataspherestudio/shared/common/style/variables.scss";
.management-platform-container {
  margin-left: 304px;
  transition: margin-left 0.3s;
  position: relative;
  height: 100%;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  @include bg-color($light-base-color, $dark-base-color);
  &.sidebar-fold {
    margin-left: 54px;
  }
  &-tapbar {
    // background-color: #fff;
    @include bg-color($light-base-color, $dark-base-color);
    &-header {
      padding: 24px 24px;
      // color: rgba(0,0,0,0.65);
      @include font-color($workspace-title-color, $dark-workspace-title-color);

      font-size: 21px;
      border-bottom: 24px solid #edf1f6;
      @include border-color($border-color-base, $dark-workspace-background);
    }
  }
  &-list {
    flex: 1;
    display: flex;
    @include bg-color($light-base-color, $dark-base-color);
    flex-direction: column;
    &-breadcrumb {
      height: 30px;
      border-bottom: 1px solid #dee4ec;
      margin-bottom: 10px;
      line-height: 30px;
      width: 100%;
      margin-left: -24px;
      padding-left: 24px;
      @include border-color($border-color-base, $dark-border-color-base);
    }
    &-appmain {
      overflow: hidden;
      height: 100%;
    }
  }
}
.consoleStyle {
  margin-left: 24px;
}
</style>
