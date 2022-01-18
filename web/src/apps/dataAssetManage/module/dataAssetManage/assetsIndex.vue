<template>
  <div class="assets-index-wrap">
    <!-- top 面包屑  -->
    <div class="assets-index-t">
      <!-- top -->
      <div class="assets-index-t-t1">
        <slot name="top">
          <div
            class="top-l-text"
            :class="{ active: textColor }"
            :style="{ color: textColor }"
            @click="selectProject"
          >
            <span>{{ $t("message.dataAssetManage.menu") }}</span>
          </div>
          <div class="top-r-container">
            <template v-for="(work, index) in topTapList">
              <div
                :key="work.id"
                :class="{
                  active: currentTab.guid === work.guid && !textColor,
                }"
                class="tab-item"
                ref="work_item"
              >
                <we-tab
                  :index="index"
                  :work="work"
                  @on-choose="onChooseWork"
                  @on-remove="onRemoveWork"
                />
              </div>
            </template>
          </div>
        </slot>
      </div>
    </div>

    <!-- bottom  -->
    <div class="assets-index-b"
      :style="{overflow: $route.path === '/dataAssetManage/assets/search' ? 'hidden' : 'auto' }">
      <router-view></router-view>
    </div>
  </div>
</template>
<script>
import weTab from "../common/tabList/tabs.vue";
import {EventBus} from "../common/eventBus/event-bus";

export default {
  components: {
    weTab,
  },
  data() {
    return {
      textColor: true,
      index: 0,
      work: {},
      currentTab: {},
      topTapList: [],
    };
  },
  created() {
  },
  mounted() {
    EventBus.$on("on-choose-card", (model) => {
      let that = this;
      that.textColor = false;
      const topTapList = that.topTapList.slice(0);
      const {guid} = model;
      that.currentTab = model;
      if (topTapList.some((model) => model.guid === guid)) {
        return false;
      }
      topTapList.push(model);
      that.topTapList = topTapList;
    });
  },
  beforeDestroy() {
    // 销毁 eventBus
    EventBus.$off("on-choose-card");
  },
  methods: {
    // 面包屑相关
    selectProject() {
      // 返回到 目录 即搜索页面
      const workspaceId = this.$route.query.workspaceId;
      this.$router.push({
        name: "assetsSearch",
        query: {workspaceId},
      });
      this.textColor = true;
    },
    onChooseWork(modal) {
      const workspaceId = this.$route.query.workspaceId;
      const {guid} = modal;
      this.$router.push({
        name: "assetsInfo",
        params: {guid},
        query: {workspaceId},
      });
      this.currentTab = modal;
      this.textColor = false;
    },
    onRemoveWork(modal) {
      let that = this;
      let topTapList = that.topTapList.slice(0);
      let len = topTapList.length;
      let idx = 0;

      topTapList.forEach((item, index) => {
        if (item.guid === modal.guid) {
          return (idx = index);
        }
      });

      const removeAction = () => {
        if (that.currentTab.guid === modal.guid) {
          const {guid} = that.currentTab;
          const workspaceId = that.$route.query.workspaceId;
          if (len > 1 && idx < len - 1) {
            that.currentTab = topTapList[idx + 1];
            that.$router.push({
              name: "assetsInfo",
              params: {guid},
              query: {workspaceId},
            });
          } else if (len > 1 && idx == len - 1) {
            that.currentTab = topTapList[idx - 1];
            that.$router.push({
              name: "assetsInfo",
              params: {guid},
              query: {workspaceId},
            });
          } else {
            that.currentTab = {};
            that.textColor = true;
            that.$router.push({
              name: "assetsSearch",
              query: {workspaceId},
            });
          }
        }
        topTapList.splice(idx, 1);
        that.topTapList = topTapList;
      };

      removeAction();
    },
    // 通过 eventBus 获取 面包屑数据
  },
};
</script>
<style lang="scss" scoped>
@import "@/common/style/variables.scss";

.assets-index-wrap {
  height: 100%;
  max-height: 100%;
  overflow: scroll;
  display: flex;
  flex-direction: column;

  .assets-index-t {
    .assets-index-t-t1 {
      padding: 0px $padding-25;
      border-bottom: $border-width-base $border-style-base $border-color-base;
      @include border-color(
          $background-color-base,
          $dark-workspace-body-bg-color
      );
      @include font-color($workspace-title-color, $dark-workspace-title-color);
      flex: none;
      display: flex;
      align-items: center;
      font-size: $font-size-large;

      .top-l-text {
        cursor: pointer;
        flex: none;
        font-size: $font-size-large;
        padding: 0 15px;
        margin-bottom: -1px;
        line-height: 40px;
        position: relative;

        &::after {
          content: "";
          border-left: 1px solid #dee4ec;
          @include border-color($border-color-base, $dark-border-color-base);
          width: 0;
          position: absolute;
          right: -15px;
          top: 12px;
          height: 16px;
          margin: 0 15px;
        }
      }

      .active {
        border-bottom: 2px solid $primary-color;
        @include border-color($primary-color, $dark-primary-color);
      }

      .top-r-container {
        flex: 1;
        height: 40px;

        .tab-item {
          display: inline-block;
          height: 40px;
          line-height: 40px;
          color: rgba(0, 0, 0, 0.85);
          cursor: pointer;
          min-width: 100px;
          max-width: 200px;
          overflow: hidden;
          margin-right: 2px;
        }
      }
    }
  }

  .assets-index-b {
    flex: 1;
    display: flex;
    flex-direction: column;
  }
}
</style>
